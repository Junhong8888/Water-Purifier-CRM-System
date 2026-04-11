package Other;

import module java.base;
import Main.FileStorage;
import Main.Menu;
import Main.User;


public class Staff extends User implements FileStorage, Menu {
        public static final String RESET = "\u001B[0m";
        public static final String RED = "\u001B[31m";
        public static final String GREEN = "\u001B[32m";

        private String role;

        public Staff() throws IOException {

        }

        public Staff(String username, String password, String role) throws IOException {
            super(username, password);
            this.role = role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getRole() {
            return role;
        }

        //Staff Credential
        public boolean credentials(String username, String password) throws IOException {
            ArrayList<Staff> staffs = loadStaffToList();

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

        public boolean verifyRole(String role) throws IOException {
            if (role.equalsIgnoreCase("Technician") || role.equalsIgnoreCase("Manager")) {
                return true;
            }
            return false;
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
                System.out.println("1. View Submitted Ticket"); //
                System.out.println("2. Edit Ticket");
                System.out.println("3. View Ticket History");
                System.out.println("4. Search Ticket");
                System.out.println("5. Exit Staff");

                System.out.print("Enter your choice: ");
                choice = sc.nextInt();
                sc.nextLine();
                System.out.println();

                switch (choice) {
                    case 1 -> viewSubmittedTicket();
                    case 2 -> System.out.println("Edit Ticket");
                    case 3 -> viewTicketHistory();
                    case 4 -> searchTicket();
                    case 5 -> exit = false;
                    default -> System.out.println("Invalid choice");
                }
            } while (exit);

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
            tickets.stream()
                    .filter(s -> s.getTicketStatus().equalsIgnoreCase("Completed"))
                    .forEach(new Consumer<Ticket>() {
                        @Override
                        public void accept(Ticket ticket) {
                            System.out.println("|=========================================================================================|");
                            System.out.printf("|%-12s %-20s %-20s %-18s %-15s| %n",
                                    "Customer", "Ticket Status", "Priority Level", "Technician", "Date Submitted");
                            System.out.println("|=========================================================================================|");


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

                            System.out.println("|=========================================================================================|\n");
                        }
                    });
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

        //Create a ticket object from the file then store into a list
        public ArrayList<Ticket> loadTicketToList() throws IOException {
            ArrayList<Ticket> tickets = new ArrayList<Ticket>();
            String currentDirectory = System.getProperty("user.dir");
            File file = new File(currentDirectory + File.separator + "ticket.txt");
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

        //Create a ticket object from the file then store into a list
        public ArrayList<Staff> loadStaffToList() throws IOException {
            //Read data from the text file,then store staff info to a ArrayList
            ArrayList<Staff> staffList = new ArrayList<Staff>();
            String currentDirectory = System.getProperty("user.dir");
            File file = new File(currentDirectory + File.separator + "staff.txt");

            try(BufferedReader br = new BufferedReader(new FileReader(file))) {

                String line;
                while ((line = br.readLine()) != null) {
                    String[] strings = line.split(",");
                    staffList.add(new Staff(strings[0], strings[1], strings[2]));
                }
            }
            //br.close();
            return staffList;
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
            return getUsername() + "," + getPassword() + "," + getRole();
        }
}




