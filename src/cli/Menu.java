package cli;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Menu {

    private final Scanner scanner = new Scanner(System.in);
    private static final int PORT = 9333;

    public void start() {
        while (true) {
            System.out.println("\n==== DaemonV Remote Control ====");
            System.out.println("1. Silence Control");
            System.out.println("2. Trigger & Timing Control");
            System.out.println("3. Exit");
            System.out.print("Select option: ");

            try {
                String input = scanner.nextLine();
                if (input.isEmpty()) continue;

                int choice = Integer.parseInt(input);

                switch (choice) {
                    case 1 -> showSilenceMenu();
                    case 2 -> showTriggerMenu();
                    case 3 -> {
                        System.out.println("Exiting menu.");
                        return; // Exits the menu program completely
                    }
                    default -> System.out.println("Invalid choice.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private void showSilenceMenu() {
        while (true) {
            System.out.println("\n==== DaemonV Silence Control ====");
            System.out.println("1. Check Silent Status");
            System.out.println("2. Enable Silent (minutes)");
            System.out.println("3. Set Scheduled Silent Window");
            System.out.println("4. Go Back");
            System.out.print("Select option: ");

            try {
                String input = scanner.nextLine();
                if (input.isEmpty()) continue;

                int choice = Integer.parseInt(input);

                switch (choice) {
                    case 1 -> sendCommand("STATUS");
                    case 2 -> {
                        System.out.print("Enter minutes: ");
                        sendCommand("SILENT " + scanner.nextLine());
                    }
                    case 3 -> {
                        System.out.print("Start hour (0-23): ");
                        String start = scanner.nextLine();
                        System.out.print("End hour (0-23): ");
                        String end = scanner.nextLine();
                        sendCommand("SCHEDULE " + start + " " + end);
                    }
                    case 4 -> {
                        return; // Returns to the Main Menu
                    }
                    default -> System.out.println("Invalid choice.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private void showTriggerMenu() {
        while (true) {
            System.out.println("\n==== DaemonV Trigger Control ====");
            System.out.println("1. Change Trigger Interval (seconds)");
            System.out.println("2. Manual Trigger Now");
            System.out.println("3. Go Back");
            System.out.print("Select option: ");

            try {
                String input = scanner.nextLine();
                if (input.isEmpty()) continue;

                int choice = Integer.parseInt(input);

                switch (choice) {
                    case 1 -> {
                        System.out.print("Enter interval seconds: ");
                        sendCommand("INTERVAL " + scanner.nextLine());
                    }
                    case 2 -> sendCommand("TRIGGER");
                    case 3 -> {
                        return; // Returns to the Main Menu
                    }
                    default -> System.out.println("Invalid choice.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private void sendCommand(String command) {
        try (Socket socket = new Socket("localhost", PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println(command);
            System.out.println("Daemon Response: " + in.readLine());

        } catch (Exception e) {
            System.out.println("Error: Could not connect to DaemonV. Is the background daemon currently running?");
        }
    }
}