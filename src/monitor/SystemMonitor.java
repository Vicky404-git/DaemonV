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

        } catch (Exception e) {
            return 0;
        }
    }

    public static String getActiveWindow() {

        long now = System.currentTimeMillis();

        if (now - lastWindowCheck < 10_000) {
            return lastWindow;
        }

        lastWindowCheck = now;

        try {
            String os = System.getProperty("os.name").toLowerCase();
            String result = "Unknown";

            if (os.contains("win")) {

                Process p = new ProcessBuilder("powershell", "-Command",
                        "Get-Process | Where-Object {$_.MainWindowTitle} | Select-Object -First 1 -ExpandProperty MainWindowTitle"
                ).start();

                BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line = reader.readLine();

                result = (line != null && !line.isBlank()) ? line.trim() : "Desktop";

            } else {

                Process p = new ProcessBuilder("sh", "-c",
                        "xdotool getwindowfocus getwindowname"
                ).start();

                BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line = reader.readLine();

                result = (line != null) ? line.trim() : "Desktop";
            }

            lastWindow = result;
            return result;

        } catch (Exception e) {
            return lastWindow;
        }
    }

    public static boolean isAudioPlaying() {
        try {
            String os = System.getProperty("os.name").toLowerCase();

            if (os.contains("win")) {
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
                Process p = new ProcessBuilder("sh", "-c",
                        "grep -q 'RUNNING' /proc/asound/card*/pcm*/sub*/status || pactl list sink-inputs | grep -q 'State: RUNNING'"
                ).start();

                return p.waitFor() == 0;
            }

        } catch (Exception e) {
            return false;
        }
    }
}