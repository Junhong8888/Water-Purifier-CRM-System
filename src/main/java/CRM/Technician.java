package CRM;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Technician extends Staff implements Menu{

    private final TicketService ticketService = new TicketService();

    public Technician() throws IOException {
    }

    public Technician(String id, String username, String password, String role, double salary)
            throws IOException {
        super(id, username, password, role, salary);
    }

    // ---------------------------------------------------------------
    // DASHBOARD – shows only tickets assigned to THIS technician,
    // sorted via Ticket.compareTo() (already done by loadTicketToList)
    // ---------------------------------------------------------------
    public void dashboard() throws IOException {
        List<Ticket> myTickets = ticketService.loadTicketToList()
                .stream()
                .filter(t -> t.getTechnician() != null
                        && t.getTechnician().equalsIgnoreCase(this.getUsername()))
                .collect(Collectors.toList());

        if (myTickets.isEmpty()) {
            System.out.println("\n  No tickets are currently assigned to you.\n");
            return;
        }

        // Split into active vs completed for clearer display
        List<Ticket> active = myTickets.stream()
                .filter(t -> !t.getTicketStatus().equalsIgnoreCase("Completed"))
                .collect(Collectors.toList());

        List<Ticket> completed = myTickets.stream()
                .filter(t -> t.getTicketStatus().equalsIgnoreCase("Completed"))
                .collect(Collectors.toList());

        System.out.println("\n|============================================================" +
                "==============================|");
        System.out.printf("  Technician Dashboard — Logged in as: %s%n", this.getUsername());
        System.out.println("|================================================================" +
                "============================|");

        System.out.printf("  Active: %d ticket(s)    Completed: %d ticket(s)    " +
                        "Total: %d ticket(s)%n",
                active.size(), completed.size(), myTickets.size());

        // --- Active tickets ---
        if (!active.isEmpty()) {
            System.out.println("\n  [ ACTIVE TICKETS ]");
            printTicketTable(active);
        }

        // --- Completed tickets ---
        if (!completed.isEmpty()) {
            System.out.println("\n  [ COMPLETED TICKETS ]");
            printTicketTable(completed);
        }

        System.out.println();
    }

    // Reusable table printer — avoids duplicate formatting code
    private void printTicketTable(List<Ticket> tickets) {
        System.out.println("  +--------------+-------------------+----------+------------+-------------+");
        System.out.printf("  | %-12s | %-17s | %-8s | %-10s | %-11s |%n",
                "Customer ID", "Status", "Priority", "Date", "Description");
        System.out.println("  +--------------+-------------------+----------+------------+-------------+");

        for (Ticket t : tickets) {
            String priority = t.getPriorityLevel() != null ? t.getPriorityLevel() : "-";
            String coloured = priority.equalsIgnoreCase("HIGH")
                    ? TicketService.RED + String.format("%-8s", priority) + TicketService.RESET
                    : String.format("%-8s", priority);

            // Description capped at 11 chars to fit column
            String desc = t.getDescription() != null
                    ? t.getDescription().length() > 11
                    ? t.getDescription().substring(0, 8) + "..."
                    : t.getDescription()
                    : "-";

            System.out.printf("  | %-12s | %-17s | %s | %-10s | %-11s |%n",
                    t.getCustomerID(),
                    t.getTicketStatus(),
                    coloured,
                    t.getDate(),
                    desc);
        }
        System.out.println("  +--------------+-------------------+----------+------------+-------------+");
    }

    // ---------------------------------------------------------------
    // ASSIGN TICKET – reassigns one of this technician's active
    // tickets to another technician and rewrites ticket.txt
    // ---------------------------------------------------------------
    public void assignTicketToOther() throws IOException {
        Scanner sc = new Scanner(System.in);

        // 1. Load only this technician's ACTIVE tickets
        List<Ticket> myActive = ticketService.loadTicketToList()
                .stream()
                .filter(t -> t.getTechnician() != null
                        && t.getTechnician().equalsIgnoreCase(this.getUsername())
                        && !t.getTicketStatus().equalsIgnoreCase("Completed"))
                .collect(Collectors.toList());

        if (myActive.isEmpty()) {
            System.out.println("\n  You have no active tickets to reassign.\n");
            return;
        }

        // 2. Display numbered list so user doesn't need to type an ID
        System.out.println("\n  [ YOUR ACTIVE TICKETS ]");
        printTicketTable(myActive);
        System.out.println();
        for (int i = 0; i < myActive.size(); i++) {
            Ticket t = myActive.get(i);
            System.out.printf("  [%d] Customer: %-12s  Priority: %-6s  Desc: %s%n",
                    i + 1,
                    t.getCustomerID(),
                    t.getPriorityLevel(),
                    t.getDescription());
        }

        // 3. Choose ticket by number
        int ticketChoice = -1;
        while (ticketChoice < 1 || ticketChoice > myActive.size()) {
            System.out.print("\n  Select ticket number to reassign (0 to cancel): ");
            try {
                ticketChoice = Integer.parseInt(sc.nextLine().trim());
                if (ticketChoice == 0) {
                    System.out.println("  Reassignment cancelled.");
                    return;
                }
                if (ticketChoice < 1 || ticketChoice > myActive.size()) {
                    System.out.println("  Invalid number. Try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("  Please enter a valid number.");
            }
        }
        Ticket chosen = myActive.get(ticketChoice - 1);

        // 4. Show available technicians from staffList (excluding self)
        List<Staff> otherTechs = new ArrayList<>();
        for (Staff s : staffList) {
            if (s instanceof Technician
                    && !s.getUsername().equalsIgnoreCase(this.getUsername())) {
                otherTechs.add(s);
            }
        }

        if (otherTechs.isEmpty()) {
            System.out.println("\n  No other technicians available for reassignment.\n");
            return;
        }

        System.out.println("\n  [ AVAILABLE TECHNICIANS ]");
        for (int i = 0; i < otherTechs.size(); i++) {
            System.out.printf("  [%d] %s (ID: %s)%n",
                    i + 1,
                    otherTechs.get(i).getUsername(),
                    otherTechs.get(i).getId());
        }

        // 5. Choose target technician by number
        int techChoice = -1;
        while (techChoice < 1 || techChoice > otherTechs.size()) {
            System.out.print("\n  Select technician number (0 to cancel): ");
            try {
                techChoice = Integer.parseInt(sc.nextLine().trim());
                if (techChoice == 0) {
                    System.out.println("  Reassignment cancelled.");
                    return;
                }
                if (techChoice < 1 || techChoice > otherTechs.size()) {
                    System.out.println("  Invalid number. Try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("  Please enter a valid number.");
            }
        }
        String newTechName = otherTechs.get(techChoice - 1).getUsername();

        // 6. Read all raw lines, find and update the matching ticket line
        List<String> allLines = ticketService.loadTicketToList()
                .stream()
                .map(Ticket::toString)  // rebuild lines from objects
                .collect(Collectors.toList());

        // We match by customerID + date + description (unique enough without stored ID)
        List<String> updatedLines = new ArrayList<>();
        boolean updated = false;

        for (Ticket t : ticketService.loadTicketToList()) {
            if (!updated
                    && t.getCustomerID().equals(chosen.getCustomerID())
                    && t.getDate().equals(chosen.getDate())
                    && t.getTechnician().equalsIgnoreCase(this.getUsername())
                    && t.getDescription().equals(chosen.getDescription())) {

                t.setTechnician(newTechName);
                updatedLines.add(t.toString());
                updated = true;
            } else {
                updatedLines.add(t.toString());
            }
        }

        if (updated) {
            // writeAllTickets rewrites the entire file cleanly
            new Ticket().writeAllTickets(updatedLines);
            System.out.printf("%n  ✓ Ticket successfully reassigned from [%s] → [%s]%n%n",
                    this.getUsername(), newTechName);
        } else {
            System.out.println("\n  Error: Could not locate the ticket in storage.\n");
        }
    }

    // ---------------------------------------------------------------
    // MENU
    // ---------------------------------------------------------------
    @Override
    public void displayMenu() throws IOException {
        Scanner sc = new Scanner(System.in);
        int choice = -1;

        do {
            System.out.println("\n╔══════════════════════════════╗");
            System.out.println("║      TECHNICIAN MENU         ║");
            System.out.println("╠══════════════════════════════╣");
            System.out.println("║  1. Dashboard                ║");
            System.out.println("║  2. Assign Ticket to Other   ║");
            System.out.println("║  3. Exit                     ║");
            System.out.println("╚══════════════════════════════╝");
            System.out.print("  Select option: ");

            try {
                choice = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("  Invalid input — please enter 1, 2, or 3.");
                choice = -1;
                continue;
            }

            switch (choice) {
                case 1 -> dashboard();
                case 2 -> assignTicketToOther();
                case 3 -> System.out.println("  Logging out...");
                default -> System.out.println("  Invalid choice — please select 1 to 3.");
            }

        } while (choice != 3);
    }

}