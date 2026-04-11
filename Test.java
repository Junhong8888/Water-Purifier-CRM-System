package Other;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Test {


    public static void main(String[] args) throws IOException {

        {
            Staff staff = new Staff();
            staff.writeFile("Junhong,junhong,Manager");

            Ticket ticket = new Ticket();

            List<String> ticketData = Arrays.asList(
                    "customerID,in progress,high,technicianID,08-04-2025,null,null,null,null",
                    "customerID2,in progress,high,technicianID2,07-04-2025,null,null,null,null",
                    "junhong,in progress,low,adam,08-04-2025,null,null,null,null"
            );
            ticket.writeAllTickets(ticketData);
        }

        Staff staff = new Staff();
        //Declare value
        Scanner sc = new Scanner(System.in);
        String username;
        String password;

        do {
            System.out.println("Please enter your username:");
            username = sc.nextLine();
            System.out.println("Please enter your password:");
            password = sc.nextLine();
            staff.setUsername(username);
            staff.setPassword(password);
        } while (!staff.credentials(username, password));

        System.out.println();
        staff.displayMenu();
    }
}
