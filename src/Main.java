import cli.Remote;
import core.Daemon;
import core.Env;
import java.io.File;

public class Main {
    public static final String VERSION = "1.0.0";

    public static void main(String[] args) {
        // 1. Handle basic info flags immediately
        for (String arg : args) {
            if (arg.equals("--version") || arg.equals("-v")) {
                System.out.println("DaemonV v" + VERSION);
                return;
            }
            if (arg.equals("--help") || arg.equals("-h")) {
                System.out.println("Usage: daemonv [options]");
                System.out.println("Options:");
                System.out.println("  --menu    Open the remote control CLI");
                System.out.println("  --ask     Force a one-time trigger (if daemon is not running)");
                System.out.println("  --debug   Run in foreground with 5s intervals");
                return;
            }
        }

        Env.load();

        boolean menuMode = false;
        boolean debugMode = false;
        boolean manualTrigger = false;

        for (String arg : args) {
            if (arg.equals("--menu")) menuMode = true;
            if (arg.equals("--debug")) debugMode = true;
            if (arg.equals("--ask")) manualTrigger = true;
        }

        if (menuMode) {
            Remote.startMenu();
            return;
        }

        // 2. Prevent Ghost Daemons (PID Lock)
        File lock = new File(System.getProperty("user.home") + "/.daemonv.lock");
        if (lock.exists() && !debugMode && !manualTrigger) { 
            System.err.println("[ERROR] DaemonV is already running.");
            System.err.println("Use 'daemonv --menu' to control it, or delete ~/.daemonv.lock if stuck.");
            System.exit(1); 
        }
        lock.deleteOnExit();
        try { lock.createNewFile(); } catch (Exception ignored) {}

        System.out.println("DaemonV v" + VERSION + " starting...");
        System.out.println("AI: " + (Env.get("GROQ_API_KEY") != null ? "Enabled (Groq)" : "Disabled (Mock)"));
        
        Daemon daemon = new Daemon();
        
        if (debugMode) {
            System.out.println("Mode: DEBUG (5s interval)");
            daemon.setDebugMode(5, 1000); 
        }
        
        if (manualTrigger) daemon.forceTrigger();

        Remote.startServer(daemon);
        daemon.start();
    }
}
