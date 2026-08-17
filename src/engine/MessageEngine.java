package engine;

import core.Env;
import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.time.Duration;

public class MessageEngine {

  private final String apiKey = Env.get("GROQ_API_KEY");
  private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();


  public String generate(int hour, long idleMinutes, String activeWindow, boolean isAudio, String state) {

    if (apiKey == null || apiKey.isEmpty()) return fallback(hour, idleMinutes);
    String memCtx = Env.getMemoryContext();

    String windowCtx = activeWindow.equals("Unknown") ? "" : " The active window is '" + activeWindow + "'.";
    String audioCtx = isAudio ? " Audio is currently playing." : " It is completely silent.";

    String personalityInstructions = "";

    switch(state) {
      case "DISTRACTED":
        personalityInstructions = "You are a sarcastic, highly judgmental AI. Roast the user ruthlessly for procrastinating and wasting their potential on this specific app. Keep it strictly under 15 words.";
        break;
      case "IDLE":
        personalityInstructions = "You are a deeply philosophical AI. Ponder the nature of time, stillness, or the void of an unused machine. Be poetic, slightly haunting, and profound. Keep it strictly under 15 words.";
        break;
      case "FOCUSED":
        personalityInstructions = "You are a stoic, quiet observer. Acknowledge the user's deep focus and grind. Provide a brief, atmospheric observation of their terminal or code. Keep it strictly under 15 words.";
        break;
      case "PASSIVE":
        personalityInstructions = "You are a chill, observant AI. Comment on the vibe of them passively consuming media, browsing, or listening to audio. Keep it strictly under 15 words.";
        break;
      default:
        personalityInstructions = "Generate a single, short, atmospheric sentence about their current digital context. Do not use quotes.";
    }

    String memPrefix = memCtx.isEmpty() ? "" : "User context: " + memCtx + "\n";
    String prompt = memPrefix + String.format(
        "It is currently %d:00. The user has been idle for %d minutes.%s%s %s Do not act like an AI assistant. Do not offer help.",
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
                // Native, dependency-free regex parsing for safety
                java.util.regex.Matcher m = java.util.regex.Pattern.compile("\"content\"\\s*:\\s*\"(.*?)\"").matcher(res.body());
                if (m.find()) {
                    return m.group(1).replace("\\n", " ").replace("\\\"", "\"");
                }
            }
        } catch (IOException | InterruptedException ignored) {} // <-- Missing bracket was added here
        
        return fallback(hour, idleMinutes);
    }

    private String fallback(int h, long idle) {
        if (idle > 120) return "The machine has been still for two hours.";
        if (h >= 0 && h < 5) return "The world is asleep. Your screen is the only sun.";
        return "Time is a flat circle, and I am watching the loop.";
    }
}
