package engine;

public class MessageEngine {

    public String generate(int hour, long idleMinutes) {

        return "[Mock] Hour: " + hour +
                " | Idle: " + idleMinutes + " min" +
                " | You are being observed.";
    }
}