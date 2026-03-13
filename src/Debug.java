import cli.Remote;
import core.Daemon;
import core.Env;
import engine.MessageEngine;
import logging.EventLogger;
import monitor.SystemMonitor;

public class Debug {

    public static void main(String[] args) {
        System.out.println("==== DaemonV Debug & Diagnostic Suite ====\n");
        
        // 1. Run System Checks (Merged from SysCheck)
        runDiagnostics();

        System.out.println("\n==== Starting Daemon in DEBUG Mode ====");
        System.out.println("Interval: 5 seconds | Silent Windows: Bypassed\n");
        
        // 2. Start the Daemon in Debug mode (Merged from old Debug)
        Daemon daemon = new Daemon();
        daemon.setDebugMode(5, 1000); 
        
        Remote.startServer(daemon);
        daemon.start();
    }

    private static void runDiagnostics() {
        // 1. Test Environment Variables
        System.out.print("[1] Checking .env file... ");
        Env.load();
        String key = Env.get("GROQ_API_KEY");
        if (key != null && !key.isEmpty()) {
            String maskedKey = key.substring(0, 4) + "..." + key.substring(key.length() - 4);
            System.out.println("OK (Key loaded: " + maskedKey + ")");
        } else {
            System.out.println("WARN (Key not found, AI will use fallback)");
        }

        // 2. Test Idle Detector & Active Window (SystemMonitor)
        System.out.print("[2] Checking Sensors... ");
        try {
            long idle = SystemMonitor.getIdleMinutes();
            String window = SystemMonitor.getActiveWindow();
            System.out.println("OK (Idle: " + idle + "m | Active Window: '" + window + "')");
        } catch (Exception e) {
            System.out.println("FAIL (" + e.getMessage() + ")");
        }

        // 3. Test OS Notifications & CSV Dataset (EventLogger)
        System.out.println("[3] Testing Output & Dataset...");
        try {
            EventLogger.notifyAndLog("Diagnostic Test: System is fully operational.", 0, "Terminal", false, "Diagnostic");
            System.out.println("    -> OK (Check desktop for popup & dataset.csv for a new row)");
        } catch (Exception e) {
            System.out.println("    -> FAIL (" + e.getMessage() + ")");
        }

        // 4. Test AI Context Awareness
        System.out.print("[4] Testing AI Context Engine (Groq)... ");
        try {
            MessageEngine engine = new MessageEngine();
            // Simulating a test where the user is looking at VSCode
            String msg = engine.generate(14, 0, "Code | Visual Studio Code", true);
            System.out.println("OK");
            System.out.println("\n[AI Output]: " + msg);
        } catch (Exception e) {
            System.out.println("FAIL (" + e.getMessage() + ")");
        }
    }
}