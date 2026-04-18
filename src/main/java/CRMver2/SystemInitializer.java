package CRMver2;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class SystemInitializer {

    public static void runLegacyStaffTests() throws IOException {
    // Keep file creation logic
    String dir = System.getProperty("user.dir") + File.separator;
    if (!new File(dir + "staff.txt").exists()) new File(dir + "staff.txt").createNewFile();
    if (!new File(dir + "customer.txt").exists()) new File(dir + "customer.txt").createNewFile();
    
    // Seed initial manager if file is empty
    //Staff staff = new Staff();
    // Logic to only write if file is empty would go here
    
    System.out.println("System Initialized. Welcome to Apple Vacation CRM.");
    // Removed: staff.displayStaffInfo(); 
    }

    /**
     * Handles file creation, data seeding, and critical counter/list synchronization.
     */
    public static void initializeSystemResources() throws IOException {
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