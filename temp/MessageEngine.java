
import java.util.Random;

public class MessageEngine {

    private final Random random = new Random();

    private final String[] night = {
            "You’re awake again.",
            "Silence is louder at 3AM.",
            "This hour reveals who you are."
    };

    private final String[] roast = {
            "Scrolling again?",
            "You said 5 minutes.",
            "You open tabs like you open dreams."
    };

    private final String[] discipline = {
            "Start before motivation arrives.",
            "Small work. Now.",
            "Open one file."
    };

    private final String[] focus = {
            "One task. 25 minutes.",
            "Don’t switch windows.",
            "Build quietly."
    };

    private final String[] reflect = {
            "What did you actually do today?",
            "Was today intentional?",
            "You are becoming something. What?"
    };

    private String pick(String[] arr) {
        return arr[random.nextInt(arr.length)];
    }

    public String getNight() { return pick(night); }
    public String getRoast() { return pick(roast); }
    public String getDiscipline() { return pick(discipline); }
    public String getFocus() { return pick(focus); }
    public String getReflect() { return pick(reflect); }
}