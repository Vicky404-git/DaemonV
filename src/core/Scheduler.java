package core;

import java.time.LocalTime;

public class Scheduler {

    private boolean silentEnabled = false;
    private long silentUntilEpoch = 0;
    private long lastTriggerEpoch = 0;

    private final int scheduleStartHour = 22; // 10PM
    private final int scheduleEndHour = 7;    // 7AM
    
    // Default to 4 hours in milliseconds
    private long cooldownMillis = 4 * 60 * 60 * 1000L; 

    // Add a setter so Debug mode can change the cooldown
    public void setCooldownMillis(long millis) {
        this.cooldownMillis = millis;
    }

    public void enableSilentForMinutes(int minutes) {
        silentEnabled = true;
        silentUntilEpoch = System.currentTimeMillis() + (minutes * 60L * 1000L);
    }

    public int getCurrentHour() {
        return LocalTime.now().getHour();
    }

    public boolean isSilentNow() {
        int currentHour = getCurrentHour();
        long now = System.currentTimeMillis();

        if (silentEnabled && now < silentUntilEpoch) {
            return true;
        }

        if (scheduleStartHour > scheduleEndHour) {
            return currentHour >= scheduleStartHour || currentHour < scheduleEndHour;
        } else {
            return currentHour >= scheduleStartHour && currentHour < scheduleEndHour;
        }
    }

    public boolean canTrigger() {
        long now = System.currentTimeMillis();
        // Check against the flexible cooldown instead of hardcoded 4 hours
        return (now - lastTriggerEpoch) >= cooldownMillis; 
    }

    public void markTriggered() {
        lastTriggerEpoch = System.currentTimeMillis();
    }
}