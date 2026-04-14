package CRM;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Staff extends User implements FileStorage, Menu {

    private String role;
    private double salary;

    // FIX 6: private — nothing outside Staff should touch the counter
    private static int counter = 1;

    static ArrayList<Staff> staffList;
    private static final TicketService ticketService = new TicketService();

    static {
        try {
            staffList = new StaffService().loadStaffToList();
            // FIX 6: initialise counter from the highest existing ID in file
            initCounter();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load staff list on startup.", e);
        }
    }

    // ---------------------------------------------------------------
    // CONSTRUCTORS
    // ---------------------------------------------------------------

    public Staff() throws IOException {}

    public Staff(String id, String username, String password,
                 String role, double salary) throws IOException {
        super(id, username, password);
        this.role = role;
        this.salary = salary;
    }

    // ---------------------------------------------------------------
    // COUNTER INIT
    // FIX 6: read the highest numeric suffix from existing IDs
    // so new IDs never collide with ones already in the file
    // ---------------------------------------------------------------
    private static void initCounter() {
        int max = 0;
        for (Staff s : staffList) {
            String id = s.getId();
            if (id != null && id.length() > 1) {
                try {
                    int num = Integer.parseInt(id.substring(1)); // strip 't' or 'm'
                    if (num > max) max = num;
                } catch (NumberFormatException ignored) {}
            }
        }
        counter = max + 1;
    }

    // ---------------------------------------------------------------
    // GETTERS & SETTERS
    // ---------------------------------------------------------------

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public double getSalary() { return salary; }
    public void setSalary(double salary) { this.salary = salary; }

    // ---------------------------------------------------------------
    // ID ASSIGNMENT
    // FIX 10: role string is "Technician" not "CRM.Technician"
    // ---------------------------------------------------------------
    public void assignID() {
        if (getRole() == null) {
            setId("s" + counter);
        } else if (getRole().equalsIgnoreCase("Technician")) {
            setId("t" + counter);
        } else if (getRole().equalsIgnoreCase("Manager")) {
            setId("m" + counter);
        } else {
            setId("s" + counter);
        }
        counter++;
    }

    // ---------------------------------------------------------------
    // CREDENTIALS
    // ---------------------------------------------------------------
    public boolean credentials(String id, String username, String password) {
        for (Staff staff : staffList) {
            if (staff.getId().equals(id)
                    && staff.getUsername().equals(username)
                    && staff.getPassword().equals(password)) {
                if (verifyRole(staff.getRole())) {
                    return true;
                } else {
                    System.out.println("  Access denied: insufficient role.");
                    return false;
                }
            }
        }
        System.out.println("  Invalid ID, username or password.");
        return false;
    }

    // FIX 10: no package prefix in role string
    public boolean verifyRole(String role) {
        return role != null
                && (role.equalsIgnoreCase("Technician")
                || role.equalsIgnoreCase("Manager"));
    }

    // ---------------------------------------------------------------
    // DISPLAY STAFF INFO
    // FIX 7: pass username to countAssignedTickets, not id
    // ---------------------------------------------------------------
    public void displayStaffInfo() throws IOException {
        System.out.println("\n  |====================================================================|");
        System.out.printf("  |%-12s %-14s %-12s %-12s %-15s|%n",
                "Staff ID", "Username", "Role", "Salary", "Tickets Assigned");
        System.out.println("  |======================================================================|");

        for (Staff staff : staffList) {
            int assigned = 0;
            try {
                // FIX 7: use username — tickets store technician by username
                assigned = countAssignedTickets(staff.getUsername());
            } catch (IOException e) {
                System.out.println("  Warning: could not count tickets for " + staff.getUsername());
            }
            System.out.printf("  |%-12s %-14s %-12s %-12.2f %-15d|%n",
                    staff.getId(),
                    staff.getUsername(),
                    staff.getRole(),
                    staff.getSalary(),
                    assigned);
        }
        System.out.println("  |===================================================================|\n");
    }

    // FIX 7: parameter is now clearly "technicianUsername" to match ticket data
    public int countAssignedTickets(String technicianUsername) throws IOException {
        ArrayList<Ticket> ticketList = ticketService.loadTicketToList();
        long count = ticketList.stream()
                .filter(t -> t.getTicketStatus() != null
                        && t.getTicketStatus().equalsIgnoreCase("Completed"))
                .filter(t -> t.getTechnician() != null
                        && t.getTechnician().equalsIgnoreCase(technicianUsername))
                .count();
        return (int) count;
    }

    // ---------------------------------------------------------------
    // STAFF MENU
    // FIX 8: try-catch around nextInt to handle bad input gracefully
    // ---------------------------------------------------------------
    @Override
    public void displayMenu() throws IOException {
        Scanner sc = new Scanner(System.in);
        int choice = -1;

        do {
            System.out.println("\n╔══════════════════════════════╗");
            System.out.println("║        STAFF MENU            ║");
            System.out.println("╠══════════════════════════════╣");
            System.out.println("║  1. Add Staff                ║");
            System.out.println("║  2. Remove Staff             ║");
            System.out.println("║  3. Update Staff             ║");
            System.out.println("║  4. View All Staff           ║");
            System.out.println("║  5. Exit                     ║");
            System.out.println("╚══════════════════════════════╝");
            System.out.print("  Select option: ");

            // FIX 8: catch non-integer input
            try {
                choice = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("  Invalid input — please enter a number.");
                choice = -1;
                continue;
            }

            switch (choice) {
                case 1 -> {
                    if (addStaff()) System.out.println("  ✓ Staff added successfully.");
                }
                case 2 -> {
                    if (removeStaff()) {
                        System.out.println("  ✓ Staff removed successfully.");
                    } else {
                        System.out.println("  Staff ID not found.");
                    }
                }
                case 3 -> {
                    if (updateStaff()) {
                        System.out.println("  ✓ Staff updated successfully.");
                    } else {
                        System.out.println("  Staff ID not found.");
                    }
                }
                case 4 -> displayStaffInfo();
                case 5 -> System.out.println("  Exiting Staff Menu...");
                default -> System.out.println("  Invalid choice — please select 1 to 5.");
            }

        } while (choice != 5);
    }

    // ---------------------------------------------------------------
    // ADD STAFF
    // FIX 1: corrected do-while condition (loop while inputs invalid)
    // FIX 2: salary declared outside loop so condition can use it
    // FIX 3: writeFile() called to persist new staff to file
    // ---------------------------------------------------------------
    public boolean addStaff() throws IOException {
        Scanner sc = new Scanner(System.in);
        String username;
        String password;
        String role = null;
        double salary = -1;
        int choice;

        do {
            System.out.print("  Enter Username: ");
            username = sc.nextLine().trim();

            System.out.print("  Enter Password: ");
            password = sc.nextLine().trim();

            System.out.println("  Select Role:");
            System.out.println("    1. Technician");
            System.out.println("    2. Manager");
            System.out.print("  Choice: ");

            try {
                choice = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("  Invalid choice — please enter 1 or 2.");
                continue;
            }

            // FIX 10: no package prefix
            switch (choice) {
                case 1 -> role = "Technician";
                case 2 -> role = "Manager";
                default -> {
                    System.out.println("  Invalid role choice.");
                    role = null;
                }
            }

            System.out.print("  Enter Salary: ");
            try {
                salary = Double.parseDouble(sc.nextLine().trim());
                if (salary < 0) System.out.println("  Salary cannot be negative.");
            } catch (NumberFormatException e) {
                System.out.println("  Invalid salary — please enter a number.");
                salary = -1;
            }

            // FIX 1: loop WHILE inputs are incomplete/invalid
        } while (username.isEmpty() || password.isEmpty() || role == null || salary < 0);

        Staff staff = new Staff();
        staff.setUsername(username);
        staff.setPassword(password);
        staff.setRole(role);
        staff.setSalary(salary);
        staff.assignID();

        staffList.add(staff);

        // FIX 3: persist to file
        writeFile(staff.toString());
        return true;
    }

    // ---------------------------------------------------------------
    // REMOVE STAFF
    // FIX 4: rewrites the entire file after removal
    // ---------------------------------------------------------------
    public boolean removeStaff() throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.print("  Enter Staff ID to remove: ");
        String staffID = sc.nextLine().trim();

        boolean removed = staffList.removeIf(s -> s.getId().equals(staffID));

        if (removed) {
            // FIX 4: rewrite file with updated list
            rewriteStaffFile();
        }
        return removed;
    }

    // ---------------------------------------------------------------
    // UPDATE STAFF
    // FIX 5: rewrites the entire file after update
    // FIX 8: try-catch on menu input
    // ---------------------------------------------------------------
    public boolean updateStaff() throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.print("  Enter Staff ID to update: ");
        String staffID = sc.nextLine().trim();

        Staff target = staffList.stream()
                .filter(s -> s.getId().equals(staffID))
                .findFirst()
                .orElse(null);

        if (target == null) return false;

        int choice = -1;
        do {
            System.out.println("\n  What would you like to update?");
            System.out.println("  1. Username");
            System.out.println("  2. Password");
            System.out.println("  3. Role");
            System.out.println("  4. Salary");
            System.out.println("  5. Done");
            System.out.print("  Choice: ");

            try {
                choice = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("  Invalid input.");
                continue;
            }

            switch (choice) {
                case 1 -> {
                    System.out.print("  New Username: ");
                    target.setUsername(sc.nextLine().trim());
                    System.out.println("  ✓ Username updated to: " + target.getUsername());
                }
                case 2 -> {
                    System.out.print("  New Password: ");
                    target.setPassword(sc.nextLine().trim());
                    System.out.println("  ✓ Password updated.");
                }
                case 3 -> {
                    System.out.print("  New Role (Technician / Manager): ");
                    String newRole = sc.nextLine().trim();
                    if (newRole.equalsIgnoreCase("Technician")
                            || newRole.equalsIgnoreCase("Manager")) {
                        target.setRole(newRole);
                        System.out.println("  ✓ Role updated to: " + target.getRole());
                    } else {
                        System.out.println("  Invalid role — must be Technician or Manager.");
                    }
                }
                case 4 -> {
                    System.out.print("  New Salary: ");
                    try {
                        double newSalary = Double.parseDouble(sc.nextLine().trim());
                        if (newSalary < 0) {
                            System.out.println("  Salary cannot be negative.");
                        } else {
                            target.setSalary(newSalary);
                            System.out.printf("  ✓ Salary updated to: %.2f%n", target.getSalary());
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("  Invalid salary value.");
                    }
                }
                case 5 -> System.out.println("  Saving changes...");
                default -> System.out.println("  Invalid choice.");
            }

        } while (choice != 5);

        // FIX 5: persist all changes to file
        rewriteStaffFile();
        return true;
    }

    // ---------------------------------------------------------------
    // REWRITE ENTIRE STAFF FILE
    // Single place to persist the full staffList — used by
    // removeStaff() and updateStaff() so there's no duplication
    // ---------------------------------------------------------------
    private void rewriteStaffFile() throws IOException {
        File file = new File("C:\\crmSystem\\staff.txt");
        file.getParentFile().mkdirs();

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, false))) {
            for (Staff s : staffList) {
                bw.write(s.toString());
                bw.newLine();
            }
        }
    }

    // ---------------------------------------------------------------
    // WRITE FILE (append single entry)
    // FIX 9: try-with-resources — no resource leak on exception
    // ---------------------------------------------------------------
    @Override
    public void writeFile(String data) throws IOException {
        File file = new File("C:\\crmSystem\\staff.txt");
        file.getParentFile().mkdirs();
        if (!file.exists()) file.createNewFile();

        // FIX 9: try-with-resources guarantees writer is always closed
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
            bw.write(data);
            bw.newLine();
        }
    }

    // ---------------------------------------------------------------
    // toString
    // ---------------------------------------------------------------
    @Override
    public String toString() {
        return getId() + "," + getUsername() + "," + getPassword()
                + "," + getRole() + "," + getSalary();
    }
}