import cli.Menu;
import core.ControlServer;
import core.MainLoop;
import core.Scheduler;

public class Debug{

    public static void main(String[] args) {

        System.out.println("DaemonV v0.1");
        System.out.println("Mode: Debug Observer");
        System.out.println("AI: Disabled (Mock)");
        System.out.println("--------------------------------");

        long intervalSeconds = 5;      
        long checkSleepMillis = 1000;  

        Scheduler scheduler = new Scheduler();
        boolean manualTrigger = false;

        for (int i = 0; i < args.length; i++) {

            if (args[i].equals("--silent") && i + 1 < args.length) {
                try {
                    int minutes = Integer.parseInt(args[i + 1]);
                    scheduler.enableSilentForMinutes(minutes);
                    System.out.println("Silent mode enabled for " + minutes + " minutes.");
                    i++; 
                } catch (NumberFormatException e) {
                    System.out.println("Invalid number for --silent.");
                }
            }

            if (args[i].equals("--menu")) {
                new Menu().start(); // FIXED: Empty constructor
                return;
            }

            if (args[i].equals("--ask")) {
                if (manualTrigger) {
                    System.out.println("Warning: --ask flag already set. Ignoring duplicate.");
                } else {
                    manualTrigger = true;
                    System.out.println("Manual trigger mode enabled.");
                }
            }

            if (args[i].equals("--interval") && i + 1 < args.length) {
                try {
                    intervalSeconds = Long.parseLong(args[i + 1]);
                    System.out.println("Debug interval set to " + intervalSeconds + " seconds.");
                    i++;
                } catch (NumberFormatException e) {
                    System.out.println("Invalid number for --interval.");
                }
            }

            if (args[i].equals("--check") && i + 1 < args.length) {
                try {
                    checkSleepMillis = Long.parseLong(args[i + 1]);
                    System.out.println("Check sleep set to " + checkSleepMillis + " ms.");
                    i++;
                } catch (NumberFormatException e) {
                    System.out.println("Invalid number for --check.");
                }
            }
        }

        // ADDED: Start the background listener so the menu works
        ControlServer server = new ControlServer(scheduler);
        server.start();

        MainLoop loop = new MainLoop(
                scheduler,
                manualTrigger,
                intervalSeconds,
                checkSleepMillis
        );

        loop.start();
    }
}