package monitor;

import java.awt.MouseInfo;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class SystemMonitor {
    private static Point lastLocation = null;
    private static long idleStartTime = System.currentTimeMillis();

    public static long getIdleMinutes() {
        try {
            Point current = MouseInfo.getPointerInfo().getLocation();
            if (lastLocation != null && current.equals(lastLocation)) {
                return (System.currentTimeMillis() - idleStartTime) / 60000;
            }
            lastLocation = current;
            idleStartTime = System.currentTimeMillis();
            return 0;
        } catch (Exception e) { return 0; }
    }

    public static String getActiveWindow() {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            ProcessBuilder pb;
            if (os.contains("win")) {
                String script = "Add-Type @\"\nusing System; using System.Runtime.InteropServices;\npublic class API { [DllImport(\"user32.dll\")] public static extern IntPtr GetForegroundWindow(); [DllImport(\"user32.dll\")] public static extern int GetWindowText(IntPtr hWnd, System.Text.StringBuilder text, int count); }\n\"@\n$hwnd = [API]::GetForegroundWindow(); $title = New-Object System.Text.StringBuilder 256; [API]::GetWindowText($hwnd, $title, 256) > $null; $title.ToString()";
                pb = new ProcessBuilder("powershell", "-Command", script);
            } else {
                pb = new ProcessBuilder("sh", "-c", "xdotool getwindowfocus getwindowname");
            }
            Process p = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = reader.readLine();
            return (line != null && !line.isEmpty()) ? line.trim() : "Unknown";
        } catch (Exception e) { return "Unknown"; }
    }
}