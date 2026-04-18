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

            // ==========================================
            // 1. Check Staff / Manager / Technician
            // ==========================================
            if (!expectedRole.equalsIgnoreCase("Customer")) {
                // Fresh load to ensure we see new registrations
                Staff.staffList = new StaffRepository().loadStaffToList();

                for (Staff s : Staff.staffList) {
                    // 1. Match Username AND Password AND the Section Role
                    if (s.getUsername().equalsIgnoreCase(username) &&
                            s.getPassword().equals(password) &&
                            s.getRole().equalsIgnoreCase(expectedRole)) {

                        credentialsMatched = true;
                        System.out.println("\n[SUCCESS] Welcome " + s.getRole() + ": " + s.getUsername());

                        // Route to specific dashboard
                        if (s.getRole().equalsIgnoreCase("Manager")) {
                            new ManagerMenu().dashBoard(s);
                        } else {
                            new TechinicianMenu().dashBoard(s);
                        }
                        return; // Exit method on successful login
                    }
                }

                // 2. Optional: Check if the user exists but in a DIFFERENT role
                // This provides a better error message if they picked the wrong menu option
                for (Staff s : Staff.staffList) {
                    if (s.getUsername().equalsIgnoreCase(username) && s.getPassword().equals(password)) {
                        System.out.println("\n[ERROR] Account found, but it is registered as a " + s.getRole() + ".");
                        System.out.println("Please go back and select the correct Login section.");
                        return;
                    }
                }
            }

            // ==========================================
            // 2. Check Customers
            // ==========================================
            if (expectedRole.equalsIgnoreCase("Customer")) {
                CustomerRepository cs = new CustomerRepository();
                CustomerMenu customerMenu = new CustomerMenu();

                // CustomerService already fetches fresh data via loadCustomersToList(),
                // so this was already safe from the caching bug!
                for (Customer c : cs.loadCustomersToList()) {
                    if (c.getUsername().equalsIgnoreCase(username) && c.getPassword().equals(password)) {
                        credentialsMatched = true;

                        System.out.println("\n[SUCCESS] Welcome Customer: " + c.getUsername());
                        customerMenu.customerProfileMenu(c);
                        return;
                    }
                }
            }

            // ==========================================
            // 3. Handle Invalid Credentials
            // ==========================================
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

