package CRM;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class StaffService {

    // FIX 7: single shared instance — not recreated on every menu call
    private final TicketService ticketService = new TicketService();

    public StaffService() {}

    // ---------------------------------------------------------------
    // LOAD STAFF FROM FILE
    // FIX 1: returns empty list if file doesn't exist (first run)
    // FIX 2: split(",", -1) keeps trailing empty fields
    // FIX 3: length check before accessing array indices
    // FIX 4: try-catch around Double.parseDouble
    // ---------------------------------------------------------------
    public ArrayList<Staff> loadStaffToList() throws IOException {
        ArrayList<Staff> staffList = new ArrayList<>();
        File file = new File("C:\\crmSystem\\staff.txt");

        // FIX 1: gracefully handle first run — no file yet
        if (!file.exists()) {
            System.out.println("  Notice: staff.txt not found — starting with empty staff list.");
            return staffList;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNumber = 0;

            while ((line = br.readLine()) != null) {
                lineNumber++;

                // FIX 2: -1 keeps trailing empty fields
                String[] parts = line.split(",", -1);

                // FIX 3: skip malformed lines with a warning
                if (parts.length < 5) {
                    System.out.printf("  Warning: skipping malformed staff line %d " +
                                    "(expected 5 fields, got %d): %s%n",
                            lineNumber, parts.length, line);
                    continue;
                }

                // Replace blank/null strings with null
                for (int i = 0; i < parts.length; i++) {
                    if (parts[i].equalsIgnoreCase("null") || parts[i].isBlank()) {
                        parts[i] = null;
                    }
                }

                // FIX 4: catch bad salary values instead of crashing
                double salary = 0.0;
                try {
                    if (parts[4] != null) {
                        salary = Double.parseDouble(parts[4]);
                    }
                } catch (NumberFormatException e) {
                    System.out.printf("  Warning: invalid salary on line %d (\"%s\") " +
                            "— defaulting to 0.0%n", lineNumber, parts[4]);
                }

                try {
                    staffList.add(new Staff(parts[0], parts[1], parts[2], parts[3], salary));
                } catch (IOException e) {
                    System.out.printf("  Warning: could not create Staff from line %d: %s%n",
                            lineNumber, e.getMessage());
                }
            }
        }

        return staffList;
    }

    // ---------------------------------------------------------------
    // STAFF OPERATION MENU
    // FIX 5: case 2 now fully implemented — select ticket, respond,
    //         update status
    // FIX 6: try-catch on menu input — no crash on bad input
    // FIX 7: reuses shared ticketService instance
    // ---------------------------------------------------------------
    public void staffOperationMenu() throws IOException {
        Scanner sc = new Scanner(System.in);
        int choice = -1;

        do {
            System.out.println("\n╔══════════════════════════════════╗");
            System.out.println("║     STAFF OPERATION MENU         ║");
            System.out.println("╠══════════════════════════════════╣");
            System.out.println("║  1. View Submitted Tickets       ║");
            System.out.println("║  2. Edit Ticket                  ║");
            System.out.println("║  3. View Ticket History          ║");
            System.out.println("║  4. Search Ticket                ║");
            System.out.println("║  5. Exit                         ║");
            System.out.println("╚══════════════════════════════════╝");
            System.out.print("  Select option: ");

            // FIX 6: handle non-integer input gracefully
            try {
                choice = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("  Invalid input — please enter a number.");
                choice = -1;
                continue;
            }

            switch (choice) {
                case 1 -> ticketService.viewSubmittedTicket();
                case 2 -> editTicket(sc);        // FIX 5: fully implemented
                case 3 -> ticketService.viewTicketHistory();
                case 4 -> ticketService.searchTicket();
                case 5 -> System.out.println("  Exiting Staff Menu...");
                default -> System.out.println("  Invalid choice — please select 1 to 5.");
            }

        } while (choice != 5);
    }

    // ---------------------------------------------------------------
    // EDIT TICKET  (FIX 5)
    // Lets staff:
    //   1. See all active tickets
    //   2. Pick one by customer ID + date
    //   3. Add a response
    //   4. Advance its status via updateTicketStatus()
    // ---------------------------------------------------------------
    private void editTicket(Scanner sc) throws IOException {
        // Step 1: display active tickets so staff can choose
        ticketService.viewSubmittedTicket();

        ArrayList<Ticket> tickets = ticketService.loadTicketToList();

        // Filter to active tickets only
        ArrayList<Ticket> active = new ArrayList<>();
        for (Ticket t : tickets) {
            if (!t.getTicketStatus().equalsIgnoreCase("Completed")) {
                active.add(t);
            }
        }

        if (active.isEmpty()) {
            System.out.println("  No active tickets to edit.\n");
            return;
        }

        // Step 2: staff selects a ticket by number
        System.out.println("  Select a ticket to edit:");
        for (int i = 0; i < active.size(); i++) {
            System.out.printf("  [%d] Customer: %-12s  Priority: %-6s  Status: %-20s  Date: %s%n",
                    i + 1,
                    active.get(i).getCustomerID(),
                    active.get(i).getPriorityLevel(),
                    active.get(i).getTicketStatus(),
                    active.get(i).getDate());
        }

        int ticketChoice = -1;
        while (ticketChoice < 1 || ticketChoice > active.size()) {
            System.out.print("\n  Enter ticket number (0 to cancel): ");
            try {
                ticketChoice = Integer.parseInt(sc.nextLine().trim());
                if (ticketChoice == 0) {
                    System.out.println("  Edit cancelled.");
                    return;
                }
                if (ticketChoice < 1 || ticketChoice > active.size()) {
                    System.out.println("  Invalid number — please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("  Please enter a valid number.");
            }
        }

        Ticket selected = active.get(ticketChoice - 1);

        // Step 3: show edit sub-menu for the chosen ticket
        int editChoice = -1;
        do {
            System.out.println("\n  Editing ticket (Customer: " + selected.getCustomerID()
                    + " | Status: " + selected.getTicketStatus() + ")");
            System.out.println("  ─────────────────────────────────");
            System.out.println("  1. Add / Update Response");
            System.out.println("  2. Advance Ticket Status");
            System.out.println("  3. Do Both (Response + Status)");
            System.out.println("  4. Back");
            System.out.print("  Choice: ");

            try {
                editChoice = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("  Invalid input.");
                continue;
            }

            switch (editChoice) {
                case 1 -> ticketService.addResponse(selected);
                case 2 -> {
                    ticketService.updateTicketStatus(selected);
                    ticketService.persistTicketChange(selected);
                }
                case 3 -> {
                    ticketService.addResponse(selected);
                    ticketService.updateTicketStatus(selected);
                    ticketService.persistTicketChange(selected);
                }
                case 4 -> System.out.println("  Returning to Staff Menu...");
                default -> System.out.println("  Invalid choice.");
            }

        } while (editChoice != 4);
    }
}