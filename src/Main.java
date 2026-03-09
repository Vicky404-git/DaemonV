import cli.Menu;
import core.ControlServer;
import core.EnvLoader;
import core.MainLoop;
import core.Scheduler; // ADDED THIS IMPORT
import java.time.LocalTime;

public class Main {

    public static void main(String[] args) {
        EnvLoader.load();

        System.out.println("DaemonV v0.2");
        System.out.println("Mode: Background Observer");
        // Update the print statement to check if the key loaded
        System.out.println("AI: " + (EnvLoader.get("GROQ_API_KEY") != null ? "Enabled (Groq)" : "Disabled (Mock)"));
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