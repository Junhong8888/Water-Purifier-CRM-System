package CRMver2;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class StaffService implements RegistrationService<Staff>{
    private final StaffRepository staffRepo;
    private final TicketService ticketService;

    // Shared instance to avoid repeated file loading overhead
    //private final TicketService ticketService = new TicketService();

    public StaffService() {
        this.staffRepo = new StaffRepository();
        this.ticketService = new TicketService();
    }

    // ---------------------------------------------------------------
    // OPERATIONAL LOGIC
    // ---------------------------------------------------------------

    public void displayStaffInfo() throws IOException {
        ArrayList<Staff> staffList = staffRepo.loadStaffToList();
        System.out.println("\n  |========================================================================|");
        System.out.printf("  | %-10s | %-15s | %-12s | %-10s | %-12s |%n",
                "ID", "Username", "Role", "Salary", "Jobs Done");
        System.out.println("  |========================================================================|");

        for (Staff s : staffList) {
            int jobsDone = countAssignedTickets(s.getUsername());
            System.out.printf("  | %-10s | %-15s | %-12s | %-10.2f | %-12d |%n",
                    s.getId(), s.getUsername(), s.getRole(), s.getSalary(), jobsDone);
        }
        System.out.println("  |========================================================================|\n");
    }

    public int countAssignedTickets(String technicianUsername) throws IOException {
        ArrayList<Ticket> ticketList = ticketService.loadTicketToList();
        return (int) ticketList.stream()
                .filter(t -> t.getTechnician() != null && t.getTechnician().equalsIgnoreCase(technicianUsername))
                .filter(t -> t.getTicketStatus() != null && t.getTicketStatus().equalsIgnoreCase("Completed"))
                .count();
    }


    @Override
    public void register(Staff staff) throws IOException {
            String role = staff.getRole();
            Scanner sc = new Scanner(System.in);
            StaffRepository staffRepository = new StaffRepository();
            ArrayList<Staff> currentStaff = staffRepository.loadStaffToList();

            System.out.println("\n---" + role  + " Registration ---");

            String username;
            while (true) {
                System.out.print("Enter Username: ");
                username = sc.nextLine().trim();

                if (username.isEmpty() || username.contains(",")) {
                    System.out.println("[ERROR] Username cannot be empty or contain commas.");
                    continue;
                }

                boolean isTaken = false;
                for (Staff s : currentStaff) {
                    if (s.getUsername().equalsIgnoreCase(username) && s.getRole().equalsIgnoreCase(role)) {
                        isTaken = true;
                        break;
                    }
                }
                if (isTaken) System.out.println("[ERROR] A " + role + " with this username already exists.");
                else break;
            }

            String password;
            while (true) {
                System.out.print("Enter Password (min 6 chars): ");
                password = sc.nextLine().trim();
                if (password.length() >= 6 && !password.contains(",")) break;
                System.out.println("[ERROR] Password must be at least 6 characters and contain no commas.");
            }

            double salary = 0;
            while (true) {
                try {
                    System.out.print("Enter Monthly Salary: ");
                    salary = Double.parseDouble(sc.nextLine().trim());
                    if (salary > 0) break;
                    else System.out.println("[ERROR] Salary must be greater than 0.");
                } catch (Exception e) {
                    System.out.println("[ERROR] Please enter a valid number for salary.");
                }
            }

            staff.setUsername(username);
            staff.setPassword(password);
            staff.setSalary(salary);
            staff.setRole(role);
            staff.assignID();

            new StaffRepository().writeFile(staff.toString());

            System.out.println("\n[SUCCESS] "+ role +" registered successfully! You may now login.");

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
    }

}