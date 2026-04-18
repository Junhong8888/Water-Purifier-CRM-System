package CRMver2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class ManagerMenu implements DashBoardService {

    @Override
    public void dashBoard() throws IOException {
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
            System.out.println("║  7. Logout                                   ║");
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
                    ArrayList<Customer> customers = new CustomerService().loadCustomersToList();
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
                case "7" -> System.out.println("  Logging out...");
                default -> System.out.println("  [ERROR] Invalid choice. Please enter 1-7.");
            }
        } while (!choice.equals("7"));
    }
}
