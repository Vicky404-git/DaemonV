package engine;

import core.Env;
import java.net.URI;
import java.net.http.*;
import java.time.Duration;
import java.util.Random;

public class MessageEngine {
    private final String apiKey = Env.get("GROQ_API_KEY");
    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
    private final Random random = new Random();

    public String generate(int hour, long idleMinutes, String activeWindow) {
        if (apiKey == null || apiKey.isEmpty()) return fallback(hour, idleMinutes);

        String windowCtx = activeWindow.equals("Unknown") ? "" : " They are currently looking at an app/window titled: '" + activeWindow + "'.";
        
        String prompt = String.format(
            "You are a quiet, observant background process. It is currently %d:00. " +
            "The user has been idle for %d minutes.%s " +
            "Generate a single, short, atmospheric sentence about time, presence, or their current context. " +
            "Do not use quotes. Do not offer help. Do not act like an AI.", 
            hour, idleMinutes, windowCtx
        );

        String body = String.format("{\"model\": \"llama-3.1-8b-instant\", \"messages\": [{\"role\": \"user\", \"content\": \"%s\"}], \"temperature\": 0.7, \"max_tokens\": 60}", 
                                    prompt.replace("\"", "\\\"").replace("\n", " "));

        try {
            HttpRequest req = HttpRequest.newBuilder(URI.create("https://api.groq.com/openai/v1/chat/completions"))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body)).build();

            HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
            if (res.statusCode() == 200) {
                int start = res.body().indexOf("\"content\":\"") + 11;
                int end = res.body().indexOf("\"", start);
                return res.body().substring(start, end).replace("\\n", " ").replace("\\\"", "\"");
            }
        } catch (Exception ignored) {}
        return fallback(hour, idleMinutes);
    }

    private String fallback(int h, long idle) {
        if (idle > 120) return "The machine has been still for two hours.";
        if (h >= 0 && h < 5) return "The world is asleep. Your screen is the only sun.";
        return "Time is a flat circle, and I am watching the loop.";
    }
}