package logging;

import java.io.IOException;
import java.time.LocalDateTime;

public class EventLogger {

    public static void log(String message) {
        System.out.println("[" + LocalDateTime.now() + "] " + message);
        
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            sendWindowsNotification("DaemonV", message);
        } else {
            sendLinuxNotification("DaemonV", message);
        }
    }

    private static void sendLinuxNotification(String title, String body) {
        try {
            new ProcessBuilder("notify-send", "-a", "DaemonV", title, body).start();
        } catch (IOException e) {
            System.err.println("[System] Linux notification failed: " + e.getMessage());
        }
    }

    private static void sendWindowsNotification(String title, String body) {
        try {
            String script = String.format(
                "Add-Type -AssemblyName System.Windows.Forms; " +
                "$notify = New-Object System.Windows.Forms.NotifyIcon; " +
                "$notify.Icon = [System.Drawing.Icon]::ExtractAssociatedIcon((Get-Process -id $pid).Path); " +
                "$notify.Visible = $true; " +
                "$notify.ShowBalloonTip(5000, '%s', '%s', [System.Windows.Forms.ToolTipIcon]::Info);",
                title, body);
            
            new ProcessBuilder("powershell", "-Command", script).start();
        } catch (IOException e) {
            System.err.println("[System] Windows notification failed: " + e.getMessage());
        }
    }
}