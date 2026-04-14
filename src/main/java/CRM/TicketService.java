package CRM;

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

    // ---------------------------------------------------------------
    // VIEW SUBMITTED TICKETS — excludes Completed tickets
    // ---------------------------------------------------------------
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

    // ---------------------------------------------------------------
    // VIEW TICKET HISTORY — only Completed tickets
    // ---------------------------------------------------------------
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

    // ---------------------------------------------------------------
    // SEARCH TICKET
    // Bug fixes:
    //   1. Filter result was never assigned back — now it is
    //   2. "Invalid Input" printed on every non-match instead of
    //      only when zero results found — fixed with a found flag
    // ---------------------------------------------------------------
    public void searchTicket() throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.print("  Enter keyword to search: ");
        String keyword = sc.nextLine().trim();

        if (keyword.isEmpty()) {
            System.out.println("  Keyword cannot be empty.");
            return;
        }

        // FIX: assign the filtered result back into a variable
        ArrayList<Ticket> results = loadTicketToList()
                .stream()
                .filter(t -> !t.getTicketStatus().equalsIgnoreCase("Completed"))
                .filter(t -> matchesKeyword(t, keyword))
                .collect(Collectors.toCollection(ArrayList::new));

        if (results.isEmpty()) {
            // FIX: only print "not found" once, not per-ticket
            System.out.println("  No tickets found matching: \"" + keyword + "\"\n");
        } else {
            printTicketTable("SEARCH RESULTS for \"" + keyword + "\"", results);
        }
    }

    // Checks all relevant fields of a ticket against the keyword (case-insensitive)
    private boolean matchesKeyword(Ticket t, String keyword) {
        return (t.getCustomerID()    != null && t.getCustomerID().equalsIgnoreCase(keyword))
                || (t.getTicketStatus()  != null && t.getTicketStatus().equalsIgnoreCase(keyword))
                || (t.getPriorityLevel() != null && t.getPriorityLevel().equalsIgnoreCase(keyword))
                || (t.getTechnician()    != null && t.getTechnician().equalsIgnoreCase(keyword))
                || (t.getDate()          != null && t.getDate().equalsIgnoreCase(keyword))
                || (t.getDescription()   != null && t.getDescription().equalsIgnoreCase(keyword));
    }

    // ---------------------------------------------------------------
    // UPDATE TICKET STATUS
    // Bug fix: HashMap lookup found the current status key then set
    // the SAME status back. Fixed with a clear transition map.
    //
    // Transition order:
    //   Pending → Technician Assigned → Completed
    //   Completed stays Completed (terminal state)
    // ---------------------------------------------------------------
    public void updateTicketStatus(Ticket ticket) {
        if (ticket == null || ticket.getTicketStatus() == null) {
            System.out.println("  Cannot update status: ticket is null or has no status.");
            return;
        }

        // FIX: explicit transitions instead of a HashMap key lookup
        Map<String, String> transitions = new LinkedHashMap<>();
        transitions.put("pending",             "Technician Assigned");
        transitions.put("technician assigned", "Completed");
        transitions.put("completed",           "Completed"); // terminal — no further change

        String current = ticket.getTicketStatus().toLowerCase();
        String next = transitions.get(current);

        if (next == null) {
            System.out.printf("  Unknown status \"%s\" — no transition defined.%n",
                    ticket.getTicketStatus());
            return;
        }

        if (next.equalsIgnoreCase(ticket.getTicketStatus())) {
            System.out.println("  Ticket is already Completed — no further status change.");
            return;
        }

        System.out.printf("  Status updated: [%s] → [%s]%n",
                ticket.getTicketStatus(), next);
        ticket.setTicketStatus(next);
    }

    // ---------------------------------------------------------------
    // ADD RESPONSE TO TICKET
    // Prompts staff for a response string and saves it to the ticket.
    // Persists the change immediately by rewriting the file.
    // ---------------------------------------------------------------
    public void addResponse(Ticket ticket) throws IOException {
        if (ticket == null) {
            System.out.println("  No ticket selected.");
            return;
        }

        Scanner sc = new Scanner(System.in);
        System.out.printf("  Adding response to ticket (Customer: %s | Status: %s)%n",
                ticket.getCustomerID(), ticket.getTicketStatus());
        System.out.print("  Enter response: ");
        String response = sc.nextLine().trim();

        if (response.isEmpty()) {
            System.out.println("  Response cannot be empty. No changes made.");
            return;
        }

        ticket.setResponse(response);

        // Persist: reload all, replace the matching ticket, rewrite file
        persistTicketChange(ticket);
        System.out.println("  ✓ Response saved successfully.");
    }

    // ---------------------------------------------------------------
    // LOAD ALL TICKETS FROM FILE INTO A SORTED LIST
    // Bug fixes:
    //   1. split(",") drops trailing empty fields → use split(",",-1)
    //   2. ArrayIndexOutOfBoundsException if line has < 9 fields —
    //      now skips malformed lines with a warning
    // ---------------------------------------------------------------
    public ArrayList<Ticket> loadTicketToList() throws IOException {
        ArrayList<Ticket> tickets = new ArrayList<>();
        File file = new File("C:\\crmSystem\\ticket.txt");

        if (!file.exists()) {
            return tickets; // return empty list rather than crash
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNumber = 0;

            while ((line = br.readLine()) != null) {
                lineNumber++;
                // FIX: use -1 to keep trailing empty fields (e.g. empty response)
                String[] parts = line.split(",", -1);

                if (parts.length < 9) {
                    System.out.printf("  Warning: skipping malformed line %d " +
                                    "(expected 9+ fields, got %d): %s%n",
                            lineNumber, parts.length, line);
                    continue;
                }

                // Replace the string "null" with actual null for each field
                for (int i = 0; i < parts.length; i++) {
                    if (parts[i].equalsIgnoreCase("null") || parts[i].isBlank()) {
                        parts[i] = null;
                    }
                }

                try {
                    tickets.add(new Ticket(
                            parts[0],   // customerID
                            parts[1],   // ticketStatus
                            parts[2],   // priorityLevel
                            parts[3],   // technician
                            parts[4],   // date
                            parts[5],   // resolveTime
                            parts[6],   // description
                            parts[7],   // content
                            parts[8]    // response
                    ));
                } catch (Exception e) {
                    System.out.printf("  Warning: could not parse line %d: %s%n",
                            lineNumber, e.getMessage());
                }
            }
        }

        Collections.sort(tickets); // uses Ticket.compareTo()
        return tickets;
    }

    // ---------------------------------------------------------------
    // PERSIST A SINGLE TICKET CHANGE
    // Reloads all tickets, replaces the matching one, rewrites file.
    // Match key: customerID + date + description (no stored ID yet).
    // ---------------------------------------------------------------
    public void persistTicketChange(Ticket updated) throws IOException {
        ArrayList<Ticket> all = loadTicketToList();
        List<String> updatedLines = new ArrayList<>();
        boolean found = false;

        for (Ticket t : all) {
            boolean isMatch = !found
                    && equals(t.getCustomerID(),  updated.getCustomerID())
                    && equals(t.getDate(),         updated.getDate())
                    && equals(t.getDescription(),  updated.getDescription());

            if (isMatch) {
                updatedLines.add(updated.toString());
                found = true;
            } else {
                updatedLines.add(t.toString());
            }
        }

        if (!found) {
            System.out.println("  Warning: ticket not found in storage — no changes written.");
            return;
        }

        new Ticket().writeAllTickets(updatedLines);
    }

    // Null-safe string equality helper
    private boolean equals(String a, String b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        return a.equals(b);
    }

    // ---------------------------------------------------------------
    // SHARED TABLE PRINTER
    // Used by viewSubmittedTicket, viewTicketHistory, searchTicket
    // ---------------------------------------------------------------
    public void printTicketTable(String title, List<Ticket> tickets) {
        System.out.println("\n  [ " + title + " ]");
        System.out.println("  +---------------+---------------------+----------+------------------+--------------+");
        System.out.printf("  | %-13s | %-19s | %-8s | %-16s | %-12s |%n",
                "Customer ID", "Status", "Priority", "Technician", "Date");
        System.out.println("  +---------------+---------------------+----------+------------------+--------------+");

        for (Ticket t : tickets) {
            String priority = t.getPriorityLevel() != null ? t.getPriorityLevel() : "-";
            String coloured;
            if (priority.equalsIgnoreCase("HIGH")) {
                coloured = RED    + String.format("%-8s", priority) + RESET;
            } else if (priority.equalsIgnoreCase("MEDIUM")) {
                coloured = YELLOW + String.format("%-8s", priority) + RESET;
            } else {
                coloured = GREEN  + String.format("%-8s", priority) + RESET;
            }

            System.out.printf("  | %-13s | %-19s | %s | %-16s | %-12s |%n",
                    nvl(t.getCustomerID()),
                    nvl(t.getTicketStatus()),
                    coloured,
                    nvl(t.getTechnician()),
                    nvl(t.getDate()));
        }
        System.out.println("  +---------------+---------------------+----------+------------------+--------------+\n");
    }

    // Returns "-" for null values so the table never shows "null"
    private String nvl(String value) {
        return value != null ? value : "-";
    }
}