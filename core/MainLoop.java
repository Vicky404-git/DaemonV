package core;

import java.time.Instant;

public class MainLoop {

    private final DecisionEngine decisionEngine = new DecisionEngine();
    private Instant lastSpoken = Instant.now();

    public void start() {
        System.out.println("Sentinel v0.1 started...");

        while (true) {

            Instant now = Instant.now();

            // Speak only if 60 seconds passed (testing)
            if (now.getEpochSecond() - lastSpoken.getEpochSecond() >= 60) {

                decisionEngine.evaluate();
                lastSpoken = now;
            }

            try {
                Thread.sleep(5000); // check every 5 seconds
            } catch (InterruptedException e) {
                System.out.println("Interrupted.");
            }
        }
    }
}