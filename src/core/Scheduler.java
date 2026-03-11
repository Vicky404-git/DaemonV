package core;

import java.time.LocalTime;

public class Scheduler {

    private volatile boolean silentEnabled = false;
    private volatile long silentUntilEpoch = 0;
    
    // Fixed: Initializes to current time so it waits for the first interval properly
    private volatile long lastTriggerEpoch = System.currentTimeMillis();

    private volatile int scheduleStartHour = 22; 
    private volatile int scheduleEndHour = 7;    
    
    private volatile long cooldownMillis = 4 * 60 * 60 * 1000L;
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

        if (silentEnabled && now < silentUntilEpoch) {
            return true;
        }

        if (ignoreSilentWindow) {
            return false;
        }

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