package CRM;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class StaffService {

    public StaffService() {

    }

    //Create a ticket object from the file then store into a list
    public ArrayList<Staff> loadStaffToList() throws IOException {
        //Read data from the text file,then store staff info to a ArrayList
        ArrayList<Staff> staffList = new ArrayList<>();
        //String currentDirectory = System.getProperty("user.dir");
        File file = new File("C:\\crmSystem\\staff.txt");

        try(BufferedReader br = new BufferedReader(new FileReader(file))) {

            String line;
            while ((line = br.readLine()) != null) {
                String[] strings = line.split(",");
                staffList.add(new Staff(strings[0],strings[1], strings[2], strings[3],Double.parseDouble(strings[4])));
            }
        }
        //br.close();
        return staffList;
    }

    public void staffOperationMenu() throws IOException {
        Scanner sc = new Scanner(System.in);
        int choice;
        boolean exit;

        do {
            //initialize flag to true
            exit = true;

            //Prompt CRM.Menu
            System.out.println("CRM.Staff Operation CRM.Menu");
            System.out.println("====================");
            System.out.println("1. View Submitted CRM.Ticket");
            System.out.println("2. Edit CRM.Ticket");
            System.out.println("3. View CRM.Ticket History");
            System.out.println("4. Search CRM.Ticket");
            System.out.println("5. Exit CRM.Staff");

            System.out.print("Enter your choice: ");
            choice = sc.nextInt();
            sc.nextLine();
            System.out.println();

            switch (choice) {
                case 1 -> new TicketService().viewSubmittedTicket();
                case 2 -> System.out.println("Edit CRM.Ticket");
                case 3 -> new TicketService().viewTicketHistory();
                case 4 -> new TicketService().searchTicket();
                case 5 -> exit = false;
                default -> System.out.println("Invalid choice");
            }
        } while (exit);
    }
}
