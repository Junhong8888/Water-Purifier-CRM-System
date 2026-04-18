package CRMver2;

import java.io.IOException;
import java.util.*;

public class ManagerMenu implements DashBoardService<Staff> {

    @Override
    public void dashBoard(Staff user) throws IOException {
        TicketMenu ticketMenu = new TicketMenu();
        Scanner sc = new Scanner(System.in);
        String choice;

        do {
            System.out.println("\n╔══════════════════════════════════════════════╗");
            System.out.println("║              MANAGER DASHBOARD               ║");
            System.out.println("╠══════════════════════════════════════════════╣");
            System.out.println("║  1. View Central Dashboard (All Tickets)     ║");
            System.out.println("║  2. Staff Operation Menu                     ║");
            System.out.println("║  3. Search & Filter Tickets                  ║");
            System.out.println("║  4. Assign Ticket to Technician              ║");
            System.out.println("║  5. View Maintenance History                 ║");
            System.out.println("║  6. View Monthly Reporting (Analytics)       ║");
            System.out.println("║  7. View Technician Rating                   ║");
            System.out.println("║  8. Logout                                   ║");
            System.out.println("╚══════════════════════════════════════════════╝");
            System.out.print("  Select option (1-7): ");

            choice = sc.nextLine().trim();

            switch (choice) {
                case "1" -> ticketMenu.showActiveTickets();
                case "2" -> ticketMenu.showUpdateResponse();
                case "3" -> ticketMenu.showSearchAndFilter();
                case "4" -> ticketMenu.showAssignTechnician();
                case "5" ->  {
                    Customer targetCustomer;
                    System.out.print("Enter Customer ID: ");
                    String customerID = sc.nextLine();
                    ArrayList<Customer> customers = new CustomerRepository().loadCustomersToList();
                    for (Customer customer : customers) {
                        if(customer.getId().equals(customerID)) {
                            targetCustomer = customer;
                            new MaintenanceHistoryReport(targetCustomer).generateReport(new TicketService().loadTicketToList());
                            break;
                        }
                    }
                    System.out.println("Customer ID not found");
                } //ticketMenu.showMaintenanceHistory();
                case "6" -> new MonthlyPerformanceReport();//ticketMenu.showMonthlyReport();
                case "7" -> viewTechnicianRating();
                case "8" -> System.out.println("  Logging out...");
                default -> System.out.println("  [ERROR] Invalid choice. Please enter 1-7.");
            }
        } while (!choice.equals("7"));
    }

    public void viewTechnicianRating() throws IOException {
        ArrayList<Ticket> allTickets = new TicketRepository().loadAll(); //
        Map<String, List<Integer>> techRatings = new HashMap<>();

        for (Ticket t : allTickets) {
            String resp = t.getResponse(); //
            if (resp != null && resp.contains("Stars")) {
                try {
                    // Extracts the first number found (the 1-5 rating)
                    int stars = Integer.parseInt(resp.substring(0, 1));

                    techRatings.putIfAbsent(t.getTechnician(), new ArrayList<>());
                    techRatings.get(t.getTechnician()).add(stars);
                } catch (Exception ignored) {}
            }
        }

        // 2. Display the Results
        System.out.println("\n===================================================");
        System.out.println("       TECHNICIAN PERFORMANCE REPORT          ");
        System.out.println("===================================================");
        System.out.printf("%-20s | %-15s | %-10s%n", "Technician", "Total Ratings", "Avg Stars");
        System.out.println("---------------------------------------------------");

        if (techRatings.isEmpty()) {
            System.out.println("No ratings found in the system.");
        } else {
            for (String tech : techRatings.keySet()) {
                List<Integer> ratings = techRatings.get(tech);
                double sum = 0;
                for (int r : ratings) sum += r;
                double average = sum / ratings.size();

                System.out.printf("%-20s | %-15d | %.2f Stars%n",
                        tech, ratings.size(), average);
            }
        }
        System.out.println("===================================================\n");
    }
}
