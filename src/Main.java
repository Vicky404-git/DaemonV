import cli.Remote;
import core.Daemon;
import core.Env;

public class Main {
    public static void main(String[] args) {
        Env.load();

        boolean menuMode = false;
        boolean debugMode = false;
        boolean manualTrigger = false;
        int silentStart = 0;

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--menu")) menuMode = true;
            if (args[i].equals("--debug")) debugMode = true;
            if (args[i].equals("--ask")) manualTrigger = true;
            if (args[i].equals("--silent") && i + 1 < args.length) {
                try { silentStart = Integer.parseInt(args[++i]); } catch (Exception ignored) {}
            }
        }

        if (menuMode) {
            Remote.startMenu();
            return;
        }

        System.out.println("DaemonV v0.3 (Context-Aware)");
        System.out.println("AI: " + (Env.get("GROQ_API_KEY") != null ? "Enabled (Groq)" : "Disabled (Mock)"));
        
        Daemon daemon = new Daemon();
        
        if (debugMode) {
            System.out.println("Mode: DEBUG (5s interval)");
            daemon.setDebugMode(5, 1000); 
        }
        
        if (silentStart > 0) daemon.enableSilent(silentStart);
        if (manualTrigger) daemon.forceTrigger();

        Remote.startServer(daemon);
        daemon.start();
    }
}