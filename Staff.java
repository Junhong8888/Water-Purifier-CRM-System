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
        private static int counter;
        static ArrayList<Staff> staffList;

        static {
            try {
                staffList = new StaffService().loadStaffToList();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

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
        public boolean credentials(String id,String username, String password) throws IOException {
            //ArrayList<Staff> staffs = new StaffService().loadStaffToList();


            for (Staff staff : staffList) {
                // First check if credentials match
                if (staff.getId().equals(id) && staff.getUsername().equals(username) && staff.getPassword().equals(password)) {
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
            //ArrayList<Staff> staffList = new StaffService().loadStaffToList();

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
                System.out.println("1. Add Staff");
                System.out.println("2. Remove Staff");
                System.out.println("3. Update Staff");
                System.out.println("4. Exit");

                System.out.print("Enter your choice: ");
                choice = sc.nextInt();
                sc.nextLine();
                System.out.println();

                switch (choice) {
                    case 1 -> addStaff();
                    case 2 -> {
                        if(removeStaff()){
                            System.out.println("Staff has been removed.");
                        } else{
                            System.out.println("Not valid staff ID");
                        }
                    }
                    case 3 -> {
                        if(updateStaff()){
                            System.out.println("Staff has been updated.");
                        } else{
                            System.out.println("Not valid staff ID");
                        }
                    }
                    case 4 -> exit =  false;
                    default -> System.out.println("Invalid choice");
                }
            } while (exit);
        }

        public boolean addStaff() throws IOException {
            //ArrayList<Staff> staffList = new StaffService().loadStaffToList();
            Scanner sc = new Scanner(System.in);
            String username;
            String password;
            String role = null;
            double salary;
            int choice;
            do{
                System.out.print("Enter Username: ");
                username = sc.nextLine();
                System.out.print("Enter Password: ");
                password = sc.nextLine();
                System.out.print("Enter Role (1. Technician, 2. Manager): ");
                choice = sc.nextInt();
                switch (choice) {
                    case 1 -> role = "Technician";
                    case 2  -> role = "Manager";
                    default -> System.out.println("Invalid choice");
                }
                sc.nextLine();
                System.out.print("Enter Salary: ");
                salary = sc.nextDouble();

            }while(username != null && password != null && (choice != 1 && choice != 2) && salary < 0);

            Staff staff = new Staff();
            staff.setUsername(username);
            staff.setPassword(password);
            staff.setRole(role);
            staff.setSalary(salary);
            staff.assignID();
            staffList.add(staff);
            return true;
        }

        //Remove Staff
        public boolean removeStaff() throws IOException {
            //ArrayList<Staff> staffToRemove = new StaffService().loadStaffToList();
            System.out.println("Enter Staff ID: ");
            String staffID = new Scanner(System.in).nextLine();

            for (int i = 0; i < staffList.size(); i++) {
                if(staffList.get(i).getId().equals(staffID)){
                    staffList.remove(i);
                    return true;
                }
            }
            return false;
        }

        //Update Staff
        public boolean updateStaff() throws IOException {
            //ArrayList<Staff> staff = new StaffService().loadStaffToList();
            Scanner sc = new Scanner(System.in);
            Staff s = null;
            int choice;
            String newInput;
            double newSalary;

            System.out.println("Enter Staff ID: ");
            String staffID = sc.nextLine();

            for (int i = 0; i < staffList.size(); i++) {
                Staff temp = staffList.get(i);
                if(temp.getId().equals(staffID)){
                    s = temp;
                }
            }

            if(s != null) {
                do {
                    System.out.println("Choose what you want to update: ");
                    System.out.println("1.Username");
                    System.out.println("2. Password");
                    System.out.println("3. Role");
                    System.out.println("4. Salary");
                    System.out.println("5. Exit Update");
                    System.out.print("Enter your choice: ");
                    choice = sc.nextInt();
                    sc.nextLine();

                    switch (choice) {
                        case 1 -> {
                            System.out.println("Enter New Username: ");
                            newInput = sc.nextLine();
                            s.setUsername(newInput);
                            System.out.println("Updated Username: " + s.getUsername());
                        }
                        case 2 -> {
                            System.out.println("Enter New Password: ");
                            newInput = sc.nextLine();
                            s.setPassword(newInput);
                            System.out.println("Updated Password: " + s.getPassword());
                        }
                        case 3 -> {
                            System.out.println("Enter New Role: ");
                            newInput = sc.nextLine();
                            s.setRole(newInput);
                            System.out.println("Updated Role: " + s.getRole());
                        }
                        case 4 -> {
                            System.out.println("Enter New Salary: ");
                            salary = sc.nextDouble();
                            s.setSalary(salary);
                            System.out.println("Updated Salary: " + s.getSalary());
                        }
                    }
                } while (choice != 5);

                return true;
            }
            return false;
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
            return getId() + "," + getUsername() + "," + getPassword() + "," + getRole() + "," + getSalary();
        }
}




