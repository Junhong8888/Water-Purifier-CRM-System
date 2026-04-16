package CRMver2;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class StaffService {
    private final StaffRepository staffRepository;
    private final TicketService ticketService;

    // Shared instance to avoid repeated file loading overhead
    //private final TicketService ticketService = new TicketService();

    public StaffService() {
        this.staffRepository = new StaffRepository();
        this.ticketService = new TicketService();
    }

    // ---------------------------------------------------------------
    // OPERATIONAL LOGIC
    // ---------------------------------------------------------------

    public void displayStaffInfo() throws IOException {
        ArrayList<Staff> staffList = staffRepository.loadStaffToList();
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


   /* *//**
     * Handles registration for Staff, Managers, and Technicians.
     *//*
    public void registerStaff(String roleName) throws IOException {
        Scanner sc = new Scanner(System.in);
        ArrayList<Staff> currentStaff = loadStaffToList();

        System.out.println("\n--- " + roleName + " Registration ---");

        String username;
        while (true) {
            System.out.print("Enter Username (Alphanumeric only): ");
            username = sc.nextLine().trim();

            if (username.isEmpty() || !username.matches("[a-zA-Z0-9]+")) {
                System.out.println("[ERROR] Invalid format.");
                continue;
            }

            boolean isTaken = false;
            for (Staff s : currentStaff) {
                if (s.getUsername().equalsIgnoreCase(username) && s.getRole().equalsIgnoreCase(roleName)) {
                    isTaken = true;
                    break;
                }
            }
            if (isTaken) System.out.println("[ERROR] Username already exists for this role.");
            else break;
        }

        System.out.print("Enter Password (min 6 chars): ");
        String password = sc.nextLine().trim();

        double salary = 0;
        while (true) {
            try {
                System.out.print("Enter Monthly Salary: RM ");
                salary = Double.parseDouble(sc.nextLine().trim());
                if (salary > 0) break;
            } catch (Exception e) {
                System.out.println("[ERROR] Invalid numeric value.");
            }
        }

        Staff newStaff = new Staff("temp", username, password, roleName, salary);
        newStaff.assignID();
        newStaff.writeFile(newStaff.toString());

        Staff.staffList.add(newStaff);

        System.out.println("\n[SUCCESS] Registration complete.");
    }*/
}