package CRMver2;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class TicketService {
    public static final String STATUS_PENDING   = "Pending";
    public static final String STATUS_COMPLETED = "Completed";
    public static final String STATUS_ASSIGNED  = "Technician Assigned";
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

    /** Customer submits a new service request. */
    public void submitTicket(String customerId) throws IOException {
        System.out.println("\n--- [Module 2] Submit Service Request ---");
        Scanner sc = new Scanner(System.in);

        System.out.println("Category: 1. Filter Replacement  2. Leaking Repair  3. General Maintenance");
        System.out.print("Choose (1-3): ");
        String description = switch (sc.nextLine().trim()) {
            case "1" -> "Filter Replacement";
            case "2" -> "Leaking Repair";
            case "3" -> "General Maintenance";
            default  -> "General Service";
        };

        System.out.print("Priority (Low / Medium / High): ");
        String priority = sc.nextLine().trim();
        System.out.print("Additional details: ");
        String content = sc.nextLine().trim();

        // FIX: Remove the first 'null' argument so it uses the 9-parameter constructor
        Ticket t = new Ticket(
                customerId,
                "Pending",
                priority,
                "Not Assigned",
                java.time.LocalDate.now().toString(),
                "null",        // resolveTime
                description,
                content,
                "null"         // response
        );

        t.writeFile(t.toString());
        System.out.println("[SUCCESS] Ticket submitted! ID: " + t.getId());
    }

    /** Customer tracks their own tickets. */
    public void trackTicketStatus(String customerId) throws IOException {
        System.out.println("\n--- [Module 2] Track My Tickets ---");
        ArrayList<Ticket> tickets = loadTicketToList();
        boolean found = false;
        printTableHeader();
        for (Ticket t : tickets) {
            if (t.getCustomerID() != null && t.getCustomerID().equalsIgnoreCase(customerId)) {
                printRow(t);
                found = true;
            }
        }
        printTableFooter();
        if (!found) System.out.println("No tickets found for: " + customerId);
    }

    /** Customer closes ticket and gives feedback. */
    public void closeTicketAndFeedback(String customerId) throws IOException {
        System.out.println("\n--- [Module 2] Close Ticket & Give Feedback ---");
        ArrayList<Ticket> tickets = loadTicketToList();
        Scanner sc = new Scanner(System.in);
        boolean found = false;

        for (Ticket t : tickets) {
            if (t.getCustomerID() != null
                    && t.getCustomerID().equalsIgnoreCase(customerId)
                    && !STATUS_COMPLETED.equalsIgnoreCase(t.getTicketStatus())) {
                t.setTicketStatus(STATUS_COMPLETED);
                t.setResolveTime(java.time.LocalDate.now().toString());
                System.out.print("Rate the technician (1-5 stars): ");
                t.setResponse("Customer Rating: " + sc.nextLine().trim() + " stars");
                System.out.println("[SUCCESS] Ticket closed. Thank you for your feedback!");
                found = true;
                break;
            }
        }
        if (!found) System.out.println("[ERROR] No active ticket found for your account.");
        rewriteAllTickets(tickets);
    }

    /** Staff creates a ticket manually. */
    public void createTicket() throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Customer ID  : ");
        String customerID = sc.nextLine().trim();
        System.out.print("Priority (Low/Medium/High): ");
        String priority = sc.nextLine().trim().toUpperCase();
        System.out.print("Enter Description  : ");
        String description = sc.nextLine().trim();

        Ticket t = new Ticket(null, customerID, STATUS_PENDING, priority,
                "Not Assigned", java.time.LocalDate.now().toString(),
                null, description, null, null);
        t.writeFile(t.toString());
        System.out.println("[SUCCESS] Ticket created! ID: " + t.getId());
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Display helpers
    // ──────────────────────────────────────────────────────────────────────────
    private void printTableHeader() {
        System.out.println("|====================================================================================================|");
        System.out.printf("|%-8s %-14s %-22s %-12s %-18s %-12s|%n",
                "ID", "Customer", "Ticket Status", "Priority", "Technician", "Date");
        System.out.println("|====================================================================================================|");
    }

    private void printTableFooter() {
        System.out.println("|====================================================================================================|\n");
    }

    private void printRow(Ticket t) {
        String pri = t.getPriorityLevel() != null ? t.getPriorityLevel() : "";
        if ("HIGH".equalsIgnoreCase(pri)) {
            System.out.printf("|%-8s %-14s %-22s " + RED + "%-12s" + RESET + " %-18s %-12s|%n",
                    t.getId(), t.getCustomerID(), t.getTicketStatus(),
                    pri, t.getTechnician(), t.getDate());
        } else {
            System.out.printf("|%-8s %-14s %-22s %-12s %-18s %-12s|%n",
                    t.getId(), t.getCustomerID(), t.getTicketStatus(),
                    pri, t.getTechnician(), t.getDate());
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
    public void rewriteAllTickets(ArrayList<Ticket> tickets) throws IOException {
        String path = System.getProperty("user.dir");
        File file = new File(path + File.separator + "ticket.txt");
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, false))) {
            for (Ticket t : tickets) {
                bw.write(t.toString());
                bw.newLine();
            }
        }
    }

    public ArrayList<Ticket> loadTicketToList() throws IOException {
        ArrayList<Ticket> list = new ArrayList<>();
        File file = new File("C:\\crmSystem\\ticket.txt");
        if (!file.exists()) return list;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",", -1);
                if (p.length >= 10) {
                    // USE THE RESTORATION CONSTRUCTOR HERE
                    // This keeps the ID from the file and doesn't touch the 'count'
                    Ticket t = new Ticket(p[0], p[1], p[2], p[3], p[4], p[5], p[6], p[7], p[8], p[9]);
                    list.add(t);
                }
            }
        }
        return list;
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