package CRMver2;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class StaffService {

    public StaffService() {}
    
    public void registerStaff(String roleName) throws IOException {
        Scanner sc = new Scanner(System.in);
        ArrayList<Staff> currentStaff = loadStaffToList();

        System.out.println("\n--- " + roleName + " Registration ---");

        // 1. Role-Specific & Format Validated Username
        String username;
        while (true) {
            System.out.print("Enter Username (Letters and Numbers only): ");
            username = sc.nextLine().trim();
            
            if (username.isEmpty() || !username.matches("[a-zA-Z0-9]+")) {
                System.out.println("[ERROR] Username must contain ONLY letters and numbers (no spaces or symbols).");
                continue;
            }
            
            boolean isTakenInRole = false;
            for (Staff s : currentStaff) {
                if (s.getUsername().equalsIgnoreCase(username) && s.getRole().equalsIgnoreCase(roleName)) {
                    isTakenInRole = true;
                    break;
                }
            }
            
            if (isTakenInRole) System.out.println("[ERROR] A " + roleName + " with the name '" + username + "' already exists.");
            else break;
        }

        // 2. Password Validation (Min 6 chars)
        String password;
        while (true) {
            System.out.print("Enter Password (min 6 chars): ");
            password = sc.nextLine().trim();
            if (password.length() >= 6 && !password.contains(",")) break;
            System.out.println("[ERROR] Password must be at least 6 characters and contain no commas.");
        }
        
        // 3. Bulletproof Salary Validation
        double salary = 0;
        while (true) {
            try {
                System.out.print("Enter Monthly Salary: RM ");
                String salaryInput = sc.nextLine().trim();
                if (salaryInput.isEmpty()) continue;
                
                salary = Double.parseDouble(salaryInput);
                if (salary > 0) break;
                else System.out.println("[ERROR] Salary must be greater than 0.");
            } catch (NumberFormatException e) {
                System.out.println("[ERROR] Invalid format! Please enter numbers only.");
            }
        }

        Staff newStaff = new Staff("temp", username, password, roleName, salary);
        newStaff.assignID(); 
        newStaff.writeFile(newStaff.toString());

        System.out.println("\n[SUCCESS] " + roleName + " registered successfully! You may now login.");
    }

    public ArrayList<Staff> loadStaffToList() throws IOException {
        ArrayList<Staff> staffList = new ArrayList<>();
        String currentDirectory = System.getProperty("user.dir");
        File file = new File(currentDirectory + File.separator + "staff.txt");
        if (!file.exists()) return staffList;

        try(BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] strings = line.split(",");
                if (strings.length >= 5) {
                    staffList.add(new Staff(strings[0],strings[1], strings[2], strings[3],Double.parseDouble(strings[4])));
                }
            }
        }
        return staffList;
    }

    public void staffOperationMenu() throws IOException {
        Scanner sc = new Scanner(System.in);
        boolean exit = false;
        TicketService ts = new TicketService(); 

        do {
            System.out.println("\n=== Staff / Manager Dashboard ===");
            System.out.println("1. Central Dashboard (View All Tickets) - [Module 3]");
            System.out.println("2. Search & Filter Tickets - [Module 3]");
            System.out.println("3. Assign Ticket to Technician - [Module 3]");
            System.out.println("4. Add Notes & Update Ticket Status - [Module 3]");
            System.out.println("5. View Purifier Maintenance History - [Module 4]");
            System.out.println("6. View Monthly Reporting (Analytics) - [Module 4]");
            System.out.println("7. Logout");
            System.out.print("Enter your choice: ");
            
            // ANTI-CRASH: Read as string
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1" -> ts.centralDashboard();
                case "2" -> ts.searchAndFilter();
                case "3" -> ts.assignTechnician();
                case "4" -> ts.updateResponse();
                case "5" -> ts.viewMaintenanceHistory();
                case "6" -> ts.generateMonthlyReport();
                case "7" -> exit = true;
                default -> System.out.println("[ERROR] Invalid choice. Please enter a number 1-7.");
            }
        } while (!exit);
    }
}