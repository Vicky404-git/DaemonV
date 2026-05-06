package monitor;

import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class SystemMonitor {

    private static Point lastLocation = null;
    private static long idleStartTime = System.currentTimeMillis();

    private static String lastWindow = "Unknown";
    private static long lastWindowCheck = 0;

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
        long now = System.currentTimeMillis();
        if (now - lastWindowCheck < 10_000) return lastWindow;
        lastWindowCheck = now;

        try {
            String os = System.getProperty("os.name").toLowerCase();
            String result = "Unknown";

            if (os.contains("win")) {
                Process p = new ProcessBuilder("powershell", "-Command",
                        "Get-Process | Where-Object {$_.MainWindowTitle} | Select-Object -First 1 -ExpandProperty MainWindowTitle").start();
                BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line = reader.readLine();
                result = (line != null && !line.isBlank()) ? line.trim() : "Desktop";
            } else {
                // THE FIX: Automatically hunt down the terminal's child process if it's a generic shell window
                String script = "title=$(xdotool getwindowfocus getwindowname 2>/dev/null); " +
                                "if echo \"$title\" | grep -qiE 'terminal|alacritty|kitty|bash|zsh|i3|~'; then " +
                                "pid=$(xdotool getwindowfocus getwindowpid 2>/dev/null); " +
                                "if [ -n \"$pid\" ]; then " +
                                "child=$(ps --ppid $pid -o comm= | tail -n 1); " +
                                "if [ -n \"$child\" ]; then echo \"Terminal ($child)\"; exit 0; fi; " +
                                "fi; echo \"Terminal\"; else echo \"$title\"; fi";
                                
                Process p = new ProcessBuilder("sh", "-c", script).start();
                BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line = reader.readLine();
                result = (line != null) ? line.trim() : "Desktop";
            }
            lastWindow = result;
            return result;
        } catch (Exception e) { return lastWindow; }
    }

    public static boolean isAudioPlaying() {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                Process p = new ProcessBuilder("tasklist").start();
                BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line;
                while ((line = r.readLine()) != null) {
                    if (line.toLowerCase().matches(".*(spotify|vlc|itunes|brave|chrome|edge).*")) return true;
                }
                return false;
            } else {
                // THE FIX: Added 'wpctl' for Debian Trixie PipeWire support (YouTube Music)
                String script = "grep -q 'RUNNING' /proc/asound/card*/pcm*/sub*/status 2>/dev/null || " +
                                "pactl list sink-inputs 2>/dev/null | grep -q 'State: RUNNING' || " +
                                "wpctl status 2>/dev/null | grep -qi 'running'";
                Process p = new ProcessBuilder("sh", "-c", script).start();
                return p.waitFor() == 0;
            }
        } catch (Exception e) { return false; }
    }
}
