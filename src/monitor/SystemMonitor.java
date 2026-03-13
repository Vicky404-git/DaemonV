package monitor;

import java.awt.HeadlessException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.IOException;
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
        } catch (HeadlessException | SecurityException e) { return 0; }
    }

    public static String getActiveWindow() {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            ProcessBuilder pb;
            if (os.contains("win")) {
                // Extracts BOTH the Process Name (.exe) and the Window Title
                String script = "Add-Type @\"\nusing System; using System.Runtime.InteropServices;\n" +
                    "public class API { [DllImport(\"user32.dll\")] public static extern IntPtr GetForegroundWindow(); " +
                    "[DllImport(\"user32.dll\")] public static extern int GetWindowThreadProcessId(IntPtr hWnd, out int lpdwProcessId); " +
                    "[DllImport(\"user32.dll\")] public static extern int GetWindowText(IntPtr hWnd, System.Text.StringBuilder text, int count); }\n\"@\n" +
                    "$hwnd = [API]::GetForegroundWindow(); $pid = 0; [API]::GetWindowThreadProcessId($hwnd, [ref]$pid) > $null; " +
                    "$proc = Get-Process -Id $pid -ErrorAction SilentlyContinue; " +
                    "$title = New-Object System.Text.StringBuilder 256; [API]::GetWindowText($hwnd, $title, 256) > $null; " +
                    "Write-Output \"$($proc.ProcessName) | $($title.ToString())\"";
                pb = new ProcessBuilder("powershell", "-Command", script);
            } else {
                pb = new ProcessBuilder("sh", "-c", "xdotool getwindowfocus getwindowname");
            }
            Process p = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = reader.readLine();
            return (line != null && !line.isEmpty()) ? line.trim() : "Unknown";
        } catch (IOException e) { return "Unknown"; }
    }

    public static boolean isAudioPlaying() {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                // Windows Zero-Dependency Heuristic: Checks if known music players are running
                Process p = new ProcessBuilder("tasklist").start();
                BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line;
                while ((line = r.readLine()) != null) {
                    String lower = line.toLowerCase();
                    if (lower.contains("spotify.exe") || lower.contains("vlc.exe") || lower.contains("itunes.exe")) {
                        return true;
                    }
                }
                return false;
            } else {
                // Linux (Arch): 100% accurate native soundcard check
                Process p = new ProcessBuilder("sh", "-c", "grep -q 'RUNNING' /proc/asound/card*/pcm*/sub*/status || pactl list sink-inputs | grep -q 'State: RUNNING'").start();
                return p.waitFor() == 0;
            }
        } catch (Exception e) { return false; }
    }
}