package core;

import java.time.LocalTime;

public class Scheduler {

    // Thread-safe variables (Cleaned up duplicates)
    private volatile boolean silentEnabled = false;
    private volatile long silentUntilEpoch = 0;
    private volatile long lastTriggerEpoch = 0;

    private volatile int scheduleStartHour = 22; 
    private volatile int scheduleEndHour = 7;    
    
    private volatile long cooldownMillis = 4 * 60 * 60 * 1000L;
    
    // Allows Debug mode to run late at night
    private volatile boolean ignoreSilentWindow = false;

    public void setIgnoreSilentWindow(boolean ignore) {
        this.ignoreSilentWindow = ignore;
    }

    public void setCooldownMillis(long millis) {
        this.cooldownMillis = millis;
    }

    public void enableSilentForMinutes(int minutes) {
        this.silentEnabled = true;
        this.silentUntilEpoch = System.currentTimeMillis() + (minutes * 60L * 1000L);
    }

    public int getCurrentHour() {
        return LocalTime.now().getHour();
    }

    public boolean isSilentNow() {
        long now = System.currentTimeMillis();

        // 1. Manual Silent Mode ALWAYS wins (even in debug mode)
        if (silentEnabled && now < silentUntilEpoch) {
            return true;
        }

        // 2. If Debug mode bypass is on, ignore the normal scheduled hours
        if (ignoreSilentWindow) {
            return false;
        }

        // 3. Normal schedule check (e.g., 10 PM to 7 AM)
        int currentHour = getCurrentHour();
        if (scheduleStartHour > scheduleEndHour) {
            return currentHour >= scheduleStartHour || currentHour < scheduleEndHour;
        } else {
            return currentHour >= scheduleStartHour && currentHour < scheduleEndHour;
        }
    }

    public boolean canTrigger() {
        long now = System.currentTimeMillis();
        return (now - lastTriggerEpoch) >= cooldownMillis; 
    }

    public void setScheduleWindow(int start, int end) {
        this.scheduleStartHour = start;
        this.scheduleEndHour = end;
    }
    
    public void setTriggerIntervalSeconds(long seconds) {
        this.cooldownMillis = seconds * 1000L;
    }

    public void forceTriggerNow() {
        this.lastTriggerEpoch = 0; 
    }

    public void markTriggered() {
        this.lastTriggerEpoch = System.currentTimeMillis();
    }
}