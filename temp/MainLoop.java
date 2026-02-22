public class MainLoop {

    private final DecisionEngine decisionEngine = new DecisionEngine();

    public void start() {
        System.out.println("DaemonV v0.1 started...");

        while (true) {
            decisionEngine.evaluate();

            try {
                Thread.sleep(120000);
            } catch (InterruptedException e) {
                System.out.println("Interrupted.");
            }
        }
    }
}