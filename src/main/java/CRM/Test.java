package CRM;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Test {

    public static void main(String[] args) throws IOException {

        // ---------------------------------------------------------------
        // STEP 1: Create directories and files if they don't exist
        // FIX 7: only write seed data when files are freshly created
        //         so re-runs don't keep appending duplicate records
        // ---------------------------------------------------------------
        File dir = new File("C:\\crmSystem");
        dir.mkdirs();

        File staffFile  = new File("C:\\crmSystem\\staff.txt");
        File ticketFile = new File("C:\\crmSystem\\ticket.txt");

        boolean staffFileCreated  = staffFile.createNewFile();  // false if already exists
        boolean ticketFileCreated = ticketFile.createNewFile();

        // ---------------------------------------------------------------
        // STEP 2: Seed staff data — only on first run
        // FIX 1: reload staffList AFTER writing so the static list is fresh
        // FIX 7: guard with staffFileCreated flag
        // ---------------------------------------------------------------
        if (staffFileCreated) {
            System.out.println("  Creating staff seed data...");
            Staff seedWriter = new Staff();
            // Format: id,username,password,role,salary
            seedWriter.writeFile("m001,Junhong,junhong,Manager,2500.00");
            seedWriter.writeFile("t001,Adam,adam123,Technician,1800.00");
            seedWriter.writeFile("t002,Sarah,sarah123,Technician,1800.00");
            System.out.println("  ✓ Staff seed data written.");
        } else {
            System.out.println("  staff.txt already exists — skipping seed.");
        }

        // FIX 1: reload staffList from file now that seed data is present
        Staff.staffList = new StaffService().loadStaffToList();
        System.out.println("  ✓ Staff list loaded: " + Staff.staffList.size() + " staff member(s).");

        // ---------------------------------------------------------------
        // STEP 3: Seed ticket data — only on first run
        // FIX 2: id is now the FIRST field (after Ticket.java fix)
        // FIX 7: guard with ticketFileCreated flag
        // ---------------------------------------------------------------
        if (ticketFileCreated) {
            System.out.println("  Creating ticket seed data...");
            // Format: id,customerID,ticketStatus,priorityLevel,technician,
            //         date,resolveTime,description,content,response
            List<String> ticketData = Arrays.asList(
                    "t1,customerID,in progress,High,Adam,08-04-2025,null,Repair Water Purifier,null,null",
                    "t2,customerID2,in progress,High,Adam,14-04-2025,null,Water Purifier Maintenance,null,null",
                    "t3,junhong,in progress,Low,Adam,14-04-2025,null,Billing Enquiry,null,null",
                    "t4,junhong,Completed,Low,Adam,14-04-2025,16-04-2025,Billing Enquiry,null,Resolved",
                    "t5,customerID2,Completed,High,m001,15-04-2025,16-04-2025,Installation,null,Done",
                    "t6,customerID3,in progress,High,Sarah,15-04-2025,null,Filter Replacement,null,null"
            );

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(ticketFile))) {
                for (String line : ticketData) {
                    bw.write(line);
                    bw.newLine();
                }
            }
            System.out.println("  ✓ Ticket seed data written.");
        } else {
            System.out.println("  ticket.txt already exists — skipping seed.");
        }

        // ---------------------------------------------------------------
        // STEP 4: Initialise Ticket ID counter from file
        // FIX 3 & 8: must be called AFTER seed data is written and
        //             BEFORE any new Ticket objects are created
        // ---------------------------------------------------------------
        Ticket.initCount();
        System.out.println("  ✓ Ticket ID counter initialised.");

        // ---------------------------------------------------------------
        // STEP 5: Test Ticket creation and booking
        // FIX 4: BookingTimeSlot.txt uses user.dir — no extra setup needed
        //         (TimeSlotBooking.loadTimeSlotBookingToList handles missing file)
        // ---------------------------------------------------------------
        System.out.println("\n--- Testing Ticket Creation & Booking ---");
        Ticket ticket = new Ticket(
                "customerID", "in progress", "High",
                "Adam", "08-04-2025", null,
                "Repair Water Purifier", null, null
        );
        ticket.makeBooking();

        if (ticket.getTimeSlotBooking() != null) {
            System.out.println("  Booking details: " + ticket.getTimeSlotBooking());
        } else {
            System.out.println("  No booking created (description did not match booking trigger).");
        }

        // ---------------------------------------------------------------
        // STEP 6: Test Staff operations
        // FIX 6: use the loaded Manager object instead of blank new Staff()
        // ---------------------------------------------------------------
        System.out.println("\n--- Testing Staff Display ---");
        // Get the seeded manager from the loaded list
        Staff manager = Staff.staffList.stream()
                .filter(s -> s.getId().equals("m001"))
                .findFirst()
                .orElse(null);

        if (manager == null) {
            System.out.println("  Manager not found in staffList — check seed data.");
            return;
        }

        manager.displayStaffInfo();

        System.out.println("\n--- Testing Add Staff ---");
        manager.addStaff();
        manager.displayStaffInfo();

        System.out.println("\n--- Testing Update Staff ---");
        manager.updateStaff();
        manager.displayStaffInfo();

        // ---------------------------------------------------------------
        // STEP 7: Staff login loop
        // FIX 5: don't set fields on the blank object — just pass inputs
        //         directly to credentials() which searches staffList
        // ---------------------------------------------------------------
        System.out.println("\n--- Staff Login ---");
        Scanner sc = new Scanner(System.in);
        String staffID, username, password;
        boolean authenticated = false;

        do {
            System.out.print("  Staff ID : ");
            staffID = sc.nextLine().trim();

            System.out.print("  Username : ");
            username = sc.nextLine().trim();

            System.out.print("  Password : ");
            password = sc.nextLine().trim();

            // FIX 5: credentials() searches staffList — no need to set fields
            authenticated = manager.credentials(staffID, username, password);

            if (!authenticated) {
                System.out.println("  Login failed. Please try again.\n");
            }

        } while (!authenticated);

        System.out.println("  ✓ Login successful!\n");

        // ---------------------------------------------------------------
        // STEP 8: Route to correct menu based on authenticated staff role
        // ---------------------------------------------------------------
        String finalStaffID = staffID;
        Staff loggedIn = Staff.staffList.stream()
                .filter(s -> s.getId().equals(finalStaffID))
                .findFirst()
                .orElse(null);

        if (loggedIn == null) {
            System.out.println("  Error: authenticated staff not found in list.");
            return;
        }

        System.out.println("  Welcome, " + loggedIn.getUsername()
                + " [" + loggedIn.getRole() + "]");

        if (loggedIn.getRole().equalsIgnoreCase("Manager")) {
            // Manager sees staff operations AND ticket operations
            int choice = -1;
            do {
                System.out.println("\n╔════════════════════════════════════╗");
                System.out.println("║          MANAGER MENU              ║");
                System.out.println("╠════════════════════════════════════╣");
                System.out.println("║  1. Staff Management               ║");
                System.out.println("║  2. Ticket Operations              ║");
                System.out.println("║  3. View All Staff Info            ║");
                System.out.println("║  4. Logout                         ║");
                System.out.println("╚════════════════════════════════════╝");
                System.out.print("  Select option: ");

                try {
                    choice = Integer.parseInt(sc.nextLine().trim());
                } catch (NumberFormatException e) {
                    System.out.println("  Invalid input.");
                    continue;
                }

                switch (choice) {
                    case 1 -> loggedIn.displayMenu();
                    case 2 -> new StaffService().staffOperationMenu();
                    case 3 -> loggedIn.displayStaffInfo();
                    case 4 -> System.out.println("  Logging out...");
                    default -> System.out.println("  Invalid choice — select 1 to 4.");
                }

            } while (choice != 4);

        } else if (loggedIn.getRole().equalsIgnoreCase("Technician")) {
            Technician tech = new Technician(
                    loggedIn.getId(),
                    loggedIn.getUsername(),
                    loggedIn.getPassword(),
                    loggedIn.getRole(),
                    loggedIn.getSalary()
            );
            tech.displayMenu();
        }

        sc.close();
        System.out.println("\n  Session ended.");
    }
}