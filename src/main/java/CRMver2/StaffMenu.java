package CRMver2;

import java.io.IOException;
import java.util.Scanner;

public class StaffMenu implements DashBoardService<Staff> {
    private final TicketService ticketService;
    private final TicketMenu ticketMenu;

    public StaffMenu() {
        this.ticketMenu = new TicketMenu();
        this.ticketService = new TicketService();
    }

    /**
     * Primary Dashboard for Staff, Managers, and Technicians.
     * Consolidates all Module 3 and Module 4 features.
     */
    @Override
    public void dashBoard(Staff user) throws IOException {
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

    // ---------------------------------------------------------------
    // MENU & CRUD OPERATIONS
    // ---------------------------------------------------------------

    /*@Override
    public void displayMenu() throws IOException {
        Scanner sc = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\n╔════════════ STAFF MANAGEMENT ════════════╗");
            System.out.println("║ 1. Add Staff                             ║");
            System.out.println("║ 2. Remove Staff                          ║");
            System.out.println("║ 3. Update Staff                          ║");
            System.out.println("║ 4. View All Staff                        ║");
            System.out.println("║ 5. Exit                                  ║");
            System.out.println("╚══════════════════════════════════════════╝");
            System.out.print("  Choice: ");

            try {
                choice = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("  [ERROR] Please enter a valid number (1-5).");
                choice = -1;
                continue;
            }

            switch (choice) {
                case 1 -> addStaff();
                case 2 -> removeStaff();
                case 3 -> updateStaff();
                case 4 -> displayStaffInfo();
                case 5 -> System.out.println("  Returning to main menu...");
                default -> System.out.println("  [ERROR] Invalid option.");
            }
        } while (choice != 5);
    }

    public boolean addStaff() throws IOException {
        Scanner sc = new Scanner(System.in);
        String username, password, role = "";
        double salary = -1;

        System.out.println("\n  --- Add New Staff ---");
        System.out.print("  Enter Username: ");
        username = sc.nextLine().trim();
        System.out.print("  Enter Password: ");
        password = sc.nextLine().trim();

        System.out.println("  Select Role: 1. Technician | 2. Manager");
        System.out.print("  Choice: ");
        String rChoice = sc.nextLine().trim();
        if (rChoice.equals("1")) role = "Technician";
        else if (rChoice.equals("2")) role = "Manager";
        else {
            System.out.println("  [ERROR] Invalid role. Operation cancelled.");
            return false;
        }

        System.out.print("  Enter Salary: ");
        try {
            salary = Double.parseDouble(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("  [ERROR] Invalid salary amount.");
            return false;
        }

        if (username.isEmpty() || password.isEmpty() || salary < 0) {
            System.out.println("  [ERROR] Invalid data entered.");
            return false;
        }

        Staff newStaff = new Staff("temp", username, password, role, salary);
        newStaff.assignID();
        staffList.add(newStaff);
        writeFile(newStaff.toString());

        System.out.println("  [SUCCESS] Staff " + newStaff.getId() + " added.");
        return true;
    }

    public boolean removeStaff() throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.print("  Enter Staff ID to remove: ");
        String id = sc.nextLine().trim();

        boolean removed = staffList.removeIf(s -> s.getId().equalsIgnoreCase(id));
        if (removed) {
            rewriteStaffFile();
            System.out.println("  [SUCCESS] Staff removed.");
        } else {
            System.out.println("  [ERROR] Staff ID not found.");
        }
        return removed;
    }

    public boolean updateStaff() throws IOException {
        //ArrayList<Staff> staffList = StaffRepository.load
        Scanner sc = new Scanner(System.in);
        System.out.print("  Enter Staff ID to update: ");
        String id = sc.nextLine().trim();

        Staff target = staffList.stream()
                .filter(s -> s.getId().equalsIgnoreCase(id))
                .findFirst()
                .orElse(null);

        if (target == null) {
            System.out.println("  [ERROR] ID not found.");
            return false;
        }

        System.out.println("  1. Update Password | 2. Update Salary | 3. Cancel");
        System.out.print("  Choice: ");
        String choice = sc.nextLine().trim();

        if (choice.equals("1")) {
            System.out.print("  New Password: ");
            target.setPassword(sc.nextLine().trim());
        } else if (choice.equals("2")) {
            System.out.print("  New Salary: ");
            try {
                target.setSalary(Double.parseDouble(sc.nextLine().trim()));
            } catch (Exception e) { return false; }
        } else {
            return false;
        }

        rewriteStaffFile();
        System.out.println("  [SUCCESS] Information updated.");
        return true;
    }*/
}
