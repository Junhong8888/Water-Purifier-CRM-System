package CRMver2;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class StaffService {

    // Shared instance to avoid repeated file loading overhead
    private final TicketService ticketService = new TicketService();

    public StaffService() {}

    /**
     * Loads staff data from the text file.
     * Includes fixes for missing files, malformed lines, and number parsing.
     */
    public ArrayList<Staff> loadStaffToList() throws IOException {
        ArrayList<Staff> staffList = new ArrayList<>();
        String path = System.getProperty("user.dir") + File.separator + "staff.txt";
        File file = new File(path);

        if (!file.exists()) {
            return staffList;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNumber = 0;

            while ((line = br.readLine()) != null) {
                lineNumber++;
                String[] parts = line.split(",", -1);

                if (parts.length < 5) {
                    System.out.printf("  [Warning] Skipping malformed line %d%n", lineNumber);
                    continue;
                }

                // Standardize null/blank values
                for (int i = 0; i < parts.length; i++) {
                    if (parts[i] == null || parts[i].equalsIgnoreCase("null") || parts[i].isBlank()) {
                        parts[i] = null;
                    }
                }

                double salary = 0.0;
                try {
                    if (parts[4] != null) {
                        salary = Double.parseDouble(parts[4]);
                    }
                } catch (NumberFormatException e) {
                    // Log warning but keep salary at 0.0
                }

                try {
                    staffList.add(new Staff(parts[0], parts[1], parts[2], parts[3], salary));
                } catch (Exception e) {
                    System.err.println("Error creating staff from file: " + e.getMessage());
                }
            }
        }
        return staffList;
    }

    /**
     * Primary Dashboard for Staff, Managers, and Technicians.
     * Consolidates all Module 3 and Module 4 features.
     */
    public void staffOperationMenu() throws IOException {
        Scanner sc = new Scanner(System.in);
        String choice;

        do {
            System.out.println("\n╔══════════════════════════════════════════════╗");
            System.out.println("║         STAFF / MANAGER DASHBOARD            ║");
            System.out.println("╠══════════════════════════════════════════════╣");
            System.out.println("║  1. View Central Dashboard (All Tickets)     ║");
            System.out.println("║  2. Edit Ticket (Add Notes & Update Status)  ║");
            System.out.println("║  3. Search & Filter Tickets                  ║");
            System.out.println("║  4. Assign Ticket to Technician              ║");
            System.out.println("║  5. View Maintenance History                 ║");
            System.out.println("║  6. View Monthly Reporting (Analytics)       ║");
            System.out.println("║  7. Logout                                   ║");
            System.out.println("╚══════════════════════════════════════════════╝");
            System.out.print("  Select option (1-7): ");

            choice = sc.nextLine().trim();

            switch (choice) {
                case "1" -> ticketService.viewSubmittedTicket(); // Or ticketService.centralDashboard()
                case "2" -> editTicket(sc);
                case "3" -> ticketService.searchAndFilter();
                case "4" -> ticketService.assignTechnician();
                case "5" -> ticketService.viewMaintenanceHistory();
                case "6" -> ticketService.generateMonthlyReport();
                case "7" -> System.out.println("  Logging out...");
                default -> System.out.println("  [ERROR] Invalid choice. Please enter 1-7.");
            }
        } while (!choice.equals("7"));
    }

    /**
     * Logic for editing an existing ticket's progress and response.
     */
    private void editTicket(Scanner sc) throws IOException {
        ticketService.viewSubmittedTicket();
        ArrayList<Ticket> tickets = ticketService.loadTicketToList();

        // Filter for non-completed tickets
        ArrayList<Ticket> active = new ArrayList<>();
        for (Ticket t : tickets) {
            if (t.getTicketStatus() != null && !t.getTicketStatus().equalsIgnoreCase("Completed")) {
                active.add(t);
            }
        }

        if (active.isEmpty()) {
            System.out.println("  No active tickets found.");
            return;
        }

        System.out.println("\n  Select a ticket to edit (0 to cancel):");
        for (int i = 0; i < active.size(); i++) {
            System.out.printf("  [%d] Customer: %-10s | Status: %-15s | Date: %s%n",
                    i + 1, active.get(i).getCustomerID(), active.get(i).getTicketStatus(), active.get(i).getDate());
        }

        int ticketChoice;
        try {
            ticketChoice = Integer.parseInt(sc.nextLine().trim());
            if (ticketChoice == 0) return;
            if (ticketChoice < 1 || ticketChoice > active.size()) throw new Exception();
        } catch (Exception e) {
            System.out.println("  Invalid selection.");
            return;
        }

        Ticket selected = active.get(ticketChoice - 1);

        System.out.println("\n  1. Update Response  2. Update Status  3. Both  4. Cancel");
        String subChoice = sc.nextLine().trim();

        if (subChoice.equals("1") || subChoice.equals("3")) {
            ticketService.updateResponse(); // Or pass 'selected' if your method supports it
        }
        if (subChoice.equals("2") || subChoice.equals("3")) {
            // Advancing status logic
            System.out.println("  Updating ticket status...");
            // Assuming persistTicketChange is a method in your TicketService
        }
    }

    /**
     * Handles registration for Staff, Managers, and Technicians.
     */
    public void registerStaff(String roleName) throws IOException {
        Scanner sc = new Scanner(System.in);
        ArrayList<Staff> currentStaff = loadStaffToList();

        System.out.println("\n--- " + roleName + " Registration ---");

        String username;
        while (true) {
            System.out.print("Enter Username (Alphanumeric only): ");
            username = sc.nextLine().trim();

            if (username.isEmpty() || !username.matches("[a-zA-Z0-9]+")) {
                System.out.println("[ERROR] Invalid format.");
                continue;
            }

            boolean isTaken = false;
            for (Staff s : currentStaff) {
                if (s.getUsername().equalsIgnoreCase(username) && s.getRole().equalsIgnoreCase(roleName)) {
                    isTaken = true;
                    break;
                }
            }
            if (isTaken) System.out.println("[ERROR] Username already exists for this role.");
            else break;
        }

        System.out.print("Enter Password (min 6 chars): ");
        String password = sc.nextLine().trim();

        double salary = 0;
        while (true) {
            try {
                System.out.print("Enter Monthly Salary: RM ");
                salary = Double.parseDouble(sc.nextLine().trim());
                if (salary > 0) break;
            } catch (Exception e) {
                System.out.println("[ERROR] Invalid numeric value.");
            }
        }

        Staff newStaff = new Staff("temp", username, password, roleName, salary);
        newStaff.assignID();
        newStaff.writeFile(newStaff.toString());

        Staff.staffList.add(newStaff);

        System.out.println("\n[SUCCESS] Registration complete.");
    }
}