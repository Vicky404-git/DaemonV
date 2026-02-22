import core.MainLoop;
import core.Scheduler;

public class Main {

    public static void main(String[] args) {

        System.out.println("DaemonV v0.1");
        System.out.println("Mode: Background Observer");
        System.out.println("AI: Disabled (Mock)");
        System.out.println("--------------------------------");

        Scheduler scheduler = new Scheduler();
        boolean manualTrigger = false;

        for (int i = 0; i < args.length; i++) {

            if (args[i].equals("--silent") && i + 1 < args.length) {
                int minutes = Integer.parseInt(args[i + 1]);
                scheduler.enableSilentForMinutes(minutes);
                System.out.println("Silent mode enabled for " + minutes + " minutes.");
            }

            if (args[i].equals("--ask")) {
                manualTrigger = true;
            }
        }

        MainLoop loop = new MainLoop(scheduler, manualTrigger);
        loop.start();
    }
}