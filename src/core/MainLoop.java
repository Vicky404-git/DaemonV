package core;

import engine.DecisionEngine;
import engine.MessageEngine;
import logging.EventLogger;
import monitor.IdleDetector;

public class MainLoop {

    private final Scheduler scheduler;
    private final boolean manualTrigger;

    private final DecisionEngine decisionEngine;
    private final MessageEngine messageEngine;
    private final IdleDetector idleDetector;

    private long checkSleepMillis = 5 * 60 * 1000L; // Default 5 mins

    // Production Constructor (Used by Main.java)
    public MainLoop(Scheduler scheduler, boolean manualTrigger) {
        this.scheduler = scheduler;
        this.manualTrigger = manualTrigger;

        this.decisionEngine = new DecisionEngine();
        this.messageEngine = new MessageEngine();
        this.idleDetector = new IdleDetector();
    }

    // Debug Constructor (Used by Debug.java)
    public MainLoop(Scheduler scheduler, boolean manualTrigger, long intervalSeconds, long checkSleepMillis) {
        this(scheduler, manualTrigger); 
        
        this.checkSleepMillis = checkSleepMillis;
        this.scheduler.setCooldownMillis(intervalSeconds * 1000L); 
        
        // Tells the scheduler to bypass the 10PM-7AM lock
        this.scheduler.setIgnoreSilentWindow(true); 
    }

    public void start() {
        while (true) {
            try {
                if (scheduler.isSilentNow()) {
                    Thread.sleep(60 * 1000);
                    continue;
                }

                if (decisionEngine.shouldTrigger(scheduler, manualTrigger)) {
                    long idleMinutes = idleDetector.getIdleMinutes();

                    String message = messageEngine.generate(
                            scheduler.getCurrentHour(),
                            idleMinutes
                    );

                    EventLogger.log(message);
                    scheduler.markTriggered();
                }

                Thread.sleep(checkSleepMillis); 

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}