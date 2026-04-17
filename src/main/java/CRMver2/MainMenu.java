package CRMver2;

import CRMver2.Staff;
import CRMver2.Ticket;
import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class MainMenu {

    /**
     * Entry point for the CRM application.
     */
    public static void startCRMSystem() throws IOException {
        // 1. Setup files, seed data, and sync static lists/counters
        initializeSystemResources();

        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n          .:'");
            System.out.println("      __ :'__");
            System.out.println("   .'`__`-'__``.");
            System.out.println("  :__________.-'");
            System.out.println("  :_________:");
            System.out.println("   :_________`-;");
            System.out.println("    `.__.-.__.'");
            System.out.println("============================================");
            System.out.println("   Water Purifier CRM System (Module 1)   ");
            System.out.println("============================================");
            System.out.println("1. Register New Account");
            System.out.println("2. Login as Existing User");
            System.out.println("3. Exit");
            System.out.print("Select an option: ");

            String choice = sc.nextLine().trim();

            if (choice.equals("1")) {
                System.out.println("\nRegister as:");
                System.out.println("1. Customer\n2. Staff\n3. Manager\n4. Technician");
                System.out.print("Choice: ");
                String regChoice = sc.nextLine().trim();
                handleRegistration(regChoice);

            } else if (choice.equals("2")) {
                System.out.println("\nLogin to section:");
                System.out.println("1. Customer\n2. Staff\n3. Manager\n4. Technician");
                System.out.print("Choice: ");
                String loginChoice = sc.nextLine().trim();
                handleLogin(loginChoice);

            } else if (choice.equals("3")) {
                System.out.println("Exiting system...");
                break;
            } else {
                System.out.println("[ERROR] Invalid option. Please enter 1, 2, or 3.");
            }
        }
    }

    private static void handleRegistration(String roleChoice) throws IOException {
        StaffService ss = new StaffService();

        switch (roleChoice) {
            case "1" -> new CustomerService().register(new Customer());
            //case "2" -> new StaffService().;
            case "3" -> new ManagerService().register(new Manager());
            case "4" -> new TechnicianService().register(new  Technician());
            default -> System.out.println("[ERROR] Invalid Role Choice.");
        }
    }

    private static void handleLogin(String roleChoice) throws IOException {
        String role = "";
        switch (roleChoice) {
            case "1" -> role = "Customer";
            case "2" -> role = "Staff";
            case "3" -> role = "Manager";
            case "4" -> role = "Technician";
            default -> {
                System.out.println("[ERROR] Invalid Role Selection.");
                return;
            }
        }
        // Redirects to the appropriate dashboard upon success
        new AuthService().loginSystem(role);
    }

    /**
     * Handles file creation, data seeding, and critical counter/list synchronization.
     */
    private static void initializeSystemResources() throws IOException {
        File staffFile =  new File(System.getProperty("user.dir") + File.separator + "staff.txt");
        File ticketFile = new File(System.getProperty("user.dir") + File.separator + "ticket.txt");

        // --- STAFF SEEDING ---
        if (staffFile.createNewFile() || staffFile.length() == 0) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(staffFile))) {
                // Format: id,username,password,role,salary
                bw.write("m1,Junhong,junhong,Manager,2500.00");
                bw.newLine();
                bw.write("t1,Adam,adam123,Technician,1800.00");
                bw.newLine();
                bw.write("t2,Sarah,sarah123,Technician,1800.00");
                bw.newLine();
            }
            System.out.println("[SYSTEM] Staff data seeded.");
        }

        // --- TICKET SEEDING ---
        if (ticketFile.createNewFile() || ticketFile.length() == 0) {
            List<String> ticketData = Arrays.asList(
                    "t1,C1,in progress,High,Adam,08-04-2025,null,Repair Water Purifier,null,null",
                    "t2,C2,in progress,High,Adam,14-04-2025,null,Water Purifier Maintenance,null,null",
                    "t3,C3,Completed,Low,Adam,14-04-2025,16-04-2025,Billing Enquiry,null,Resolved"
            );
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(ticketFile))) {
                for (String line : ticketData) {
                    bw.write(line);
                    bw.newLine();
                }
            }
            System.out.println("[SYSTEM] Ticket data seeded.");
        }

        // --- DATA SYNCHRONIZATION (CRITICAL) ---

        // 1. Sync the Staff List so AuthService has access to the seeded accounts
        Staff.staffList = new StaffRepository().loadStaffToList();

        // 2. Initialize Ticket ID counter based on existing file content
        // This ensures the next ticket is 't4' instead of 't1'
        Ticket.initCount();

        System.out.println("[SYSTEM] Internal lists and counters synchronized.");
    }
}