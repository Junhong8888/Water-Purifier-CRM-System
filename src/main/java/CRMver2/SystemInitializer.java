package CRMver2;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class SystemInitializer {

    public static void runLegacyStaffTests() throws IOException {
    // Keep file creation logic
    String dir = System.getProperty("user.dir") + File.separator;
    if (!new File(dir + "staff.txt").exists()) new File(dir + "staff.txt").createNewFile();
    if (!new File(dir + "customer.txt").exists()) new File(dir + "customer.txt").createNewFile();
    
    // Seed initial manager if file is empty
    //Staff staff = new Staff();
    // Logic to only write if file is empty would go here
    
    System.out.println("System Initialized. Welcome to Apple Vacation CRM.");
    // Removed: staff.displayStaffInfo(); 
}
}