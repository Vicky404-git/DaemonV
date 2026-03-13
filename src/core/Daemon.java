package core;

import engine.MessageEngine;
import logging.EventLogger;
import monitor.SystemMonitor;
import java.time.LocalTime;

public class Daemon {
    private volatile boolean silentEnabled = false;
    private volatile long silentUntilEpoch = 0;
    private volatile long lastTriggerEpoch = System.currentTimeMillis();
    
    private volatile int scheduleStartHour = 22; 
    private volatile int scheduleEndHour = 7;    
    
    private volatile long cooldownMillis = 4 * 60 * 60 * 1000L;
    private volatile long checkSleepMillis = 5 * 60 * 1000L;
    private volatile boolean ignoreSilentWindow = false;

    private final MessageEngine ai = new MessageEngine();

    public void setDebugMode(long intervalSeconds, long checkMillis) {
        this.cooldownMillis = intervalSeconds * 1000L;
        this.checkSleepMillis = checkMillis;
        this.ignoreSilentWindow = true;
    }

    public void enableSilent(int minutes) {
        this.silentEnabled = true;
        this.silentUntilEpoch = System.currentTimeMillis() + (minutes * 60000L);
    }

    public void setSchedule(int start, int end) { this.scheduleStartHour = start; this.scheduleEndHour = end; }
    public void setInterval(long seconds) { this.cooldownMillis = seconds * 1000L; }
    public void forceTrigger() { this.lastTriggerEpoch = 0; }

    public boolean isSilentNow() {
        if (silentEnabled && System.currentTimeMillis() < silentUntilEpoch) return true;
        if (ignoreSilentWindow) return false;
        
        int h = LocalTime.now().getHour();
        return (scheduleStartHour > scheduleEndHour) ? (h >= scheduleStartHour || h < scheduleEndHour) : (h >= scheduleStartHour && h < scheduleEndHour);
    }

    public void start() {
        while (true) {
            try {
                boolean silent = isSilentNow();
                long idle = SystemMonitor.getIdleMinutes();
                String window = SystemMonitor.getActiveWindow();

                if (silent) {
                    EventLogger.notifyAndLog("Skipped (Silent Mode)", idle, window, true, "Silent Window Active");
                    Thread.sleep(60 * 1000); 
                    lastTriggerEpoch = System.currentTimeMillis(); 
                    continue;
                }

                if ((System.currentTimeMillis() - lastTriggerEpoch) >= cooldownMillis) {
                    String msg = ai.generate(LocalTime.now().getHour(), idle, window);
                    EventLogger.notifyAndLog(msg, idle, window, false, "Interval Trigger");
                    lastTriggerEpoch = System.currentTimeMillis();
                }

                Thread.sleep(checkSleepMillis);
            } catch (Exception e) { e.printStackTrace(); }
        }
    }
}