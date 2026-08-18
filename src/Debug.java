import cli.Remote;
import core.Daemon;
import core.Env;
import engine.BehaviorEngine;
import engine.MessageEngine;
import logging.EventLogger;
import monitor.SystemMonitor;

public class Debug {

    public static void main(String[] args) {
        System.out.println("==== DaemonV Debug & Diagnostic Suite ====\n");
        
        runDiagnostics();

        System.out.println("\n==== Starting Daemon in DEBUG Mode ====");
        System.out.println("Interval: 5 seconds | Silent Windows: Bypassed\n");
        
        Daemon daemon = new Daemon();
        daemon.setDebugMode(5, 1000); 
        
        Remote.startServer(daemon);
        daemon.start();
    }

    private static void runDiagnostics() {
        // 1. Environment
        System.out.print("[1] Checking .env file... ");
        Env.load();
        String key = Env.get("GROQ_API_KEY");
        if (key != null && !key.isEmpty()) {
            String maskedKey = key.substring(0, 4) + "..." + key.substring(key.length() - 4);
            System.out.println("OK (Key loaded: " + maskedKey + ")");
        } else {
            System.out.println("WARN (Key not found, AI will use fallback)");
        }

        // 2. Sensors
        System.out.print("[2] Checking Sensors... ");
        try {
            long idle = SystemMonitor.getIdleMinutes();
            String window = SystemMonitor.getActiveWindow();
            System.out.println("OK (Idle: " + idle + "m | Active Window: '" + window + "')");
        } catch (Exception e) {
            System.out.println("FAIL (" + e.getMessage() + ")");
        }

        // 3. Notifications + CSV
        System.out.println("[3] Testing Output & Dataset...");
        try {
            EventLogger.notifyAndLog("Diagnostic Test: System is fully operational.", 0, "Terminal", false, "Diagnostic");
            System.out.println("    -> OK (Check desktop for popup & ~/DaemonV/CSVs/events.csv for a new row)");
        } catch (Exception e) {
            System.out.println("    -> FAIL (" + e.getMessage() + ")");
        }

        // 4. AI Engine
        System.out.print("[4] Testing AI Context Engine (Groq)... ");
        try {
            MessageEngine engine = new MessageEngine();
            String state = BehaviorEngine.classify(0, "Code | Visual Studio Code", true);
            String msg = engine.generate(14, 0, "Code | Visual Studio Code", true, state);
            System.out.println("OK");
            System.out.println("    -> [AI Output]: " + msg);
        } catch (Exception e) {
            System.out.println("FAIL (" + e.getMessage() + ")");
        }

        // 5. Memory Context
        System.out.print("[5] Checking Memory Context... ");
        String memCtx = Env.getMemoryContext();
        if (!memCtx.isEmpty()) {
            System.out.println("OK (Loaded " + memCtx.length() + " chars from memory.md)");
        } else {
            System.out.println("WARN (No memory.md found — first run or not yet generated)");
        }

        // 6. Note Logger
        System.out.print("[6] Testing Note Logger... ");
        try {
            EventLogger.logNote("Diagnostic test note.");
            System.out.println("OK (Check ~/DaemonV/Memory/notes.txt)");
        } catch (Exception e) {
            System.out.println("FAIL (" + e.getMessage() + ")");
        }

        // 7. MemoryManager
        System.out.print("[7] Testing MemoryManager... ");
        if (key != null && !key.isEmpty()) {
            System.out.println("SKIP (would trigger real Groq call — use --menu > Upgrade Memory to test manually)");
        } else {
            System.out.println("SKIP (no API key)");
        }
    }
}
