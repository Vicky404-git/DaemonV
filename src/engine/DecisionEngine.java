package engine;

import core.Scheduler;

public class DecisionEngine {
    public boolean shouldTrigger(Scheduler scheduler) {
        return scheduler.canTrigger();
    }
}