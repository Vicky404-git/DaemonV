package engine;

public class BehaviorEngine {

    public static String classify(long idleMinutes, String window, boolean isAudio) {

        String w = (window == null) ? "" : window.toLowerCase();
        // 💤 Completely idle
        if (idleMinutes >= 10) {
            return "IDLE";
        }

        // 🎧 Passive consumption (music + browser/video)
        if (isAudio && (w.contains("youtube") || w.contains("chrome") || w.contains("spotify"))) {
            return "PASSIVE";
        }

        // 📺 Distraction (browser but no real work signal)
        if (w.contains("youtube") || w.contains("netflix") || w.contains("instagram")) {
            return "DISTRACTED";
        }

        // 💻 Focus (coding / terminal / IDE)
        if (w.contains("code") || w.contains("terminal") || w.contains("intellij") || w.contains("pycharm")) {
            return "FOCUSED";
        }

        // Default fallback
        return "PASSIVE";
    }
}