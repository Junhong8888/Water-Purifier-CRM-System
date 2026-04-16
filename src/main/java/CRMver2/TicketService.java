package CRMver2;



import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * TicketService — LOGIC LAYER
 * Responsibility: Business logic only.
 * NO Scanner, NO System.out, NO direct file I/O.
 * Calls TicketRepository for all storage needs.
 * Returns results to TicketMenu for display.
 */
public class TicketService {

    public static final String STATUS_PENDING   = "Pending";
    public static final String STATUS_COMPLETED = "Completed";
    public static final String STATUS_ASSIGNED  = "Technician Assigned";

    private final TicketRepository repo = new TicketRepository();

    public TicketService() {}

    // ================================================================
    // MODULE 2 — Customer Ticket Operations
    // ================================================================

    /**
     * Creates and saves a new ticket.
     * FIX 1: now calls repo.append(t) instead of t.writeFile()
     * so ALL file access stays inside the Repository layer.
     * Returns the created Ticket so TicketMenu can show its ID.
     */
    public Ticket submitTicket(String customerId, String priority,
                                       String description, String content) throws IOException {
        Ticket t = new Ticket(
                customerId,
                STATUS_PENDING,
                priority.toUpperCase(),
                "Not Assigned",
                java.time.LocalDate.now().toString(),
                null,
                description,
                content,
                null
        );
        repo.append(t); // FIX: was t.writeFile(t.toString()) — now fully through Repository
        return t;
    }

    /** Returns all tickets belonging to a specific customer. */
    public ArrayList<Ticket> getTicketsByCustomer(String customerId) throws IOException {
        ArrayList<Ticket> result = new ArrayList<>();
        for (Ticket t : repo.loadAll()) {
            if (t.getCustomerID() != null
                    && t.getCustomerID().equalsIgnoreCase(customerId)) {
                result.add(t);
            }
        }
        return result;
    }

    /**
     * Closes a customer's active ticket and saves their feedback rating.
     * Returns true if a ticket was found and closed.
     */
    public boolean closeTicketAndFeedback(String customerId, String rating) throws IOException {
        ArrayList<Ticket> all = repo.loadAll();
        for (Ticket t : all) {
            if (t.getCustomerID() != null
                    && t.getCustomerID().equalsIgnoreCase(customerId)
                    && !STATUS_COMPLETED.equalsIgnoreCase(t.getTicketStatus())) {
                t.setTicketStatus(STATUS_COMPLETED);
                t.setResolveTime(java.time.LocalDate.now().toString());
                t.setResponse("Customer Rating: " + rating + " stars");
                repo.saveAll(all);
                return true;
            }
        }
        return false;
    }

    // ================================================================
    // MODULE 3 — Staff Operations
    // ================================================================

    /** Returns all tickets that are NOT completed (active tickets). */
    public ArrayList<Ticket> getActiveTickets() throws IOException {
        return repo.loadAll().stream()
                .filter(t -> !STATUS_COMPLETED.equalsIgnoreCase(t.getTicketStatus()))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * FIX 2: now takes ticketId (not customerId).
     * Unambiguous — one ticket ID maps to exactly one ticket,
     * so same-customer/multiple-ticket scenario is fully handled.
     * Returns the updated Ticket, or null if not found.
     */
    public Ticket assignTechnician(String ticketId, String technicianUsername) throws IOException {
        ArrayList<Ticket> all = repo.loadAll();
        for (Ticket t : all) {
            if (t.getId() != null
                    && t.getId().equalsIgnoreCase(ticketId)
                    && !STATUS_COMPLETED.equalsIgnoreCase(t.getTicketStatus())) {
                t.setTechnician(technicianUsername);
                t.setTicketStatus(STATUS_ASSIGNED);
                repo.saveAll(all);
                return t;
            }
        }
        return null;
    }

    /**
     * Updates status and/or response notes on a specific ticket by ticket ID.
     * Returns true if ticket was found and updated.
     */
    public boolean updateTicketResponse(String ticketId, String newStatus,
                                        String notes) throws IOException {
        ArrayList<Ticket> all = repo.loadAll();
        for (Ticket t : all) {
            if (t.getId() != null && t.getId().equalsIgnoreCase(ticketId)) {
                if (newStatus != null && !newStatus.isEmpty()) t.setTicketStatus(newStatus);
                if (notes     != null && !notes.isEmpty())     t.setResponse(notes);
                repo.saveAll(all);
                return true;
            }
        }
        return false;
    }

    /**
     * Searches tickets by keyword across all fields.
     * Returns matching tickets — TicketMenu displays them.
     */
    public ArrayList<Ticket> searchTickets(String keyword) throws IOException {
        String k = keyword.toLowerCase();
        return repo.loadAll().stream()
                .filter(t ->
                        (t.getCustomerID()    != null && t.getCustomerID().toLowerCase().contains(k))
                                || (t.getTicketStatus()  != null && t.getTicketStatus().toLowerCase().contains(k))
                                || (t.getPriorityLevel() != null && t.getPriorityLevel().toLowerCase().contains(k))
                                || (t.getTechnician()    != null && t.getTechnician().toLowerCase().contains(k))
                                || (t.getDescription()   != null && t.getDescription().toLowerCase().contains(k)))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    // ================================================================
    // MODULE 4 — History & Analytics
    // ================================================================

    /** Returns all completed tickets. */
    public ArrayList<Ticket> getCompletedTickets() throws IOException {
        return repo.loadAll().stream()
                .filter(t -> STATUS_COMPLETED.equalsIgnoreCase(t.getTicketStatus()))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Computes analytics and returns a ReportData object.
     * TicketMenu is responsible for displaying the data.
     */
    public ReportData generateReport() throws IOException {
        ArrayList<Ticket> all = repo.loadAll();
        ReportData data = new ReportData();
        data.total    = all.size();
        data.resolved = all.stream().filter(t -> STATUS_COMPLETED.equalsIgnoreCase(t.getTicketStatus())).count();
        data.pending  = all.stream().filter(t -> STATUS_PENDING.equalsIgnoreCase(t.getTicketStatus())).count();
        data.assigned = all.stream().filter(t -> STATUS_ASSIGNED.equalsIgnoreCase(t.getTicketStatus())).count();
        data.highPri  = all.stream().filter(t -> "HIGH".equalsIgnoreCase(t.getPriorityLevel())).count();
        data.medPri   = all.stream().filter(t -> "MEDIUM".equalsIgnoreCase(t.getPriorityLevel())).count();
        data.lowPri   = all.stream().filter(t -> "LOW".equalsIgnoreCase(t.getPriorityLevel())).count();
        data.jobsPerTechnician = all.stream()
                .filter(t -> STATUS_COMPLETED.equalsIgnoreCase(t.getTicketStatus()))
                .collect(Collectors.groupingBy(
                        t -> t.getTechnician() != null ? t.getTechnician() : "Unassigned",
                        Collectors.counting()));
        return data;
    }

    /** Exposed for Staff.countAssignedTickets() compatibility. */
    public ArrayList<Ticket> loadTicketToList() throws IOException {
        return repo.loadAll();
    }

    // ================================================================
    // Inner class: Report data container (no display logic here)
    // ================================================================
    public static class ReportData {
        public long total, resolved, pending, assigned, highPri, medPri, lowPri;
        public Map<String, Long> jobsPerTechnician;
    }
}
