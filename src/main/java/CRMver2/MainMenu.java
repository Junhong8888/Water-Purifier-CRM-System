package CRMver2;

import java.io.IOException;
import java.util.Scanner;

public class MainMenu {

    public static void startCRMSystem() throws IOException {
        Scanner sc = new Scanner(System.in);
        
        while (true) {
            System.out.println("\n          .:'");
            System.out.println("      __ :'__");
            System.out.println("   .'`__`-'__``.");
            System.out.println("  :__________.-'");
            System.out.println("  :_________:");
            System.out.println("   :_________`-;");
            System.out.println("    `.__.-.__.'");
            System.out.println("============================================");
            System.out.println("   Water Purifier CRM System (Module 1)   ");
            System.out.println("============================================");
            System.out.println("1. Register New Account"); 
            System.out.println("2. Login as Existing User");
            System.out.println("3. Exit");
            System.out.print("Select an option: ");
            
            String choice = sc.nextLine().trim();

            if (choice.equals("1")) {
                System.out.println("\nRegister as:");
                System.out.println("1. Customer\n2. Staff\n3. Manager\n4. Technician");
                System.out.print("Choice: ");
                String regChoice = sc.nextLine().trim();
                handleRegistration(regChoice);
                
            } else if (choice.equals("2")) {
                System.out.println("\nLogin to section:");
                System.out.println("1. Customer\n2. Staff\n3. Manager\n4. Technician");
                System.out.print("Choice: ");
                String loginChoice = sc.nextLine().trim();
                handleLogin(loginChoice);
                
            } else if (choice.equals("3")) {
                System.out.println("Exiting system...");
                break;
            } else {
                System.out.println("[ERROR] Invalid option. Please enter 1, 2, or 3.");
            }
        }
    }

    private static void handleRegistration(String roleChoice) throws IOException {
        switch (roleChoice) {
            case "1" -> new CustomerService().registerCustomer();
            case "2" -> new StaffService().registerStaff("Staff");
            case "3" -> new StaffService().registerStaff("Manager"); 
            case "4" -> new StaffService().registerStaff("Technician"); 
            default -> System.out.println("[ERROR] Invalid Role Choice. Returning to Main Menu.");
        }
    }

    private static void handleLogin(String roleChoice) throws IOException {
        String role = "";
        switch (roleChoice) {
            case "1" -> role = "Customer";
            case "2" -> role = "Staff";
            case "3" -> role = "Manager";
            case "4" -> role = "Technician";
            default -> {
                System.out.println("[ERROR] Invalid Role Selection. Returning to Main Menu.");
                return;
            }
        }
        new AuthService().loginSystem(role); 
    }
}