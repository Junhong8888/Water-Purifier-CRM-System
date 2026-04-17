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
        MainMenu.startCRMSystem();

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

}