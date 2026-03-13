package cli;

import core.Daemon;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Remote {
    private static final int PORT = 9333;

    public static void startServer(Daemon daemon) {
        new Thread(() -> {
            try (ServerSocket server = new ServerSocket(PORT)) {
                System.out.println("[Daemon] Control Server listening on port " + PORT);
                while (true) {
                    try (Socket client = server.accept();
                         PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                         BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()))) {
                        
                        String[] parts = in.readLine().split(" ");
                        switch (parts[0].toUpperCase()) {
                            case "STATUS": out.println("Silent active: " + daemon.isSilentNow()); break;
                            case "SILENT": daemon.enableSilent(Integer.parseInt(parts[1])); out.println("Silenced."); break;
                            case "SCHEDULE": daemon.setSchedule(Integer.parseInt(parts[1]), Integer.parseInt(parts[2])); out.println("Scheduled."); break;
                            case "INTERVAL": daemon.setInterval(Long.parseLong(parts[1])); out.println("Interval updated."); break;
                            case "TRIGGER": daemon.forceTrigger(); out.println("Trigger queued."); break;
                            default: out.println("Unknown command.");
                        }
                    } catch (Exception ignored) {}
                }
            } catch (Exception e) { System.out.println("Port 9333 busy. Daemon already running?"); }
        }).start();
    }

    public static void startMenu() {
        Scanner s = new Scanner(System.in);
        while (true) {
            System.out.println("\n==== DaemonV Menu ====\n1. Silence (minutes)\n2. Set Interval (seconds)\n3. Force Trigger\n4. Exit");
            System.out.print("> ");
            String input = s.nextLine();
            if (input.equals("4")) return;
            
            try {
                if (input.equals("1")) { System.out.print("Minutes: "); send("SILENT " + s.nextLine()); }
                else if (input.equals("2")) { System.out.print("Seconds: "); send("INTERVAL " + s.nextLine()); }
                else if (input.equals("3")) { send("TRIGGER"); }
            } catch (Exception e) { System.out.println("Invalid input."); }
        }
    }

    private static void send(String cmd) {
        try (Socket socket = new Socket("localhost", PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            out.println(cmd);
            System.out.println("Daemon: " + in.readLine());
        } catch (Exception e) { System.out.println("Error: Daemon not running."); }
    }
}