package cli;

import core.Daemon;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Remote {
    private static final int PORT = 9333;
    private static volatile boolean serverRunning = true;

    public static void startServer(Daemon daemon) {
        new Thread(() -> {
            try (ServerSocket server = new ServerSocket(PORT)) {
                System.out.println("[Daemon] Control Server listening on port " + PORT);
                while (serverRunning) {
                    try (Socket client = server.accept();
                         PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                         BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()))) {
                        
                        String line = in.readLine();

                        if (line == null || line.isBlank()) {
                            out.println("Invalid command.");
                            continue;
                        }
                            
                        String[] parts = line.trim().split("\\s+");
                        String cmd = parts[0].toUpperCase();
                        switch (cmd) {
                            case "STATUS": out.println("Silent active: " + daemon.isSilentNow()); break;
                            case "SILENT":
                                if (parts.length < 2) {
                                     out.println("Usage: SILENT <minutes>");
                                     break;
                                    }
                                daemon.enableSilent(Integer.parseInt(parts[1]));
                                out.println("Silenced.");
                                break;
                            case "SCHEDULE":
                                if (parts.length < 3) {
                                    out.println("Usage: SCHEDULE <start> <end>");
                                        break;
                                    }
                                daemon.setSchedule(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
                                out.println("Scheduled.");
                                break;
                            case "INTERVAL":
                                if (parts.length < 2) {
                                    out.println("Usage: INTERVAL <seconds>");
                                    break;
                                }
                                daemon.setInterval(Long.parseLong(parts[1]));
                                out.println("Interval updated.");
                                break;
                            case "TRIGGER":
                                if (parts.length > 1) {
                                    out.println("Usage: TRIGGER");
                                    break;
                                }
                                daemon.forceTrigger();
                                out.println("Trigger queued.");
                                break;
                            case "EXIT": 
                                out.println("Daemon shutting down... Goodbye!"); 
                                daemon.stop(); 
                                serverRunning = false;
                                break;
                                
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
            System.out.println("\n==== DaemonV Menu ====\n1. Silence (minutes)\n2. Set Interval (seconds)\n3. Force Trigger\n4. Status\n5. Exit Daemon\n6. Exit CLI");
            System.out.print("> ");
            String input = s.nextLine();
            if (input.equals("6")) return;
            
            try {
                if (input.equals("1")) { System.out.print("Minutes: "); send("SILENT " + s.nextLine()); }
                else if (input.equals("2")) { System.out.print("Seconds: "); send("INTERVAL " + s.nextLine()); }
                else if (input.equals("3")) { send("TRIGGER"); }
                else if(input.equals("4")) { send("STATUS"); }
                else if(input.equals("5")) { send("EXIT"); System.out.println("Waiting for Daemon to stop...");return; }
                else { System.out.println("Invalid option."); }
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