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

    private long checkSleepMillis = 5 * 60 * 1000L; 

    public MainLoop(Scheduler scheduler, boolean manualTrigger) {
        this.scheduler = scheduler;
        this.manualTrigger = manualTrigger;

        this.decisionEngine = new DecisionEngine();
        this.messageEngine = new MessageEngine();
        this.idleDetector = new IdleDetector();
    }

    public MainLoop(Scheduler scheduler, boolean manualTrigger, long intervalSeconds, long checkSleepMillis) {
        this(scheduler, manualTrigger); 
        this.checkSleepMillis = checkSleepMillis;
        this.scheduler.setCooldownMillis(intervalSeconds * 1000L); 
        this.scheduler.setIgnoreSilentWindow(true); 
    }

    public void start() {
        if (this.manualTrigger) {
            scheduler.forceTriggerNow();
        }

        while (true) {
            try {
                if (scheduler.isSilentNow()) {
                    Thread.sleep(60 * 1000); // Sleep for 1 minute
                    scheduler.markTriggered(); // Pushes the timer forward
                    continue;
                }

                if (decisionEngine.shouldTrigger(scheduler)) {
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