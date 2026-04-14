package CRMver2;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class CustomerService {
    private ArrayList<Customer> customerList;

    public CustomerService() {
        customerList = new ArrayList<>();
    }

    public ArrayList<Customer> loadCustomersToList() throws IOException {
        customerList.clear();
        String currentDirectory = System.getProperty("user.dir");
        File file = new File(currentDirectory + File.separator + "customer.txt");

        if (!file.exists()) return customerList;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 8) {
                    customerList.add(new Customer(data[0], data[1], data[2], data[3], data[4], data[5], data[6], data[7]));
                }
            }
        }
        return customerList;
    }

    public void registerCustomer() throws IOException {
        Scanner sc = new Scanner(System.in);
        loadCustomersToList();

        System.out.println("\n=== Customer Registration ===");
        
        // 1. Strict Username Validation (Characters ONLY)
        String username;
        while (true) {
            System.out.print("Enter Username (Letters only): ");
            username = sc.nextLine().trim();
            
            if (username.isEmpty() || !username.matches("[a-zA-Z]+")) {
                System.out.println("[ERROR] Username must contain ONLY letters (no numbers, spaces, or symbols).");
                continue;
            }
            boolean taken = false;
            for (Customer c : customerList) {
                if (c.getUsername().equalsIgnoreCase(username)) {
                    taken = true; break;
                }
            }
            if (taken) System.out.println("[ERROR] Username already exists!");
            else break;
        }

        // 2. Strict Password Validation
        String password;
        while (true) {
            System.out.print("Enter Password (min 6 chars): ");
            password = sc.nextLine().trim();
            if (password.length() < 6 || password.contains(",")) {
                System.out.println("[ERROR] Password must be at least 6 characters and contain no commas.");
            } else {
                break;
            }
        }

        // 3. Email Validation
        String email;
        while (true) {
            System.out.print("Enter Email Address: ");
            email = sc.nextLine().trim();
            if (email.isEmpty() || email.contains(",") || !email.contains("@") || !email.contains(".")) {
                System.out.println("[ERROR] Please enter a valid Email Address (e.g., user@mail.com).");
            } else {
                break;
            }
        }

        // 4. Contact Number Validation (NUMBERS ONLY)
        String contact;
        while (true) {
            System.out.print("Enter Contact Number (Numbers only): ");
            contact = sc.nextLine().trim();
            if (contact.isEmpty() || !contact.matches("\\d+")) {
                System.out.println("[ERROR] Contact must contain ONLY numbers (no spaces, dashes, or letters).");
            } else break;
        }

        // 5. Home Address Validation
        String address;
        while (true) {
            System.out.print("Enter Home Address: ");
            address = sc.nextLine().trim();
            if (address.isEmpty() || address.contains(",")) {
                System.out.println("[ERROR] Address cannot be empty or contain commas.");
            } else break;
        }

        // 6. Water Purifier String Validation
        String model;
        while (true) {
            System.out.print("Enter Model (Alkaline / RO System / Mineral): ");
            model = sc.nextLine().trim();
            if (model.equalsIgnoreCase("Alkaline") || model.equalsIgnoreCase("RO System") || model.equalsIgnoreCase("Mineral")) {
                break;
            }
            System.out.println("[ERROR] Invalid Model! Please enter Alkaline, RO System, or Mineral.");
        }

        String newId = "C" + (customerList.size() + 1);
        Customer newCust = new Customer(newId, username, password, email, contact, address, model, "01-01-2026");
        
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("customer.txt", true))) {
            bw.write(newCust.toString());
            bw.newLine();
        }
        
        System.out.println("\n[SUCCESS] Customer Registration Successful! You may now login.");
    }
    
    public void customerProfileMenu(Customer loggedInCustomer) throws IOException {
        Scanner sc = new Scanner(System.in);
        boolean exit = false;

        do {
            System.out.println("\n=== Customer Dashboard ===");
            System.out.println("1. Update Purifier Model");
            System.out.println("2. Update Installation Date");
            System.out.println("3. Logout");
            System.out.print("Choice: ");
            
            // ANTI-CRASH: Changed to String choice
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1" -> {
                    String newModel;
                    while (true) {
                        System.out.print("Enter new Purifier Model (Alkaline / RO System / Mineral): ");
                        newModel = sc.nextLine().trim();
                        if (newModel.equalsIgnoreCase("Alkaline") || newModel.equalsIgnoreCase("RO System") || newModel.equalsIgnoreCase("Mineral")) {
                            break;
                        }
                        System.out.println("[ERROR] Invalid Model! Please enter Alkaline, RO System, or Mineral.");
                    }
                    loggedInCustomer.setPurifierModel(newModel);
                    updateCustomerFile(loggedInCustomer);
                    System.out.println("[SUCCESS] Model Updated Successfully!");
                }
                case "2" -> {
                    // STRICT DATE FORMAT VALIDATION
                    String newDate;
                    while (true) {
                        System.out.print("Enter new Installation Date (DD-MM-YYYY): ");
                        newDate = sc.nextLine().trim();
                        if (newDate.matches("\\d{2}-\\d{2}-\\d{4}")) {
                            break;
                        }
                        System.out.println("[ERROR] Invalid format! Please use DD-MM-YYYY (e.g., 15-05-2026).");
                    }
                    loggedInCustomer.setInstallationDate(newDate);
                    updateCustomerFile(loggedInCustomer);
                    System.out.println("[SUCCESS] Date Updated Successfully!");
                }
                case "3" -> exit = true;
                default -> System.out.println("[ERROR] Invalid Choice. Please enter 1, 2, or 3.");
            }
        } while (!exit);
    }

    private void updateCustomerFile(Customer updatedCustomer) throws IOException {
        loadCustomersToList();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("customer.txt", false))) {
            for (Customer c : customerList) {
                if (c.getId().equals(updatedCustomer.getId())) {
                    bw.write(updatedCustomer.toString());
                } else {
                    bw.write(c.toString());
                }
                bw.newLine();
            }
        }
    }
}