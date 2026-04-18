package CRMver2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Test {
    public static void main(String[] args) throws IOException {
        // This prints the tables you mentioned
        SystemInitializer.runLegacyStaffTests();
        //start();
        // Immediately prompt the user to Log in or Register
        System.out.println("\n--- Initialized Successfully ---");
        startCRMSystem();

        /*// Create our users
        User myCustomer = new Customer();
        User myAdmin = new Manager();

        // Use polymorphism to store different services in one list
        List<RegistrationService> services = new ArrayList<>();
        services.add(new CustomerService());
        services.add(new ManagerService());

        // Polymorphic Execution
        System.out.println("--- Starting Registration Batch ---");

        // The loop doesn't know if it's calling CustomerService or AdminService
        // It simply knows that every object in the list obeys the RegistrationService contract
        services.get(0).register(myCustomer);
        System.out.println("-----------------------------------");
        services.get(1).register(myAdmin);

        System.out.println("--- Batch Complete ---");*/
    }

    /*public static void start() throws IOException {
        Scanner sc = new Scanner(System.in);
        // Initialize the service so it isn't null
        RegistrationService registrationService = new StaffService();

        System.out.println("Who you are?");
        System.out.println("1. Register");
        System.out.println("2. Login");
        int mainChoice = sc.nextInt();

        if (mainChoice == 1) {
            System.out.println("1. Customer");
            System.out.println("2. Staff");
            int userType = sc.nextInt(); // Use a new variable for the sub-menu

            if (userType == 1) {
                registrationService.register(new Customer());
            } else if (userType == 2) {
                registrationService.register(new Staff());
            }
        }
    }*/

    public static void startCRMSystem() throws IOException {
        // 1. Setup files, seed data, and sync static lists/counters
        MainMenu.initializeSystemResources();


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
                System.out.println("1. Customer\n2. Manager\n3. Technician");
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
        StaffService ss = new StaffService();

        switch (roleChoice) {
            case "1" -> new CustomerService().register(new Customer());
            //case "2" -> new StaffService().;
            case "2" -> {
                Manager  m = new Manager();
                m.setRole("Manager");
                new StaffService().register(m);
            }
            case "3" -> {
                Technician t = new Technician();
                t.setRole("Technician");
                new StaffService().register(t);
            }
            default -> System.out.println("[ERROR] Invalid Role Choice.");
        }
    }

    private static void handleLogin(String roleChoice) throws IOException {
        String role = "";
        switch (roleChoice) {
            case "1" -> role = "Customer";
            //case "2" -> role = "Staff";
            case "2" -> role = "Manager";
            case "3" -> role = "Technician";
            default -> {
                System.out.println("[ERROR] Invalid Role Selection.");
                return;
            }
        }
        // Redirects to the appropriate dashboard upon success
        new AuthService().loginSystem(role);
    }
}