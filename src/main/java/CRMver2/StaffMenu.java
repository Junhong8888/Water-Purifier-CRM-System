package CRMver2;

import java.io.IOException;
import java.util.Scanner;

public class StaffMenu {
    private StaffService staffService;
    private StaffRepository staffRepo;

    public StaffMenu() throws IOException {
        StaffService staffService = new StaffService();
        StaffRepository staffRepo = new StaffRepository();
    }


    // ---------------------------------------------------------------
    // MENU & CRUD OPERATIONS
    // ---------------------------------------------------------------
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
                case 1 -> staffService.addStaff();
                case 2 -> staffService.removeStaff();
                case 3 -> staffService.updateStaff();
                case 4 -> System.out.println(); //displayStaffInfo();
                case 5 -> System.out.println("  Returning to main menu...");
                default -> System.out.println("  [ERROR] Invalid option.");
            }
        } while (choice != 5);
    }

    /*public boolean addStaff() throws IOException {
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

        Staff newStaff;
        if(role.equalsIgnoreCase("Technician")){
            newStaff = new Technician();
        } else {
            newStaff = new Manager();
        }
        newStaff.setUsername(username);
        newStaff.setPassword(password);
        newStaff.setSalary(salary);
        newStaff.setRole(role);
        newStaff.assignID();

        staffRepo.loadStaffToList().add(newStaff);
        staffRepo.writeFile(newStaff.toString());

        System.out.println("  [SUCCESS] Staff " + newStaff.getId() + " added.");
        return true;
    }

    public boolean removeStaff() throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.print("  Enter Staff ID to remove: ");
        String id = sc.nextLine().trim();

        boolean removed = staffRepo.loadStaffToList().removeIf(s -> s.getId().equalsIgnoreCase(id));
        if (removed) {
            staffRepo.rewriteStaffFile(staffRepo.loadStaffToList());
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

        Staff target = staffRepo.loadStaffToList().stream()
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

        staffRepo.rewriteStaffFile(staffRepo.loadStaffToList());
        System.out.println("  [SUCCESS] Information updated.");
        return true;
    }*/
}
