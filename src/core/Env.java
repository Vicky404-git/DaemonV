package core;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Env {

    private static final Map<String, String> ENV = new HashMap<>();

    public static void load() {
        try {
            String home = System.getProperty("user.home");

            Files.readAllLines(Paths.get(home + "/.daemonv/.env"))
                .forEach(line -> {
                    if (line.contains("=") && !line.trim().startsWith("#")) {

                        String[] parts = line.split("=", 2);

                        ENV.put(parts[0].trim(), parts[1].trim());
                    }
                });

        } catch (Exception ignored) {}
    }

    public static String get(String key) {
        return ENV.getOrDefault(key, System.getenv(key));
    }
}
