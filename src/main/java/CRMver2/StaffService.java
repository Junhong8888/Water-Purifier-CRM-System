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

}