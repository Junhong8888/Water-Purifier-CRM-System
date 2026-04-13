package CRM;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Test {


    public static void main(String[] args) throws IOException {


            File file3 = new File("C:\\crmSystem");
            file3.mkdir();
            File file = new File("C:\\crmSystem\\staff.txt");
            File file2 = new File("C:\\crmSystem\\ticket.txt");

            file.createNewFile();
            file2.createNewFile();

            // ✅ CORRECT FORMAT: id,username,password,role,salary
            Staff staff = new Staff();
            staff.writeFile("m001,Junhong,junhong,Manager,2500.00");
            new StaffService().loadStaffToList();

            // ✅ Write ticket data to ticket.txt (NOT staff.txt)
            List<String> ticketData = Arrays.asList(
                    "customerID,in progress,high,technicianID,08-04-2025,null,Repair Water Purifier,null,null",
                    "customerID2,in progress,high,technicianID2,14-04-2025,null,null,null,null",
                    "junhong,in progress,low,adam,14-04-2025,null,null,null,null",
                    "junhong,completed,low,adam,14-04-2025,null,null,null,null",
                    "customerID2,completed,high,m001,15-04-2025,null,null,null,null",
                    "customerID3,in progress,high,m002,15-04-2025,null,null,null,null"
            );

            // Write ticket data to ticket.txt instead of calling writeAllTickets
            try (java.io.BufferedWriter bw = new java.io.BufferedWriter(
                    new java.io.FileWriter(file2))) {
                for (String line : ticketData) {
                    bw.write(line);
                    bw.newLine();
                }
            }

            // ✅ Now safely create ticket and booking
            Ticket ticket = new Ticket("customerID","in progress","high","technicianID","08-04-2025",null,"Repair Water Purifier",null,null);
            ticket.makeBooking();
            System.out.println(ticket.getTimeSlotBooking());

            // ✅ Rest of your code
            staff.displayStaffInfo();
            staff.addStaff();
            staff.displayStaffInfo();
            staff.updateStaff();
            staff.displayStaffInfo();

            Scanner sc = new Scanner(System.in);
            String staffID;
            String username;
            String password;

            do {
                System.out.println("Please enter your staff ID:");
                staffID = sc.nextLine();
                System.out.println("Please enter your username:");
                username = sc.nextLine();
                System.out.println("Please enter your password:");
                password = sc.nextLine();
                staff.setUsername(username);
                staff.setPassword(password);
            } while (!staff.credentials(staffID, username, password));

            System.out.println();
            new StaffService().staffOperationMenu();

    }
}
