import core.MainLoop;
import core.Scheduler;

public class Debug {

    public static void main(String[] args) {

        long intervalSeconds = 5;       // speak every 5 seconds
        long checkSleepMillis = 1000;   // check every 1 second

        Scheduler scheduler = new Scheduler();
        boolean manualTrigger = false;

        for (int i = 0; i < args.length; i++) {

            if (args[i].equals("--silent") && i + 1 < args.length) {
                int minutes = Integer.parseInt(args[i + 1]);
                scheduler.enableSilentForMinutes(minutes);
            }

            if (args[i].equals("--ask")) {
                manualTrigger = true;
            }
        }

        MainLoop loop = new MainLoop(scheduler,manualTrigger,intervalSeconds,checkSleepMillis );

        loop.start();
    }
}