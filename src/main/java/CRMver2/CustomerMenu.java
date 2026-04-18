package CRMver2;

import java.awt.*;
import java.io.IOException;
import java.util.Scanner;

public class CustomerMenu{
    private CustomerService customerService;


    public CustomerMenu() {
        customerService = new CustomerService();
    }

    // ── Customer Dashboard Menu ────────────────────────────────────────────────
    public void customerProfileMenu(Customer loggedIn) throws IOException {
        Scanner sc = new Scanner(System.in);
        TicketService ts = new TicketService();
        TicketMenu tm = new TicketMenu();
        String ticketID = null ;
        boolean exit = false;

        SmartScheduler.checkMaintenanceDue(loggedIn);

        do {
            System.out.println("\n=== Customer Dashboard — Welcome, " + loggedIn.getUsername() + " ===");
            System.out.println("1. Submit Service Request");
            System.out.println("2. Track My Tickets");
            System.out.println("3. Close Ticket & Give Feedback");
            System.out.println("4. Register Purifier Model");
            System.out.println("5. Logout");
            System.out.print("Choice: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1" -> ticketID = tm.showSubmitTicket(loggedIn.getId());
                case "2" -> tm.showTrackTicketStatus(loggedIn.getId());
                case "3" -> {
                    System.out.print("Enter Ticket ID: ");
                    try {
                        ticketID = sc.nextLine().trim();
                    } catch (Exception e) {
                        System.out.println("[ERROR] Invalid Ticket ID! Please enter a valid Ticket ID.");
                    }
                    tm.showCloseTicketAndFeedback(ticketID,loggedIn.getId());
                }
                case "4" -> {
                    String newModel;
                    while (true) {
                        System.out.print("New Model (Alkaline / RO System / Mineral): ");
                        newModel = sc.nextLine().trim();
                        if (newModel.equalsIgnoreCase("Alkaline") ||
                                newModel.equalsIgnoreCase("RO System") ||
                                newModel.equalsIgnoreCase("Mineral")) break;
                        System.out.println("[ERROR] Invalid model.");
                    }

                    String newDate;
                    while (true) {
                        System.out.print("New Installation Date (DD-MM-YYYY): ");
                        newDate = sc.nextLine().trim();
                        if (newDate.matches("\\d{2}-\\d{2}-\\d{4}")) break;
                        System.out.println("[ERROR] Use DD-MM-YYYY format.");
                    };
                    loggedIn.setPurifierModel(new WaterPurifier(newModel, newDate));
                    System.out.println("[SUCCESS] Date updated!");
                    customerService.updateCustomerFile(loggedIn);
                    System.out.println("[SUCCESS] Model updated!");
                }
                case "5" -> exit = true;
                default  -> System.out.println("[ERROR] Enter 1-6.");
            }
        } while (!exit);
    }
}
