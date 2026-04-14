package CRMver2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class TicketService {
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";

    public TicketService() {}

    // ============================================
    // CORE FUNCTIONALITY & VIEW METHODS
    // ============================================

    public void viewSubmittedTicket() throws IOException {
        ArrayList<Ticket> tickets = loadTicketToList()
                .stream()
                .filter(t -> !t.getTicketStatus().equalsIgnoreCase("Completed"))
                .collect(Collectors.toCollection(ArrayList::new));

        if (tickets.isEmpty()) {
            System.out.println("\n  No active tickets found.\n");
            return;
        }

        printTicketTable("ACTIVE TICKETS", tickets);
    }

    public void viewTicketHistory() throws IOException {
        ArrayList<Ticket> tickets = loadTicketToList()
                .stream()
                .filter(t -> t.getTicketStatus().equalsIgnoreCase("Completed"))
                .collect(Collectors.toCollection(ArrayList::new));

        if (tickets.isEmpty()) {
            System.out.println("\n  No completed tickets found.\n");
            return;
        }

        printTicketTable("TICKET HISTORY (Completed)", tickets);
    }

    public void searchTicket() throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.print("  Enter keyword to search: ");
        String keyword = sc.nextLine().trim();

        if (keyword.isEmpty()) {
            System.out.println("  Keyword cannot be empty.");
            return;
        }

        ArrayList<Ticket> results = loadTicketToList()
                .stream()
                .filter(t -> matchesKeyword(t, keyword))
                .collect(Collectors.toCollection(ArrayList::new));

        if (results.isEmpty()) {
            System.out.println("  No tickets found matching: \"" + keyword + "\"\n");
        } else {
            printTicketTable("SEARCH RESULTS", results);
        }
    }

    private boolean matchesKeyword(Ticket t, String keyword) {
        String k = keyword.toLowerCase();
        return (t.getCustomerID()    != null && t.getCustomerID().toLowerCase().contains(k))
                || (t.getTicketStatus()  != null && t.getTicketStatus().toLowerCase().contains(k))
                || (t.getPriorityLevel() != null && t.getPriorityLevel().toLowerCase().contains(k))
                || (t.getTechnician()    != null && t.getTechnician().toLowerCase().contains(k))
                || (t.getDescription()   != null && t.getDescription().toLowerCase().contains(k));
    }

    // ============================================
    // MODULE 2: Service Request (Ticket) Management
    // ============================================

    public void submitTicket(String customerId) throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.println("\n--- Submit Service Request ---");
        System.out.print("Enter Issue Description: ");
        String desc = sc.nextLine();
        System.out.print("Priority (Low/Medium/High): ");
        String priority = sc.nextLine();

        String date = java.time.LocalDate.now().toString();

        Ticket newTicket = new Ticket(customerId, "Pending", priority, "Unassigned", date, "N/A", desc, "N/A", "N/A");
        newTicket.writeFile(newTicket.toString());
        System.out.println("  ✓ Ticket submitted successfully.");
    }

    public void trackTicketStatus(String customerId) throws IOException {
        ArrayList<Ticket> myTickets = loadTicketToList().stream()
                .filter(t -> t.getCustomerID().equalsIgnoreCase(customerId))
                .collect(Collectors.toCollection(ArrayList::new));

        if (myTickets.isEmpty()) {
            System.out.println("  No tickets found for your account.");
        } else {
            printTicketTable("YOUR SERVICE REQUESTS", myTickets);
        }
    }

    // ============================================
    // MODULE 3: Staff Operations & Tech Dispatch
    // ============================================

    public void assignTechnician() throws IOException {
        Scanner sc = new Scanner(System.in);
        ArrayList<Ticket> tickets = loadTicketToList();

        System.out.print("Enter Customer ID of the ticket to assign: ");
        String cid = sc.nextLine();

        Ticket target = tickets.stream()
                .filter(t -> t.getCustomerID().equalsIgnoreCase(cid) && !t.getTicketStatus().equalsIgnoreCase("Completed"))
                .findFirst().orElse(null);

        if (target != null) {
            System.out.print("Enter Technician ID: ");
            String tech = sc.nextLine();
            target.setTechnician(tech);
            target.setTicketStatus("Technician Assigned");
            persistTicketChange(target);
            System.out.println("  ✓ Technician assigned successfully.");
        } else {
            System.out.println("  Ticket not found or already completed.");
        }
    }

    public void updateTicketStatus(Ticket ticket) throws IOException {
        if (ticket == null) return;

        Map<String, String> transitions = new LinkedHashMap<>();
        transitions.put("pending",             "Technician Assigned");
        transitions.put("technician assigned", "Completed");

        String current = ticket.getTicketStatus().toLowerCase();
        String next = transitions.get(current);

        if (next != null) {
            ticket.setTicketStatus(next);
            persistTicketChange(ticket);
            System.out.printf("  Status updated to [%s]%n", next);
        } else {
            System.out.println("  Ticket is already in a terminal state.");
        }
    }

    // ============================================
    // DATA PERSISTENCE & LOADING
    // ============================================

    public ArrayList<Ticket> loadTicketToList() throws IOException {
        ArrayList<Ticket> tickets = new ArrayList<>();
        String path = System.getProperty("user.dir") + File.separator + "ticket.txt";
        File file = new File(path);

        if (!file.exists()) return tickets;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length < 9) continue;

                for (int i = 0; i < parts.length; i++) {
                    if (parts[i].equalsIgnoreCase("null") || parts[i].isBlank()) {
                        parts[i] = null;
                    }
                }
                tickets.add(new Ticket(parts[0], parts[1], parts[2], parts[3], parts[4],
                        parts[5], parts[6], parts[7], parts[8]));
            }
        }
        Collections.sort(tickets);
        return tickets;
    }

    public void persistTicketChange(Ticket updated) throws IOException {
        ArrayList<Ticket> all = loadTicketToList();
        List<String> updatedLines = new ArrayList<>();
        boolean found = false;

        for (Ticket t : all) {
            if (!found && equals(t.getCustomerID(), updated.getCustomerID()) &&
                    equals(t.getDate(), updated.getDate())) {
                updatedLines.add(updated.toString());
                found = true;
            } else {
                updatedLines.add(t.toString());
            }
        }
        new Ticket().writeAllTickets(updatedLines);
    }

    private boolean equals(String a, String b) {
        return Objects.equals(a, b);
    }

    public void printTicketTable(String title, List<Ticket> tickets) {
        System.out.println("\n  [ " + title + " ]");
        System.out.println("  +---------------+---------------------+----------+------------------+--------------+");
        System.out.printf("  | %-13s | %-19s | %-8s | %-16s | %-12s |%n",
                "Customer ID", "Status", "Priority", "Technician", "Date");
        System.out.println("  +---------------+---------------------+----------+------------------+--------------+");

        for (Ticket t : tickets) {
            String priority = t.getPriorityLevel() != null ? t.getPriorityLevel() : "-";
            String color = priority.equalsIgnoreCase("HIGH") ? RED :
                    priority.equalsIgnoreCase("MEDIUM") ? YELLOW : GREEN;

            System.out.printf("  | %-13s | %-19s | %s | %-16s | %-12s |%n",
                    nvl(t.getCustomerID()), nvl(t.getTicketStatus()),
                    color + String.format("%-8s", priority) + RESET,
                    nvl(t.getTechnician()), nvl(t.getDate()));
        }
        System.out.println("  +---------------+---------------------+----------+------------------+--------------+\n");
    }

    private String nvl(String value) {
        return value != null ? value : "-";
    }

    public void updateResponse() {
        System.out.println("Updating ticket response...");
    }

    public void searchAndFilter() {
        System.out.println("");
    }

    public void viewMaintenanceHistory() {
    }

    public void generateMonthlyReport() {
    }
}