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
        this(scheduler, manualTrigger); // Call the main constructor to set up engines
        
        this.checkSleepMillis = checkSleepMillis;
        
        // Tell the scheduler to trigger every X seconds instead of 4 hours
        this.scheduler.setCooldownMillis(intervalSeconds * 1000L); 
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

                // Sleep for the dynamic amount of time (1 sec in debug, 5 mins in main)
                Thread.sleep(checkSleepMillis); 

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}