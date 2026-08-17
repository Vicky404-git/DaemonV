package core;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.io.File;


public class Env {

  public static final String BASE_DIR  = System.getProperty("user.home") + File.separator + "DaemonV";
  public static final String CSV_DIR   = BASE_DIR + File.separator + "CSVs";
  public static final String MEM_DIR   = BASE_DIR + File.separator + "Memory";

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

  public static String getMemoryContext() {
    try {
      java.nio.file.Path p = java.nio.file.Paths.get(MEM_DIR + File.separator + "memory.md");
      if (!java.nio.file.Files.exists(p)) return "";
      String content = java.nio.file.Files.readString(p);
      return content.length() > 500 ? content.substring(0, 500) : content;
    } catch (Exception e) { return ""; }
  }

}
