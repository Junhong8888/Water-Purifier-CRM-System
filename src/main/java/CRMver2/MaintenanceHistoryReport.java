package CRMver2;

import java.util.List;
import java.util.stream.Collectors;

public class MaintenanceHistoryReport extends Report {
    private Customer targetCustomer; // Class Association!

    public MaintenanceHistoryReport(Customer targetCustomer) {
        super("Maintenance History Report");
        this.targetCustomer = targetCustomer;
    }

    @Override
    public void generateReport(List<Ticket> tickets) {
        System.out.println("\n  --- Complete Maintenance History for " + targetCustomer.getUsername() + " ---");
        System.out.println("  Unit Model: " + targetCustomer.getPurifierModel());

        // Filters tickets specifically for this customer
        List<CRMver2.Ticket> history = tickets.stream()
                .filter(t -> t.getCustomerID().equalsIgnoreCase(targetCustomer.getId()) || 
                             t.getCustomerID().equalsIgnoreCase(targetCustomer.getUsername()))
                .collect(Collectors.toList());

        if (history.isEmpty()) {
            System.out.println("  No past service records found.");
            return;
        }

        for (Ticket t : history) {
            System.out.printf("  | Date: %-12s | Tech: %-10s | Status: %-10s | Issue: %s%n",
                    t.getDate(), 
                    (t.getTechnician() != null ? t.getTechnician() : "Pending"), 
                    t.getTicketStatus(), 
                    t.getContent());
        }
    }
}