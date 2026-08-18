package engine;

import core.Env;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.DayOfWeek;
import java.time.Duration;

public class MemoryManager {

    private static final int THRESHOLD = 100;
    private static volatile boolean upgradeInProgress = false;
    
    private static final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10)).build();
    private static final String apiKey = Env.get("GROQ_API_KEY");

    public static void checkAndSchedule(int rowCount) {
    if (rowCount < THRESHOLD) return;
    if (upgradeInProgress) return;

    DayOfWeek day = LocalDate.now().getDayOfWeek();
    boolean isWeekend = (day == DayOfWeek.FRIDAY || day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY);
    if (!isWeekend) return;

    upgradeInProgress = true;
    new Thread(() -> {
        try {
            runUpgrade();
        } finally {
            upgradeInProgress = false;
        }
    }).start();
}
private static String readLastRows(int maxRows) {
    try {
        Path p = Paths.get(Env.CSV_DIR + File.separator + "events.csv");
        if (!Files.exists(p)) return "";
        java.util.List<String> lines = Files.readAllLines(p);
        int from = Math.max(1, lines.size() - maxRows); // skip header
        return String.join("\n", lines.subList(from, lines.size()));
    } catch (Exception e) { return ""; }
}

private static String readFile(String path) {
    try {
        Path p = Paths.get(path);
        if (!Files.exists(p)) return "";
        return Files.readString(p);
    } catch (Exception e) { return ""; }
}
private static String groqCall(String prompt) {
    if (apiKey == null || apiKey.isEmpty()) return "";
    String body = String.format(
        "{\"model\": \"llama-3.1-8b-instant\", \"messages\": [{\"role\": \"user\", \"content\": \"%s\"}], \"temperature\": 0.3, \"max_tokens\": 600}",
        prompt.replace("\"", "\\\"").replace("\n", " ")
    );
    try {
        HttpRequest req = HttpRequest.newBuilder(URI.create("https://api.groq.com/openai/v1/chat/completions"))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body)).build();
        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() == 200) {
            java.util.regex.Matcher m = java.util.regex.Pattern
                    .compile("\"content\"\\s*:\\s*\"(.*?)\"").matcher(res.body());
            if (m.find()) return m.group(1).replace("\\n", "\n").replace("\\\"", "\"");
        }
    } catch (IOException | InterruptedException ignored) {}
    return "";
}
public static void runUpgrade() {
    try {
        String csvData  = readLastRows(300);
        String notes    = readFile(Env.MEM_DIR + File.separator + "notes.txt");
        String existing = readFile(Env.MEM_DIR + File.separator + "memory.md");

        if (csvData.isEmpty()) {
            logUpgrade("Upgrade aborted: no CSV data.");
            return;
        }

        String prompt =
            "You are a memory summarizer for a desktop daemon. " +
            "Based on the following data, write a dense, factual summary of this user's behavior patterns, " +
            "work context, and any pinned notes. Be concise. No headers. No bullet points. Plain paragraph only. " +
            "Under 300 words.\n\n" +
            "PINNED NOTES:\n" + (notes.isEmpty() ? "none" : notes) + "\n\n" +
            "PREVIOUS MEMORY:\n" + (existing.isEmpty() ? "none" : existing) + "\n\n" +
            "RECENT EVENTS (CSV):\n" + csvData;

        String draft = groqCall(prompt);

        if (draft.isEmpty()) {
            logUpgrade("Upgrade aborted: Groq returned empty summary.");
            return;
        }

        Path draftPath = Paths.get(Env.MEM_DIR + File.separator + "memory_draft.md");
        new File(Env.MEM_DIR).mkdirs();
        Files.writeString(draftPath, draft, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        validate(draftPath, draft, false);

    } catch (Exception e) {
        logUpgrade("Upgrade failed: " + e.getMessage());
    }
}
private static void validate(Path draftPath, String draft, boolean isRetry) {
    String prompt =
        "You are a quality checker for an AI memory file. " +
        "Read the following summary and reply with only 'YES' if it is coherent, factual, and free of hallucinations. " +
        "If there are any issues, reply with only 'NO'.\n\n" +
        "SUMMARY:\n" + draft;

    String response = groqCall(prompt).trim().toUpperCase();
    boolean passed = response.startsWith("YES");

    if (passed) {
        try {
            Path memPath = Paths.get(Env.MEM_DIR + File.separator + "memory.md");
            Files.move(draftPath, memPath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            logUpgrade("Upgrade successful at " + LocalDateTime.now());
        } catch (Exception e) {
            logUpgrade("Atomic replace failed: " + e.getMessage());
            deleteDraft(draftPath);
        }
        return;
    }

    if (!isRetry) {
        logUpgrade("Validation failed, retrying once...");
        validate(draftPath, draft, true);
        return;
    }

    // Both attempts failed
    logUpgrade("Upgrade aborted after retry: validation rejected summary.");
    deleteDraft(draftPath);
}
private static void logUpgrade(String msg) {
    try {
        String line = LocalDateTime.now() + " | " + msg + "\n";
        Files.writeString(
            Paths.get(Env.MEM_DIR + File.separator + "upgrade.log"),
            line,
            StandardOpenOption.CREATE,
            StandardOpenOption.APPEND
        );
        System.out.println("[MemoryManager] " + msg);
    } catch (Exception ignored) {}
}

private static void deleteDraft(Path draftPath) {
    try { Files.deleteIfExists(draftPath); } catch (Exception ignored) {}
}
}
