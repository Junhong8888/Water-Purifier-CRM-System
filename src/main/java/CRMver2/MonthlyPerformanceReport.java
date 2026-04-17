package CRMver2;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MonthlyPerformanceReport extends Report {

    public MonthlyPerformanceReport() {
        super("Monthly Performance & Analytics Report");
    }

    @Override
    public void generateReport(List<CRMver2.Ticket> tickets) {
        System.out.println("\n╔══════════════════════════════════════════════╗");
        System.out.println("║  " + reportTitle + "  ║");
        System.out.println("║  Date Generated: " + generationDate + "                  ║");
        System.out.println("╚══════════════════════════════════════════════╝");

        if (tickets == null || tickets.isEmpty()) {
            System.out.println("  [Info] No ticket data available for analysis.");
            return;
        }

        // Collection-based data analysis (Java Streams)
        long totalRequests = tickets.size();
        long resolvedTickets = tickets.stream()
                .filter(t -> t.getTicketStatus().equalsIgnoreCase("Completed"))
                .count();

        System.out.println("  ▶ Total Service Requests Submitted: " + totalRequests);
        System.out.println("  ▶ Total Tickets Resolved: " + resolvedTickets);
        
        // Calculate resolution rate
        double resolveRate = ((double) resolvedTickets / totalRequests) * 100;
        System.out.printf("  ▶ Overall Resolution Rate: %.1f%%\n", resolveRate);

        // Find the most frequently reported issue
        System.out.println("\n  --- Most Frequent Service Issues ---");
        Map<String, Long> issueFrequencies = tickets.stream()
                .filter(t -> t.getContent() != null && !t.getContent().equalsIgnoreCase("null"))
                .collect(Collectors.groupingBy(Ticket::getContent, Collectors.counting()));

        issueFrequencies.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue())) // Sort descending
                .limit(3) // Top 3 issues
                .forEach(entry -> System.out.println("  - " + entry.getKey() + " (" + entry.getValue() + " times)"));
    }
}