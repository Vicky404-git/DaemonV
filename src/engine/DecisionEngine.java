package engine;

import core.Scheduler;

public class DecisionEngine {

    public boolean shouldTrigger(Scheduler scheduler, boolean manualTrigger) {

        if (manualTrigger) {
            return true;
        }

        return scheduler.canTrigger();
    }
}