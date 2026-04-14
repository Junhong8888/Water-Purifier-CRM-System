package CRMver2;

import java.io.IOException;

public class Test {
    public static void main(String[] args) throws IOException {
        // This prints the tables you mentioned
        SystemInitializer.runLegacyStaffTests();

        // Immediately prompt the user to Log in or Register
        System.out.println("\n--- Initialized Successfully ---");
        MainMenu.startCRMSystem();
    }
}