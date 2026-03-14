package monitor;

import java.awt.HeadlessException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;

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
            if (os.contains("win")) {
                // FIX: Write to a temp .ps1 file so newlines aren't destroyed by Java
                String script = "Add-Type @\"\n" +
                    "using System;\n" +
                    "using System.Runtime.InteropServices;\n" +
                    "public class API {\n" +
                    "    [DllImport(\"user32.dll\")] public static extern IntPtr GetForegroundWindow();\n" +
                    "    [DllImport(\"user32.dll\")] public static extern int GetWindowThreadProcessId(IntPtr hWnd, out int lpdwProcessId);\n" +
                    "    [DllImport(\"user32.dll\")] public static extern int GetWindowText(IntPtr hWnd, System.Text.StringBuilder text, int count);\n" +
                    "}\n" +
                    "\"@\n" +
                    "$hwnd = [API]::GetForegroundWindow()\n" +
                    "$pid = 0\n" +
                    "[API]::GetWindowThreadProcessId($hwnd, [ref]$pid) > $null\n" +
                    "$proc = Get-Process -Id $pid -ErrorAction SilentlyContinue\n" +
                    "$title = New-Object System.Text.StringBuilder 256\n" +
                    "[API]::GetWindowText($hwnd, $title, 256) > $null\n" +
                    "Write-Output \"$($proc.ProcessName) | $($title.ToString())\"\n";

                File tempFile = File.createTempFile("daemonv_window", ".ps1");
                Files.writeString(tempFile.toPath(), script);

                Process p = new ProcessBuilder("powershell", "-ExecutionPolicy", "Bypass", "-File", tempFile.getAbsolutePath()).start();
                BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line = reader.readLine();
                
                tempFile.delete(); // Clean up the temp file instantly

                return (line != null && !line.isEmpty()) ? line.trim() : "Desktop";
            } else {
                Process p = new ProcessBuilder("sh", "-c", "xdotool getwindowfocus getwindowname").start();
                BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line = reader.readLine();
                return (line != null && !line.isEmpty()) ? line.trim() : "Desktop";
            }
        } catch (Exception e) { 
            return "Unknown"; 
        }
    }

    public static boolean isAudioPlaying() {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                // Windows Heuristic: Checks if common music players are running
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