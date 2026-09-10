package core;


import engine.BehaviorEngine;
import engine.MessageEngine;
import java.time.LocalTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import logging.EventLogger;
import monitor.SystemMonitor;

import java.io.InputStream;
import java.net.URI;
import java.net.http.*;
import java.nio.file.*;
import java.time.Duration;

public class Daemon {

    private volatile boolean silentEnabled = false;
    private volatile long silentUntilEpoch = 0;
    private boolean silentLogged = false;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private volatile long lastTriggerEpoch = System.currentTimeMillis();

    private volatile int scheduleStartHour = 22;
    private volatile int scheduleEndHour = 7;

    private volatile long cooldownMillis = 2 * 60 * 60 * 1000L;
    private volatile long checkSleepMillis = 5 * 60 * 1000L;

    private volatile boolean ignoreSilentWindow = false;

    private final MessageEngine ai = new MessageEngine();
    private volatile boolean running = true;

    public void setDebugMode(long intervalSeconds, long checkMillis) {
        this.cooldownMillis = intervalSeconds * 1000L;
        this.checkSleepMillis = checkMillis;
        this.ignoreSilentWindow = true;
    }

    public void enableSilent(int minutes) {
        this.silentEnabled = true;
        this.silentUntilEpoch = System.currentTimeMillis() + (minutes * 60000L);
    }

    public void setSchedule(int start, int end) {
        this.scheduleStartHour = start;
        this.scheduleEndHour = end;
    }

    public void setInterval(long seconds) {
        this.cooldownMillis = seconds * 1000L;
    }

    // Instantly bypasses sleep to fire the AI
    public void forceTrigger() {
        lastTriggerEpoch = System.currentTimeMillis(); 
        
        executor.submit(() -> {
            long idle = SystemMonitor.getIdleMinutes();
            String window = SystemMonitor.getActiveWindow();
            boolean isAudio = SystemMonitor.isAudioPlaying();
            String state = BehaviorEngine.classify(idle, window, isAudio);
            
            String msg = ai.generate(
                    LocalTime.now().getHour(),
                    idle,
                    window,
                    isAudio,
                    state
            );
            
            EventLogger.notifyAndLog(msg, idle, window, false, "Manual Trigger");
        });
    }

    public boolean isSilentNow() {
        if (silentEnabled && System.currentTimeMillis() < silentUntilEpoch) return true;
        if (silentEnabled && System.currentTimeMillis() >= silentUntilEpoch) silentEnabled = false;
        if (ignoreSilentWindow) return false;

        int h = LocalTime.now().getHour();
        // FIXED: Handles both overnight (22 -> 7) and same-day (9 -> 17) ranges gracefully
        if (scheduleStartHour > scheduleEndHour) {
            return h >= scheduleStartHour || h < scheduleEndHour;
        } else {
            return h >= scheduleStartHour && h < scheduleEndHour;
        }
    }

    // Graceful shutdown for systemd
    public void stop() {
        System.out.println("Stopping DaemonV safely...");
        running = false;
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, java.util.concurrent.TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
    }

    public void checkForUpdates() {
      new Thread(() -> {
        try {
          HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(8)).build();
          HttpRequest req = HttpRequest.newBuilder(
              URI.create("https://api.github.com/repos/Vicky404-git/DaemonV/releases/latest"))
            .header("Accept", "application/vnd.github+json")
            .GET().build();

          HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
          if (res.statusCode() != 200) return;

          // Extract tag_name
          java.util.regex.Matcher m = java.util.regex.Pattern
            .compile("\"tag_name\"\\s*:\\s*\"([^\"]+)\"").matcher(res.body());
          if (!m.find()) return;
          String latest = m.group(1).replace("v", "").trim();
          String current = Env.VERSION;

          if (!isNewer(latest, current)) return;

          // Notify user
          EventLogger.notifyAndLog(
              "DaemonV v" + latest + " is available! Use --menu > Update Settings to update.",
              0, "DaemonV", false, "Update Available"
              );

          // Auto-update if enabled
          if ("true".equalsIgnoreCase(Env.get("AUTO_UPDATE"))) {
            downloadUpdate(res.body(), latest);
          }

        } catch (Exception ignored) {}
      }).start();

    }


    private boolean isNewer(String latest, String current) {
      try {
        String[] l = latest.split("\\.");
        String[] c = current.split("\\.");
        for (int i = 0; i < Math.min(l.length, c.length); i++) {
          int diff = Integer.parseInt(l[i]) - Integer.parseInt(c[i]);
          if (diff > 0) return true;
          if (diff < 0) return false;
        }
        return false;
      } catch (Exception e) { return false; }

    }


    private void downloadUpdate(String body, String version) {
      try {
        // Extract DaemonV.jar download URL from release assets
        java.util.regex.Matcher m = java.util.regex.Pattern
          .compile("\"browser_download_url\"\\s*:\\s*\"([^\"]+DaemonV\\.jar)\"").matcher(body);
        if (!m.find()) return;
        String url = m.group(1);

        HttpClient client = HttpClient.newBuilder()
          .connectTimeout(Duration.ofSeconds(30)).build();
        HttpRequest req = HttpRequest.newBuilder(URI.create(url)).GET().build();

        Path newJar = Paths.get(System.getProperty("user.home") + "/.daemonv/daemonv_new.jar");
        client.send(req, HttpResponse.BodyHandlers.ofFile(newJar));

        System.out.println("[Updater] Downloaded v" + version + " to " + newJar);

        if ("true".equalsIgnoreCase(Env.get("AUTO_RESTART"))) {
          restartDaemon();
        } else {
          EventLogger.notifyAndLog(
              "DaemonV v" + version + " downloaded. Restart to apply.",
              0, "DaemonV", false, "Update Ready"
              );
        }

      } catch (Exception e) {
        System.out.println("[Updater] Download failed: " + e.getMessage());
      }

    }


    private void restartDaemon() {
      try {
        Path newJar  = Paths.get(System.getProperty("user.home") + "/.daemonv/daemonv_new.jar");
        Path currJar = Paths.get(System.getProperty("user.home") + "/.daemonv/daemonv.jar");
        Files.move(newJar, currJar, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);

        EventLogger.notifyAndLog(
            "DaemonV updated. Restarting...", 0, "DaemonV", false, "Restarting"
            );

        // Launch new process then stop self
        new ProcessBuilder("bash", "-c",
            "sleep 2 && java -jar " + currJar.toString() + " &")
          .start();

        stop();

      } catch (Exception e) {
        System.out.println("[Updater] Restart failed: " + e.getMessage());
      }
    }

    @SuppressWarnings("BusyWait")
    public void start() {
      checkForUpdates();
        while (running) {
            try {
                boolean silent = isSilentNow();
                long idle = SystemMonitor.getIdleMinutes();
                String window = SystemMonitor.getActiveWindow();
                boolean isAudio = SystemMonitor.isAudioPlaying();
                String state = BehaviorEngine.classify(idle, window, isAudio);
                
                // 💤 Silent Mode
                if (silent) {
                    if (!silentLogged) {
                        EventLogger.notifyAndLog(
                                "Entering Silent Mode",
                                idle,
                                window,
                                true,
                                "Silent Window"
                        );
                        silentLogged = true;
                    }
                    Thread.sleep(60 * 1000);
                    continue;
                } else {
                    silentLogged = false;
                }

                // ⏱ Cooldown check
                long now = System.currentTimeMillis();
                if ((now - lastTriggerEpoch) < cooldownMillis) {
                    Thread.sleep(checkSleepMillis);
                    continue;
                }

                // 🤖 AI async trigger
                executor.submit(() -> {
                    String msg = ai.generate(
                            LocalTime.now().getHour(),
                            idle,
                            window,
                            isAudio,
                            state
                    );
                
                    EventLogger.notifyAndLog(
                            msg,
                            idle,
                            window,
                            false,
                            state
                    );
                });

                lastTriggerEpoch = now;
                Thread.sleep(checkSleepMillis);

            } catch (InterruptedException e) {
                System.err.println("Daemon interrupted.");
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
