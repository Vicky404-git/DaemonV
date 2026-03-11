import core.EnvLoader;
import engine.MessageEngine;
import logging.EventLogger;
import monitor.IdleDetector;

public class SysCheck {

    public static void main(String[] args) {
        System.out.println("==== DaemonV System Diagnostic ====\n");

        // 1. Test Environment Variables
        System.out.print("[1] Checking .env file... ");
        EnvLoader.load();
        String key = EnvLoader.get("GROQ_API_KEY");
        if (key != null && !key.isEmpty()) {
            // Mask the key for safety
            String maskedKey = key.substring(0, 4) + "..." + key.substring(key.length() - 4);
            System.out.println("OK (Key loaded: " + maskedKey + ")");
        } else {
            System.out.println("WARN (Key not found, AI will use fallback)");
        }

        // 2. Test Idle Detector (Mouse tracking)
        System.out.print("[2] Checking Idle Detector... ");
        try {
            IdleDetector detector = new IdleDetector();
            long idle = detector.getIdleMinutes();
            System.out.println("OK (Current idle minutes: " + idle + ")");
        } catch (Exception e) {
            System.out.println("FAIL (" + e.getMessage() + ")");
        }

        // 3. Test OS Notifications (Windows/Linux)
        System.out.println("[3] Testing OS Notification...");
        try {
            EventLogger.log("System Check: If you see this toast, OS notifications work!");
            System.out.println("    -> OK (Check your desktop for a popup)");
        } catch (Exception e) {
            System.out.println("    -> FAIL (" + e.getMessage() + ")");
        }

        // 4. Test AI / Message Generation
        System.out.print("[4] Testing Message Engine (Groq)... ");
        try {
            MessageEngine engine = new MessageEngine();
            // Simulating 2:00 PM and 15 minutes of idle time
            String msg = engine.generate(14, 15);
            System.out.println("OK");
            System.out.println("\n[Generated Output]: " + msg);
        } catch (Exception e) {
            System.out.println("FAIL (" + e.getMessage() + ")");
        }

        System.out.println("\n==== Diagnostic Complete ====");
    }
}