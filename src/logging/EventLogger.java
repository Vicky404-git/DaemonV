package logging;

import java.time.LocalDateTime;

public class EventLogger {

    public static void log(String message) {
        System.out.println("[" + LocalDateTime.now() + "] " + message);
    }
}