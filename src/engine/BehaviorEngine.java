package engine;

public class BehaviorEngine {

    public static String classify(long idleMinutes, String window, boolean isAudio) {
        String w = (window == null) ? "" : window.toLowerCase();
        
        // If they are away from the keyboard...
        if (idleMinutes >= 10) {
            // But music is playing, they are just chilling
            if (isAudio) return "PASSIVE"; 
            // Otherwise, the machine is abandoned
            return "IDLE";
        }

        // 🎧 Passive consumption (music + browser/video)
        if (isAudio || w.contains("youtube") || w.contains("music") || w.contains("spotify")) {
            // If they are on YouTube but NOT doing coding tutorials, they are distracted
            if (w.contains("youtube") && !w.matches(".*(tutorial|code|guide|server).*")) return "DISTRACTED";
            return "PASSIVE";
        }

        // 📺 Distraction
        if (w.contains("netflix") || w.contains("instagram") || w.contains("reddit")) {
            return "DISTRACTED";
        }

        // 💻 Focus (coding / terminal / IDE)
        if (w.contains("code") || w.contains("terminal") || w.contains("nvim") || w.contains("intellij")) {
            return "FOCUSED";
        }

        return "PASSIVE";
    }
}
