package engine;

import core.EnvLoader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class MessageEngine {

    private final String apiKey;
    private final HttpClient httpClient;

    public MessageEngine() {
        // Load the key from the EnvLoader
        this.apiKey = EnvLoader.get("GROQ_API_KEY");
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    public String generate(int hour, long idleMinutes) {
        if (apiKey == null || apiKey.isEmpty()) {
            return fallbackGenerate(hour, idleMinutes);
        }

        // The prompt dictates the "mood" of the daemon
        String prompt = String.format(
            "You are a quiet, observant background process. It is currently %d:00. " +
            "The user has been idle for %d minutes. " +
            "Generate a single, short, atmospheric sentence about the passage of time or presence. " +
            "Do not use quotes. Do not offer help. Do not act like an AI assistant.", 
            hour, idleMinutes
        );

        String requestBody = String.format(
            "{\"model\": \"llama3-8b-8192\", \"messages\": [{\"role\": \"user\", \"content\": \"%s\"}], \"temperature\": 0.7, \"max_tokens\": 40}",
            prompt.replace("\"", "\\\"")
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
                System.err.println("[Groq Error] HTTP " + response.statusCode());
                return fallbackGenerate(hour, idleMinutes);
            }
        } catch (Exception e) {
            System.err.println("[API Connection Error] " + e.getMessage());
            return fallbackGenerate(hour, idleMinutes);
        }
    }

    // A lightweight way to extract the message without a JSON library
    private String extractContent(String json) {
        String target = "\"content\":\"";
        int start = json.indexOf(target);
        if (start == -1) return fallbackGenerate(0, 0);
        
        start += target.length();
        int end = json.indexOf("\"", start);
        
        String content = json.substring(start, end);
        return content.replace("\\n", " ").replace("\\\"", "\"");
    }

    private String fallbackGenerate(int hour, long idleMinutes) {
        return "Time is a flat circle, and I am watching the loop.";
    }
}