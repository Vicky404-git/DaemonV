package core;

import engine.MessageEngine;
import java.time.LocalTime;
import logging.EventLogger;
import monitor.SystemMonitor;

public class Daemon {

    private volatile boolean silentEnabled = false;
    private volatile long silentUntilEpoch = 0;
    private boolean silentLogged = false;

    private volatile long lastTriggerEpoch = System.currentTimeMillis();

    private volatile int scheduleStartHour = 22;
    private volatile int scheduleEndHour = 7;

    private volatile long cooldownMillis = 4 * 60 * 60 * 1000L;
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

    public void forceTrigger() {
        this.lastTriggerEpoch = 0;
    }

    public boolean isSilentNow() {
        if (silentEnabled && System.currentTimeMillis() < silentUntilEpoch) return true;
        if (ignoreSilentWindow) return false;

        int h = LocalTime.now().getHour();

        return (scheduleStartHour > scheduleEndHour)
                ? (h >= scheduleStartHour || h < scheduleEndHour)
                : (h >= scheduleStartHour && h < scheduleEndHour);
    }

    @SuppressWarnings("BusyWait")
    public void start() {

        while (running) {
            try {

                boolean silent = isSilentNow();
                long idle = SystemMonitor.getIdleMinutes();
                String window = SystemMonitor.getActiveWindow();
                boolean isAudio = SystemMonitor.isAudioPlaying();

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
                new Thread(() -> {
                    String msg = ai.generate(
                            LocalTime.now().getHour(),
                            idle,
                            window,
                            isAudio
                    );

                    EventLogger.notifyAndLog(
                            msg,
                            idle,
                            window,
                            false,
                            "Interval Trigger"
                    );
                }).start();

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

    public void stop() {
        System.out.println("Stopping DaemonV...");
        running = false;
    }
}