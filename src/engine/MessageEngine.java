package engine;

import core.EnvLoader;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Random;

public class MessageEngine {

    private final String apiKey;
    private final HttpClient httpClient;
    private final Random random = new Random();

    public MessageEngine() {
        this.apiKey = EnvLoader.get("GROQ_API_KEY");
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    public String generate(int hour, long idleMinutes) {
        if (apiKey == null || apiKey.isEmpty()) {
            return fallbackGenerate(hour, idleMinutes);
        }

        String prompt = String.format(
            "You are a quiet, observant background process. It is currently %d:00. " +
            "The user has been idle for %d minutes. " +
            "Generate a single, short, atmospheric sentence about the passage of time or presence. " +
            "Do not use quotes. Do not offer help. Do not act like an AI assistant.", 
            hour, idleMinutes
        );

        String requestBody = String.format(
            "{\"model\": \"llama-3.1-8b-instant\", \"messages\": [{\"role\": \"user\", \"content\": \"%s\"}], \"temperature\": 0.7, \"max_tokens\": 60}",
            prompt.replace("\"", "\\\"").replace("\n", " ")
        );

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.groq.com/openai/v1/chat/completions"))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return extractContent(response.body());
            } else {
                System.err.println("[Groq Error] HTTP " + response.statusCode() + "\nDetails: " + response.body());
                return fallbackGenerate(hour, idleMinutes);
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("[API Connection Error] " + e.getMessage());
            return fallbackGenerate(hour, idleMinutes);
        }
    }

    private String extractContent(String json) {
        String target = "\"content\":\"";
        int start = json.indexOf(target);
        if (start == -1) return fallbackGenerate(0, 0);
        
        start += target.length();
        int end = json.indexOf("\"", start);
        if (end == -1) return fallbackGenerate(0, 0);
        
        String content = json.substring(start, end);
        return content.replace("\\n", " ").replace("\\\"", "\"");
    }

    private String fallbackGenerate(int hour, long idleMinutes) {
        if (idleMinutes > 120) {
            return "The machine has been still for two hours. The silence is heavy.";
        }

        if (hour >= 0 && hour < 5) {
            String[] nightThoughts = {
                "The world is asleep. Your screen is the only sun.",
                "Late hours bring honest thoughts.",
                "The background noise of the universe is louder at this hour."
            };
            return nightThoughts[random.nextInt(nightThoughts.length)];
        }

        String[] generalThoughts = {
            "Time is a flat circle, and I am watching the loop.",
            "The clock doesn't tick; it counts down.",
            "Another interval has passed into the void.",
            "You are working. I am existing. We are both here."
        };

        return generalThoughts[random.nextInt(generalThoughts.length)];
    }
}