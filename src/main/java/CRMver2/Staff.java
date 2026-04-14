package CRMver2;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Staff extends User implements FileStorage, Menu {

    private String role;
    private double salary;

    // Static counter to generate new IDs - private so only Staff handles its own IDs
    private static int counter = 1;

    static ArrayList<Staff> staffList;
    private static final TicketService ticketService = new TicketService();

    // Static block runs once when the class is loaded
    static {
        try {
            staffList = new StaffService().loadStaffToList();
            // Initialize counter based on current file data to prevent duplicates
            initCounter();
        } catch (IOException e) {
            throw new RuntimeException("CRITICAL: Failed to load staff list on startup.", e);
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
    // ID MANAGEMENT
    // ---------------------------------------------------------------

    private static void initCounter() {
        int max = 0;
        for (Staff s : staffList) {
            String id = s.getId();
            if (id != null && id.length() > 1) {
                try {
                    // Extract numeric part (e.g., "m5" -> 5)
                    int num = Integer.parseInt(id.substring(1));
                    if (num > max) max = num;
                } catch (NumberFormatException ignored) {}
            }
        }
        counter = max + 1;
    }

    public void assignID() {
        String prefix = "s"; // Default prefix
        if (role != null) {
            if (role.equalsIgnoreCase("Technician")) prefix = "t";
            else if (role.equalsIgnoreCase("Manager")) prefix = "m";
        }
        setId(prefix + counter);
        counter++;
    }

    // ---------------------------------------------------------------
    // GETTERS & SETTERS
    // ---------------------------------------------------------------

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public double getSalary() { return salary; }
    public void setSalary(double salary) { this.salary = salary; }

    // ---------------------------------------------------------------
    // AUTHENTICATION & VERIFICATION
    // ---------------------------------------------------------------

    public boolean credentials(String id, String username, String password) {
        for (Staff staff : staffList) {
            if (staff.getId().equals(id)
                    && staff.getUsername().equals(username)
                    && staff.getPassword().equals(password)) {
                return true;
            }
        }
        System.out.println("  [ERROR] Invalid credentials.");
        return false;
    }

    // ---------------------------------------------------------------
    // OPERATIONAL LOGIC
    // ---------------------------------------------------------------

    public void displayStaffInfo() throws IOException {
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

    // ---------------------------------------------------------------
    // MENU & CRUD OPERATIONS
    // ---------------------------------------------------------------

    @Override
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
    }

    // ---------------------------------------------------------------
    // PERSISTENCE (FILE I/O)
    // ---------------------------------------------------------------

    @Override
    public void writeFile(String data) throws IOException {
        File file = new File("C:\\crmSystem\\staff.txt");
        ensureDir(file);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
            bw.write(data);
            bw.newLine();
        }
    }

    private void rewriteStaffFile() throws IOException {
        File file = new File("C:\\crmSystem\\staff.txt");
        ensureDir(file);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, false))) {
            for (Staff s : staffList) {
                bw.write(s.toString());
                bw.newLine();
            }
        }
    }

    private void ensureDir(File file) {
        if (file.getParentFile() != null && !file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
    }

    @Override
    public String toString() {
        return getId() + "," + getUsername() + "," + getPassword() + "," + role + "," + salary;
    }
}