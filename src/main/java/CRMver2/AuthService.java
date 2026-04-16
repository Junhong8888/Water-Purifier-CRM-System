package CRMver2;

import java.io.IOException;
import java.util.Scanner;

public class AuthService {
    
    public void loginSystem(String expectedRole) throws IOException {
        Scanner sc = new Scanner(System.in);
        
        while (true) {
            System.out.println("\n=== LOGIN (" + expectedRole + " Section) ===");
            System.out.print("Username: ");
            String username = sc.nextLine().trim();
            System.out.print("Password: ");
            String password = sc.nextLine().trim();

            // Prevent processing if empty
            if (username.isEmpty() || password.isEmpty()) {
                System.out.println("[ERROR] Username and Password cannot be empty.");
                continue;
            }

            boolean credentialsMatched = false;

            // 1. Check Staff / Manager / Technician Logins
            if (!expectedRole.equalsIgnoreCase("Customer")) {
                StaffService ss = new StaffService();
                StaffMenu staffMenu = new StaffMenu();
                for (Staff s : Staff.staffList) {
                    if (s.getUsername().equalsIgnoreCase(username) && s.getPassword().equals(password)) {
                        credentialsMatched = true;
                        
                        // Enforce Section-Based Login
                        if (s.getRole().equalsIgnoreCase(expectedRole)) {
                            System.out.println("\n[SUCCESS] Welcome " + s.getRole() + ": " + s.getUsername());
                            staffMenu.staffOperationMenu();
                            return; 
                        } else {
                            System.out.println("\n[ERROR] Role mismatch! You are registered as a " + s.getRole() + ".");
                            System.out.println("Please check if you have selected the correct role from the menu.");
                            return; 
                        }
                    }
                }
            }

            // 2. Check Customer Logins
            if (expectedRole.equalsIgnoreCase("Customer")) {
                CustomerService cs = new CustomerService();
                for (Customer c : cs.loadCustomersToList()) {
                    if (c.getUsername().equalsIgnoreCase(username) && c.getPassword().equals(password)) {
                        credentialsMatched = true;
                        
                        System.out.println("\n[SUCCESS] Welcome Customer: " + c.getUsername());
                        cs.customerProfileMenu(c);
                        return; 
                    }
                }
            }

            // 3. Handle Invalid Credentials
            if (!credentialsMatched) {
                System.out.println("\n[ERROR] Invalid Credentials or Account does not exist in this section.");
            }

            System.out.println("1. Try Again");
            System.out.println("2. Back to Main Menu");
            System.out.print("Choice: ");
            if (sc.nextLine().trim().equals("2")) {
                return; 
            }
        }
    }
}

