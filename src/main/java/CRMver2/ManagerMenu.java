package CRMver2;

import java.io.IOException;
import java.util.Scanner;

public class ManagerMenu implements DashBoardService<Manager> {

    @Override
    public void dashBoard(Manager user) throws IOException {
        TicketMenu ticketMenu = new TicketMenu();
        Scanner sc = new Scanner(System.in);
        String choice;

        do {
            System.out.println("\n╔══════════════════════════════════════════════╗");
            System.out.println("║         STAFF / MANAGER DASHBOARD            ║");
            System.out.println("╠══════════════════════════════════════════════╣");
            System.out.println("║  1. View Central Dashboard (All Tickets)     ║");
            System.out.println("║  2. Edit Ticket (Add Notes & Update Status)  ║");
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
                case "5" -> ticketMenu.showMaintenanceHistory();
                case "6" -> ticketMenu.showMonthlyReport();
                case "7" -> System.out.println("  Logging out...");
                default -> System.out.println("  [ERROR] Invalid choice. Please enter 1-7.");
            }
        } while (!choice.equals("7"));
    }
}
