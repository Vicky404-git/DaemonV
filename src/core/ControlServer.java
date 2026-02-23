package core;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ControlServer extends Thread {

    private final Scheduler scheduler;
    private static final int PORT = 9333; // The secret Daemon port

    public ControlServer(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("[Daemon] Control Server listening on port " + PORT);
            
            while (true) {
                // Wait for the Menu to connect and send a command
                try (Socket clientSocket = serverSocket.accept();
                     PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                     BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

                    String inputLine = in.readLine();
                    if (inputLine != null) {
                        String response = processCommand(inputLine);
                        out.println(response); // Send the result back to the Menu
                    }
                } catch (Exception e) {
                    System.out.println("Error handling menu request.");
                }
            }
        } catch (Exception e) {
            System.out.println("Could not start Control Server on port " + PORT + ". Is DaemonV already running in the background?");
        }
    }

    private String processCommand(String command) {
        String[] parts = command.split(" ");
        String action = parts[0].toUpperCase();

        try {
            switch (action) {
                case "STATUS":
                    return "Silent active: " + scheduler.isSilentNow();
                case "SILENT":
                    int minutes = Integer.parseInt(parts[1]);
                    scheduler.enableSilentForMinutes(minutes);
                    return "Silent enabled for " + minutes + " minutes.";
                case "SCHEDULE":
                    int start = Integer.parseInt(parts[1]);
                    int end = Integer.parseInt(parts[2]);
                    scheduler.setScheduleWindow(start, end);
                    return "Scheduled silent updated to " + start + " - " + end + ".";
                case "INTERVAL":
                    long seconds = Long.parseLong(parts[1]);
                    scheduler.setTriggerIntervalSeconds(seconds);
                    return "Trigger interval updated to " + seconds + " seconds.";
                case "TRIGGER":
                    scheduler.forceTriggerNow();
                    return "Manual trigger queued for next loop.";
                default:
                    return "Unknown command.";
            }
        } catch (Exception e) {
            return "Error processing command format.";
        }
    }
}
