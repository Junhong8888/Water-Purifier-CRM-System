package CRMver2;

import java.io.IOException;
import java.util.Scanner;

public class TechinicianMenu implements DashBoardService<Staff> {
    @Override
    public void dashBoard(Staff user) throws IOException {
        TicketMenu ticketMenu = new TicketMenu();
        Scanner sc = new Scanner(System.in);
        String choice;

        do {
            System.out.println("\n╔══════════════════════════════════════════════╗");
            System.out.println("║           TECHNICIAN  DASHBOARD              ║");
            System.out.println("╠══════════════════════════════════════════════╣");
            System.out.println("║  1. View Central Dashboard (All Tickets)     ║");
            System.out.println("║  2. View My Booking Schedule                 ║");
            System.out.println("║  3. Edit Ticket (Add Notes & Update Status)  ║");
            System.out.println("║  4. Search & Filter Tickets                  ║");
            System.out.println("║  5. Assign Ticket to Technician              ║");
            System.out.println("║  6. Exit                                     ║");
            System.out.println("╚══════════════════════════════════════════════╝");
            System.out.print("  Select option (1-6): ");

            choice = sc.nextLine().trim();

            switch (choice) {
                case "1" -> ticketMenu.showActiveTickets();
                case "2" -> new TimeSlotBookingMenu().viewTimeSlotBooking(user);
                case "3" -> ticketMenu.showUpdateResponse();
                case "4" -> ticketMenu.showSearchAndFilter();
                case "5" -> ticketMenu.showAssignTechnician();
                case "6" -> System.out.println("  Logging out...");
                //case "5" -> ticketMenu.showMaintenanceHistory();
                //case "6" -> ticketMenu.showMonthlyReport();
                //case "7" -> System.out.println("  Logging out...");
                default -> System.out.println("  [ERROR] Invalid choice. Please enter 1-5.");
            }
        } while (!choice.equals("6"));
    }


}
