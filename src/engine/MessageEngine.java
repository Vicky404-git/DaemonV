package engine;

import core.Env;
import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.time.Duration;

public class MessageEngine {
    private final String apiKey = Env.get("GROQ_API_KEY");
    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();

    // ADDED: isAudio parameter
    public String generate(int hour, long idleMinutes, String activeWindow, boolean isAudio, String state) {
        if (apiKey == null || apiKey.isEmpty()) return fallback(hour, idleMinutes);

        String windowCtx = activeWindow.equals("Unknown") ? "" : " The active window is '" + activeWindow + "'.";
        String audioCtx = isAudio ? " Audio is playing." : " It is silent.";
        
        // Dynamic Personality Routing based on Behavior State
        String personalityInstructions = "";
        
        switch(state) {
            case "DISTRACTED":
                // The Roast
                personalityInstructions = "You are a sarcastic, slightly judgmental AI. Roast the user lightly for procrastinating or wasting time on this specific app. Keep it under 15 words.";
                break;
            case "IDLE":
                // The Philosophy
                personalityInstructions = "You are a deeply philosophical AI. Ponder the nature of time, stillness, or the void of an unused machine. Be poetic and slightly haunting. Keep it under 15 words.";
                break;
            case "FOCUSED":
                // The Vibe / Motivation
                personalityInstructions = "You are a stoic, quiet observer. Acknowledge the user's deep focus and work. Provide a brief, atmospheric observation. Keep it under 15 words.";
                break;
            default:
                // Passive / General
                personalityInstructions = "Generate a single, short, atmospheric sentence about their current digital context. Do not use quotes.";
        }

        String prompt = String.format(
            "It is currently %d:00. The user has been idle for %d minutes.%s%s %s Do not act like a helpful assistant.",
            hour, idleMinutes, windowCtx, audioCtx, personalityInstructions
        );

        String body = String.format("{\"model\": \"llama-3.1-8b-instant\", \"messages\": [{\"role\": \"user\", \"content\": \"%s\"}], \"temperature\": 0.8, \"max_tokens\": 40}", 
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
        } catch (IOException | InterruptedException ignored) {}
        return fallback(hour, idleMinutes);
    }

    private String fallback(int h, long idle) {
        if (idle > 120) return "The machine has been still for two hours.";
        if (h >= 0 && h < 5) return "The world is asleep. Your screen is the only sun.";
        return "Time is a flat circle, and I am watching the loop.";
    }
}
