package CRMv_final;

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

            if (username.isEmpty() || password.isEmpty()) {
                System.out.println("[ERROR] Username and Password cannot be empty.");
                continue;
            }

            boolean credentialsMatched = false;

            // ==========================================
            // 1. Check Staff / Manager / Technician
            // ==========================================
            if (!expectedRole.equalsIgnoreCase("Customer")) {

                // Always reload from file so newly registered accounts are visible
                Staff.staffList = new StaffRepository().loadStaffToList();

                for (Staff s : Staff.staffList) {
                    if (s.getUsername().equalsIgnoreCase(username)
                            && s.getPassword().equals(password)
                            && s.getRole().equalsIgnoreCase(expectedRole)) {

                        // Exact match: username + password + correct role
                        System.out.println("\n[SUCCESS] Welcome " + s.getRole() + ": " + s.getUsername());
                        if (s.getRole().equalsIgnoreCase("Manager")) {
                            new ManagerMenu().dashBoard();
                        } else {
                            new TechinicianMenu().dashBoard();
                        }
                        return;
                    }
                }

                // BUG FIX: After checking ALL entries, NOW check if credentials existed
                // but under a different role. This prevents the old code from returning
                // immediately when Manager "Chok" is found while logging in as Technician,
                // which blocked Technician "Chok" from ever being found.
                for (Staff s : Staff.staffList) {
                    if (s.getUsername().equalsIgnoreCase(username)
                            && s.getPassword().equals(password)) {
                        credentialsMatched = true;
                        // Credentials exist but no matching role found in the first loop
                        System.out.println("\n[ERROR] No " + expectedRole
                                + " account found with these credentials.");
                        System.out.println("You have an account as: " + s.getRole()
                                + ". Please login under the correct section.");
                        break;
                    }
                }
            }

            // ==========================================
            // 2. Check Customers
            // ==========================================
            if (expectedRole.equalsIgnoreCase("Customer")) {
                CustomerRepository cs = new CustomerRepository();
                CustomerMenu customerMenu = new CustomerMenu();

                for (Customer c : cs.loadCustomersToList()) {
                    if (c.getUsername().equalsIgnoreCase(username)
                            && c.getPassword().equals(password)) {
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
