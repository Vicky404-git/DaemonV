package logging;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;

public class EventLogger {

    private static final String DATASET_FILE = "dataset.csv";

    public static void notifyAndLog(String message, long idle, String window, boolean isSilent, String reason) {

        System.out.println("[" + LocalDateTime.now() + "] " + message);

        if (!isSilent) {
            try {
                if (System.getProperty("os.name").toLowerCase().contains("win")) {
                    String script = String.format(
                            "Add-Type -AssemblyName System.Windows.Forms; " +
                            "$n = New-Object System.Windows.Forms.NotifyIcon; " +
                            "$n.Visible = $true; " +
                            "$n.ShowBalloonTip(5000, 'DaemonV', '%s', [System.Windows.Forms.ToolTipIcon]::Info); " +
                            "Start-Sleep -Seconds 5; $n.Dispose();",
                            message.replace("'", "''")
                    );

                    new ProcessBuilder("powershell", "-Command", script).start();

                } else {
                    new ProcessBuilder("notify-send", "DaemonV", message).start();
                }

            } catch (Exception ignored) {}
        }

        try {
            File f = new File(DATASET_FILE);

            if (!f.exists()) {
                f.createNewFile();
            }

            if (f.length() > 5_000_000) {
                File backup = new File("dataset_" + System.currentTimeMillis() + ".csv");
                f.renameTo(backup);
                f = new File(DATASET_FILE);
                f.createNewFile();
            }

            String safeWindow = "\"" + window.replace("\"", "\"\"") + "\"";
            String safeMsg = "\"" + message.replace("\"", "\"\"") + "\"";

            String row = String.format("%s,%d,%d,%s,%b,%s,%s\n",
                    LocalDateTime.now(),
                    LocalDateTime.now().getHour(),
                    idle,
                    safeWindow,
                    isSilent,
                    reason,
                    safeMsg
            );

            Files.writeString(f.toPath(), row, StandardOpenOption.APPEND);

        } catch (Exception ignored) {}
    }
}