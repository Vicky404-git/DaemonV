package core;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class EnvLoader {

    private static final Map<String, String> envVars = new HashMap<>();

    public static void load() {
        try {
            Files.readAllLines(Paths.get(".env")).forEach(line -> {
                // Ignore comments and empty lines
                if (line.contains("=") && !line.trim().startsWith("#")) {
                    String[] parts = line.split("=", 2);
                    envVars.put(parts[0].trim(), parts[1].trim());
                }
            });
        } catch (Exception e) {
            System.out.println("[System] No .env file found. Falling back to system environment variables.");
        }
    }

    public static String get(String key) {
        return envVars.getOrDefault(key, System.getenv(key));
    }
}