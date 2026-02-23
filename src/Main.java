import cli.Menu;
import core.ControlServer;
import core.MainLoop;
import core.Scheduler; 
import java.time.LocalTime; // ADDED THIS IMPORT

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

            if (args[i].equals("--menu")) {
                new Menu().start(); 
                return; 
            }

            if (args[i].equals("--ask")) {
                if (manualTrigger){
                    System.out.println("Warning: --ask flag is already set. Ignoring duplicate.");
                } else {
                    manualTrigger = true;
                    System.out.println("Manual trigger mode enabled.");
                }
            }
        }

        ControlServer server = new ControlServer(scheduler);
        server.start();

        // ADDED: Check if it's night/silent right on startup
        if (scheduler.isSilentNow()) {
            System.out.println("[" + LocalTime.now() + "] It is currently night time (silent window). Daemon is going to sleep...");
        }

        MainLoop loop = new MainLoop(scheduler, manualTrigger); 
        loop.start();
    }
}