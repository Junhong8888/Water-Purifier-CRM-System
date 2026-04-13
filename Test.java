package Other;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Test {


    public static void main(String[] args) throws IOException {

        {
            /*File file3 = new File("C:\\crmSystem");
            file3.mkdir();
            File file = new File("C:\\crmSystem\\staff.txt");
            File file2 = new File("C:\\crmSystem\\ticket.txt");

            file.createNewFile();
            file2.createNewFile();*/


            Staff staff = new Staff();
            staff.writeFile("m001,Junhong,junhong,Manager,2500.00");

            Ticket ticket = new Ticket("customerID","in progress","high","technicianID","08-04-2025",null,"Repair Water Purifier",null,null);
            //ticket.assignID();
            ticket.makeBooking();
            System.out.println(ticket.getTimeSlotBooking());

            List<String> ticketData = Arrays.asList(
                    "customerID,in progress,high,technicianID,08-04-2025,null,null,null,null",
                    "customerID2,in progress,high,technicianID2,07-04-2025,null,null,null,null",
                    "junhong,in progress,low,adam,08-04-2025,null,null,null,null",
                    "junhong,completed,low,adam,08-04-2025,null,null,null,null",
                    "customerID2,completed,high,m001,07-04-2025,null,null,null,null",
                    "customerID3,in progress,high,m002,07-04-2025,null,null,null,null"
            );
            ticket.writeAllTickets(ticketData);
        }



        Staff staff = new Staff();
        staff.displayStaffInfo();
        staff.addStaff();
        staff.displayStaffInfo();
        //staff.removeStaff("m001");
        staff.displayStaffInfo();
        staff.updateStaff();
        staff.displayStaffInfo();

        //Declare value
        Scanner sc = new Scanner(System.in);
        String staffID ;
        String username;
        String password;

        do {
            System.out.println("Please enter your username:");
            staffID = sc.nextLine();
            System.out.println("Please enter your username:");
            username = sc.nextLine();
            System.out.println("Please enter your password:");
            password = sc.nextLine();
            staff.setUsername(username);
            staff.setPassword(password);
        } while (!staff.credentials(staffID,username, password));

        System.out.println();
        new StaffService().staffOperationMenu();
    }
}
