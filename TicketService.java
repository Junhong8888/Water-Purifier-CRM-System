package Other;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class TicketService {
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";

    public TicketService() {

    }

    //View submitted ticket with color output and format string
    public void viewSubmittedTicket() throws IOException {
        ArrayList<Ticket> tickets = loadTicketToList();
        tickets = tickets.stream()
                .filter(new Predicate<Ticket>() {
                    @Override
                    public boolean test(Ticket ticket) {
                        return (!ticket.getTicketStatus().equalsIgnoreCase("Completed"));
                    }
                }).collect(Collectors.toCollection(ArrayList::new));

        System.out.println("|=========================================================================================|");
        System.out.printf("|%-12s %-20s %-20s %-18s %-15s| %n",
                "Customer", "Ticket Status", "Priority Level", "Technician", "Date Submitted");
        System.out.println("|=========================================================================================|");


        for (int i = 0; i < tickets.size(); i++) {
            String customerID = tickets.get(i).getCustomerID();
            String ticketStatus = tickets.get(i).getTicketStatus();
            String priorityLevel = tickets.get(i).getPriorityLevel();
            String technicianID = tickets.get(i).getTechnician();
            String date = tickets.get(i).getDate();

            System.out.printf("|%-12s %-20s", customerID, ticketStatus);

            if (priorityLevel.equalsIgnoreCase("HIGH")) {
                System.out.printf(RED + " %-20s " + RESET, priorityLevel);
            } else {
                System.out.printf(" %-20s ", priorityLevel);
            }

            System.out.printf("%-18s %-15s| %n", technicianID, date);
        }
        System.out.println("|=========================================================================================|\n");
    }

    //View ticket history which only for Completed Ticket
    public void viewTicketHistory() throws IOException {
        ArrayList<Ticket> tickets = loadTicketToList();
        System.out.println("|=========================================================================================|");
        System.out.printf("|%-12s %-20s %-20s %-18s %-15s| %n",
                "Customer", "Ticket Status", "Priority Level", "Technician", "Date Submitted");
        System.out.println("|=========================================================================================|");

        tickets.stream()
                .filter(s -> s.getTicketStatus().equalsIgnoreCase("Completed"))
                .forEach(new Consumer<Ticket>() {
                    @Override
                    public void accept(Ticket ticket) {

                        String customerID = ticket.getCustomerID();
                        String ticketStatus = ticket.getTicketStatus();
                        String priorityLevel = ticket.getPriorityLevel();
                        String technicianID = ticket.getTechnician();
                        String date = ticket.getDate();

                        System.out.printf("|%-12s %-20s", customerID, ticketStatus);

                        if (priorityLevel.equalsIgnoreCase("HIGH")) {
                            System.out.printf(RED + " %-20s " + RESET, priorityLevel);
                        } else {
                            System.out.printf(" %-20s ", priorityLevel);
                        }

                        System.out.printf("%-18s %-15s| %n", technicianID, date);

                    }
                });

        System.out.println("|=========================================================================================|\n");
    }

    public void searchTicket() throws IOException {
        ArrayList<Ticket> tickets = loadTicketToList();
        tickets.stream().filter(s -> (!s.getTicketStatus().equalsIgnoreCase("Completed"))).collect(Collectors.toCollection(ArrayList::new));
        Scanner sc = new Scanner(System.in);

        //User input keyword
        System.out.print("Enter Keyword: ");
        String keyword = sc.nextLine();

        for (int i = 0; i < tickets.size(); i++) {
            Ticket ticket = tickets.get(i);
            String customerID = ticket.getCustomerID();
            String ticketStatus = ticket.getTicketStatus();
            String priorityLevel = ticket.getPriorityLevel().toUpperCase();
            String technicianID = ticket.getTechnician();
            String date = ticket.getDate();
            if (customerID.equalsIgnoreCase(keyword) || ticketStatus.equalsIgnoreCase(keyword) ||
                    priorityLevel.equalsIgnoreCase(keyword) || technicianID.equalsIgnoreCase(keyword)
                    || date.equalsIgnoreCase(keyword)) {
                System.out.println(ticket.toString());
            } else {
                System.out.println("Invalid Input. Please try again.");
                break;
            }
        }

        System.out.println();
    }


    //Add new function
    public void updateTicketStatus(Ticket ticket) {
        HashMap<Integer, String> map = new HashMap<>();

        map.put(1, "Completed");
        map.put(2, "Technician Assigned");
        map.put(3, "Pending");

        int flag = 0;
        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            if (entry.getValue().equalsIgnoreCase(ticket.getTicketStatus())) {
                flag = entry.getKey();
            }
        }

        ticket.setTicketStatus(map.get(flag));
    }



    //Create a ticket object from the file then store into a list
    public ArrayList<Ticket> loadTicketToList() throws IOException {
        ArrayList<Ticket> tickets = new ArrayList<Ticket>();
        //String currentDirectory = System.getProperty("user.dir");
        File file = new File("C:\\crmSystem\\ticket.txt");
        try(BufferedReader br = new BufferedReader(new FileReader(file))) {

            String line;
            while ((line = br.readLine()) != null) {
                String[] strings = line.split(",");
                for (int i = 0; i < strings.length; i++) {
                    if (strings[i].equalsIgnoreCase("null")) {
                        strings[i] = null;
                    }
                }
                tickets.add(new Ticket(strings[0], strings[1], strings[2],
                        strings[3], strings[4], strings[5], strings[6], strings[7], strings[8]));
            }
        }
        //br.close();
        Collections.sort(tickets);
        return tickets;
    }
}
