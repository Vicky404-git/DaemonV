package engine;

import java.time.LocalTime;
import java.util.Random;

public class DecisionEngine {

    private final MessageEngine messageEngine = new MessageEngine();
    private final Random random = new Random();

    public void evaluate() {

        int hour = LocalTime.now().getHour();
        System.out.println("Evaluating at " + hour + ":00...");
        System.out.println("Well Well Well...");

        // 20% chance to roast anytime
        if (random.nextInt(100) < 20) {
            System.out.println("[ROAST] " + messageEngine.getRoast());
            return;
        }

        if (hour >= 2 && hour <= 5) {
            System.out.println("[NIGHT] " + messageEngine.getNight());
        }
        else if (hour >= 6 && hour <= 11) {
            System.out.println("[MORNING] " + messageEngine.getDiscipline());
        }
        else if (hour >= 12 && hour <= 18) {
            System.out.println("[DAY] " + messageEngine.getFocus());
        }
        else {
            System.out.println("[EVENING] " + messageEngine.getReflect());
        }
    }
}