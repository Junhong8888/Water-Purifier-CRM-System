package Other;

import module java.base;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

//this class focus on create staff id and
public class Staff extends User implements FileStorage, Menu {
        private String role;
        private double salary;
        static int counter = 1;

        public Staff() throws IOException {

        }

        public Staff(String id,String username, String password, String role,double salary) throws IOException {
            super(id,username, password);
            this.role = role;
            this.salary = salary;
        }


        public void setRole(String role) {
            this.role = role;
        }

        public String getRole() {
            return role;
        }

        public void setSalary(double salary) {
            this.salary = salary;
        }

        public double getSalary() {
            return salary;
        }

        //ID for different role
        public void assignID(){
            if(getRole().equals("Technician")){
                setId("t" + counter);
            } else if(getRole().equals("Manager")){
                setId("m" + counter);
            }
            counter++;
        }

        //Staff Credential
        public boolean credentials(String username, String password) throws IOException {
            ArrayList<Staff> staffs = new StaffService().loadStaffToList();

            for (Staff staff : staffs) {
                // First check if credentials match
                if (staff.getUsername().equals(username) && staff.getPassword().equals(password)) {
                    // Then check if they have the right role
                    if (verifyRole(staff.getRole())) {
                        return true;
                    } else {
                        System.out.println("Incorrect role");
                        return false;
                    }
                }
            }
            System.out.println("Invalid Username or Password.");
            return false;
        }

        //Verify role
        public boolean verifyRole(String role) throws IOException {
            if (role.equalsIgnoreCase("Technician") || role.equalsIgnoreCase("Manager")) {
                return true;
            }
            return false;
        }

        public void displayStaffInfo() throws IOException {
            ArrayList<Staff> staffList = new StaffService().loadStaffToList();

            //Display Logic
            System.out.println("|=================================================================|");
            System.out.printf("|%-14s %-14s %-10s %-8s %s| %n",
                    "Staff ID", "Username", "Role", "Salary", "Ticket Assigned");
            System.out.println("|=================================================================|");

            //Iterate over list
            staffList.stream().forEach(new Consumer<Staff>() {
                public void accept(Staff staff) {
                    String id = staff.getId();
                    String username = staff.getUsername();
                    String role = staff.getRole();
                    double salary = staff.getSalary();
                    int ticketAssigned = 0;
                    try {
                        ticketAssigned = countAssignedTickets(id);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }


                    System.out.printf("|%-14s %-14s %-10s %-15s %-8s| %n",
                            id, username, role, salary, ticketAssigned);
                }
            });

            System.out.println("|=================================================================|\n");
        }

        public int countAssignedTickets(String technicianID) throws IOException {
            ArrayList<Ticket> ticketList = new TicketService().loadTicketToList();
            long count = ticketList.stream()
                    .filter(ticket -> ticket.getTicketStatus().equalsIgnoreCase("completed") )
                    .filter(ticket -> ticket.getTechnician().equals(technicianID)).count();
            return (int) count;
        }

        //Staff Main Menu
        @Override
        public void displayMenu() throws IOException {
            Scanner sc = new Scanner(System.in);
            int choice;
            boolean exit;

            do {
                //initialize flag to true
                exit = true;

                //Prompt Menu
                System.out.println("Staff Menu");
                System.out.println("====================");
                System.out.println("1. View Submitted Ticket");
                System.out.println("2. Edit Ticket");
                System.out.println("3. View Ticket History");
                System.out.println("4. Search Ticket");
                System.out.println("5. Exit Staff");

                System.out.print("Enter your choice: ");
                choice = sc.nextInt();
                sc.nextLine();
                System.out.println();

                switch (choice) {
                    case 1 -> new TicketService().viewSubmittedTicket();
                    case 2 -> System.out.println("Edit Ticket");
                    case 3 -> new TicketService().viewTicketHistory();
                    case 4 -> new TicketService().searchTicket();
                    case 5 -> exit = false;
                    default -> System.out.println("Invalid choice");
                }
            } while (exit);

        }

        //Write data into staff file
        @Override
        public void writeFile(String data) throws IOException {
            //pass staff info in terms of string, then write the file
            String currentDirectory = System.getProperty("user.dir");
            File file = new File(currentDirectory + File.separator + "staff.txt");

            if (!file.exists()) {
                boolean newFile = file.createNewFile();
                System.out.println(newFile);
            }

            BufferedWriter fr = new BufferedWriter(
                    new FileWriter(file));

            fr.write(data,0,data.length());
            fr.newLine();
            fr.close();
        }

        @Override
        public String toString() {
            return getId() + "," + getUsername() + "," + getPassword() + "," + getRole() + getSalary();
        }
}




