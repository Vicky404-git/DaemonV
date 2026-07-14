package logging;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EventLogger {

    // 1. Define the absolute path to ~/DaemonV
    private static final String DAEMON_DIR = System.getProperty("user.home") + File.separator + "DaemonV";
    
    // 2. Append the filename to the absolute directory
    private static final String DATASET_FILE = DAEMON_DIR + File.separator + "DaemonV_data.csv";
    
    private static final String HEADER = "Timestamp,Hour,IdleMinutes,ActiveWindow,IsSilent,TriggerReason,Message\n";

    public static void notifyAndLog(String message, long idle, String window, boolean isSilent, String reason) {
        System.out.println("[" + LocalDateTime.now() + "] " + message);

        if (!isSilent) {
            try {
                if (System.getProperty("os.name").toLowerCase().contains("win")) {
                    String script = String.format(
                            "Add-Type -AssemblyName System.Windows.Forms; $n = New-Object System.Windows.Forms.NotifyIcon; $n.Visible = $true; $n.ShowBalloonTip(5000, 'DaemonV', '%s', [System.Windows.Forms.ToolTipIcon]::Info); Start-Sleep -Seconds 5; $n.Dispose();",
                            message.replace("'", "''")
                    );
                    new ProcessBuilder("powershell", "-Command", script).start();
                } else {
                    new ProcessBuilder("notify-send", "DaemonV", message).start();
                }
            } catch (Exception ignored) {}
        }

        try {
            // 3. Ensure the ~/DaemonV directory exists before trying to write to it
            File dir = new File(DAEMON_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File f = new File(DATASET_FILE);
            boolean isNewFile = !f.exists();

            if (isNewFile) f.createNewFile();

            if (f.length() > 5_000_000) {
                String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm"));
                
                // 4. Ensure the backup file also uses the absolute DAEMON_DIR
                File backup = new File(DAEMON_DIR + File.separator + "DaemonV_data_" + ts + ".csv");
                
                f.renameTo(backup);
                f = new File(DATASET_FILE);
                f.createNewFile();
                isNewFile = true;
            }

            if (isNewFile) {
                Files.writeString(f.toPath(), HEADER, StandardOpenOption.APPEND);
            }

            String safeWindow = "\"" + window.replace("\"", "\"\"") + "\"";
            String safeMsg = "\"" + message.replace("\"", "\"\"") + "\"";

            String row = String.format("%s,%d,%d,%s,%b,%s,%s\n",
                    LocalDateTime.now(), LocalDateTime.now().getHour(), idle, safeWindow, isSilent, reason, safeMsg);

            Files.writeString(f.toPath(), row, StandardOpenOption.APPEND);

        } catch (Exception ignored) {}
    }
}
