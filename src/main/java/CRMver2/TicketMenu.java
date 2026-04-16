package CRMver2;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * TicketMenu — UI LAYER
 * Responsibility: ALL user interaction for the ticket module.
 * Handles Scanner input and System.out display.
 * Calls TicketService for logic. Never touches files directly.
 *
 * Flow: TicketMenu → TicketService → TicketRepository → ticket.txt
 */
public class TicketMenu {

    public static final String RESET  = "\u001B[0m";
    public static final String RED    = "\u001B[31m";
    public static final String GREEN  = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";

    private final TicketService service = new TicketService();

    public TicketMenu() {}

    // ================================================================
    // MODULE 2 — Customer-facing menus
    // ================================================================

    /** Asks customer for ticket details, calls service, shows result. */
    public void showSubmitTicket(String customerId) throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.println("\n--- [Module 2] Submit Service Request ---");
        System.out.println("  Category: 1. Filter Replacement  2. Leaking Repair  3. General Maintenance");
        System.out.print("  Choose (1-3): ");
        String description = switch (sc.nextLine().trim()) {
            case "1" -> "Filter Replacement";
            case "2" -> "Leaking Repair";
            case "3" -> "General Maintenance";
            default  -> "General Service";
        };

        System.out.print("  Priority (Low / Medium / High): ");
        String priority = sc.nextLine().trim();
        System.out.print("  Additional details: ");
        String content = sc.nextLine().trim();

        Ticket created = service.submitTicket(customerId, priority, description, content);
        System.out.println("  [SUCCESS] Ticket submitted! ID: " + created.getId());
    }

    /** Shows all tickets belonging to the logged-in customer. */
    public void showTrackTicketStatus(String customerId) throws IOException {
        System.out.println("\n--- [Module 2] Track My Tickets ---");
        ArrayList<Ticket> tickets = service.getTicketsByCustomer(customerId);
        if (tickets.isEmpty()) {
            System.out.println("  No tickets found for: " + customerId);
            return;
        }
        printTableHeader();
        for (Ticket t : tickets) printRow(t);
        printTableFooter();
    }

    /** Customer closes a ticket and rates the technician. */
    public void showCloseTicketAndFeedback(String customerId) throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.println("\n--- [Module 2] Close Ticket & Give Feedback ---");

        String rating = "";
        while (true) {
            System.out.print("  Rate the technician (1-5 stars): ");
            rating = sc.nextLine().trim();
            if (rating.matches("[1-5]")) break;
            System.out.println("  [ERROR] Enter a number between 1 and 5.");
        }

        boolean success = service.closeTicketAndFeedback(customerId, rating);
        if (success) {
            System.out.println("  [SUCCESS] Ticket closed. Thank you for your feedback!");
        } else {
            System.out.println("  [ERROR] No active ticket found for your account.");
        }
    }

    // ================================================================
    // MODULE 3 — Staff-facing menus
    // ================================================================

    /** Shows all active (non-completed) tickets. */
    public void showActiveTickets() throws IOException {
        System.out.println("\n--- [Module 3] Central Dashboard — All Active Tickets ---");
        ArrayList<Ticket> tickets = service.getActiveTickets();
        if (tickets.isEmpty()) {
            System.out.println("\n  No active tickets found.\n");
            return;
        }
        printTicketTable("ACTIVE TICKETS", tickets);
    }

    /**
     * FIX 2: now prompts for Ticket ID (not Customer ID).
     * Unambiguous — avoids "same customer, 2 tickets" problem.
     */
    public void showAssignTechnician() throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.println("\n--- [Module 3] Assign Ticket to Technician ---");

        // Show active tickets first so staff can see which ticket IDs exist
        showActiveTickets();

        System.out.print("  Enter Ticket ID to assign (e.g. t1): ");
        String ticketId = sc.nextLine().trim();
        System.out.print("  Enter Technician Username (e.g. Adam): ");
        String techUsername = sc.nextLine().trim();

        // FIX: passes ticketId to service instead of customerId
        Ticket updated = service.assignTechnician(ticketId, techUsername);
        if (updated != null) {
            System.out.println("  [SUCCESS] Ticket " + updated.getId()
                    + " assigned to " + updated.getTechnician());
        } else {
            System.out.println("  [ERROR] Ticket ID not found or already completed: " + ticketId);
        }
    }

    /** Shows active tickets then lets staff pick one to update notes/status. */
    public void showUpdateResponse() throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.println("\n--- [Module 3] Update Ticket Notes / Status ---");

        showActiveTickets();

        System.out.print("  Enter Ticket ID to update (e.g. t1): ");
        String ticketId = sc.nextLine().trim();
        System.out.print("  New Status (leave blank to keep current): ");
        String newStatus = sc.nextLine().trim();
        System.out.print("  Add Notes / Response (leave blank to keep current): ");
        String notes = sc.nextLine().trim();

        boolean success = service.updateTicketResponse(ticketId, newStatus, notes);
        if (success) {
            System.out.println("  [SUCCESS] Ticket " + ticketId + " updated.");
        } else {
            System.out.println("  [ERROR] Ticket ID not found: " + ticketId);
        }
    }

    /** Prompts for keyword and displays matching tickets. */
    public void showSearchAndFilter() throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.println("\n--- [Module 3] Search & Filter Tickets ---");
        System.out.print("  Enter keyword (Customer / Status / Priority / Technician / Description): ");
        String keyword = sc.nextLine().trim();

        if (keyword.isEmpty()) {
            System.out.println("  [ERROR] Keyword cannot be empty.");
            return;
        }

        ArrayList<Ticket> results = service.searchTickets(keyword);
        if (results.isEmpty()) {
            System.out.println("  No tickets found matching: \"" + keyword + "\"");
        } else {
            printTicketTable("SEARCH RESULTS for: " + keyword, results);
        }
    }

    // ================================================================
    // MODULE 4 — History & Analytics menus
    // ================================================================

    /** Shows all completed tickets (maintenance history). */
    public void showMaintenanceHistory() throws IOException {
        System.out.println("\n--- [Module 4] Purifier Maintenance History ---");
        ArrayList<Ticket> tickets = service.getCompletedTickets();
        if (tickets.isEmpty()) {
            System.out.println("\n  No completed tickets found.\n");
            return;
        }
        printTicketTable("MAINTENANCE HISTORY (Completed)", tickets);
    }

    /** Fetches report data from service and displays it. */
    public void showMonthlyReport() throws IOException {
        System.out.println("\n--- [Module 4] Monthly Analytics Report ---");
        TicketService.ReportData data = service.generateReport();

        if (data.total == 0) {
            System.out.println("  No ticket data available.");
            return;
        }

        System.out.println("  ╔══════════════════════════════════════╗");
        System.out.println("  ║        MONTHLY REPORT SUMMARY        ║");
        System.out.println("  ╠══════════════════════════════════════╣");
        System.out.printf ("  ║  Total Tickets Submitted : %-8d  ║%n", data.total);
        System.out.printf ("  ║  Resolved (Completed)    : %-8d  ║%n", data.resolved);
        System.out.printf ("  ║  In Progress / Assigned  : %-8d  ║%n", data.assigned);
        System.out.printf ("  ║  Still Pending           : %-8d  ║%n", data.pending);
        System.out.println("  ╠══════════════════════════════════════╣");
        System.out.printf ("  ║  " + RED    + "High Priority            : %-8d" + RESET + "  ║%n", data.highPri);
        System.out.printf ("  ║  " + YELLOW + "Medium Priority          : %-8d" + RESET + "  ║%n", data.medPri);
        System.out.printf ("  ║  " + GREEN  + "Low Priority             : %-8d" + RESET + "  ║%n", data.lowPri);
        System.out.println("  ╚══════════════════════════════════════╝");

        System.out.println("\n  --- Jobs Completed Per Technician ---");
        data.jobsPerTechnician.forEach((tech, count) ->
                System.out.printf("  %-20s : %d job(s)%n", tech, count));
    }

    // ================================================================
    // DISPLAY HELPERS — all System.out lives here
    // ================================================================

    public void printTicketTable(String title, java.util.List<Ticket> tickets) {
        System.out.println("\n  [ " + title + " ]");
        System.out.println("  +---------------+---------------------+----------+------------------+--------------+");
        System.out.printf("  | %-13s | %-19s | %-8s | %-16s | %-12s |%n",
                "Customer ID", "Status", "Priority", "Technician", "Date");
        System.out.println("  +---------------+---------------------+----------+------------------+--------------+");
        for (Ticket t : tickets) {
            String priority = t.getPriorityLevel() != null ? t.getPriorityLevel() : "-";
            String color = priority.equalsIgnoreCase("HIGH")   ? RED :
                    priority.equalsIgnoreCase("MEDIUM") ? YELLOW : GREEN;
            System.out.printf("  | %-13s | %-19s | %s | %-16s | %-12s |%n",
                    nvl(t.getCustomerID()), nvl(t.getTicketStatus()),
                    color + String.format("%-8s", priority) + RESET,
                    nvl(t.getTechnician()), nvl(t.getDate()));
        }
        System.out.println("  +---------------+---------------------+----------+------------------+--------------+\n");
    }

    private void printTableHeader() {
        System.out.println("  |====================================================================================================|");
        System.out.printf("  |%-8s %-14s %-22s %-12s %-18s %-12s|%n",
                "ID", "Customer", "Status", "Priority", "Technician", "Date");
        System.out.println("  |====================================================================================================|");
    }

    private void printTableFooter() {
        System.out.println("  |====================================================================================================|\n");
    }

    private void printRow(Ticket t) {
        String pri = t.getPriorityLevel() != null ? t.getPriorityLevel() : "";
        if ("HIGH".equalsIgnoreCase(pri)) {
            System.out.printf("  |%-8s %-14s %-22s " + RED + "%-12s" + RESET + " %-18s %-12s|%n",
                    nvl(t.getId()), nvl(t.getCustomerID()), nvl(t.getTicketStatus()),
                    pri, nvl(t.getTechnician()), nvl(t.getDate()));
        } else {
            System.out.printf("  |%-8s %-14s %-22s %-12s %-18s %-12s|%n",
                    nvl(t.getId()), nvl(t.getCustomerID()), nvl(t.getTicketStatus()),
                    pri, nvl(t.getTechnician()), nvl(t.getDate()));
        }
    }

    private String nvl(String value) { return value != null ? value : "-"; }
}
