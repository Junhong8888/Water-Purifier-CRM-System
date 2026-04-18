package CRMver2;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;


public class CustomerService implements RegistrationService<Customer>{
    private ArrayList<Customer> customerList;
    private CustomerRepository customerRepo;

    public CustomerService() {
        customerList = new ArrayList<>();
        customerRepo = new CustomerRepository();
    }


    public void updateCustomerFile(Customer updatedCustomer) throws IOException {
        this.customerList = customerRepo.loadCustomersToList();
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

    @Override
    public void register(Customer user) throws IOException {
        Scanner sc = new Scanner(System.in);
        // FIX: You must assign the returned list to your local variable!
        this.customerList = customerRepo.loadCustomersToList();

        // Now when you check size or maxId, it won't be 0
        //System.out.println("Debug: System found " + customerList.size() + " existing customers.");

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

        int maxId = 0;
        for (Customer c : customerList) {
            // Extract number from "C12" -> 12
            int idNum = Integer.parseInt(c.getId().substring(1));
            if (idNum > maxId) maxId = idNum;
        }
        String newId = "C" + (maxId + 1);
        Customer newCust = new Customer(newId, username, password, email, contact, new Address(address), new WaterPurifier(model,"01-01-2026"));

        try (BufferedWriter bw = new BufferedWriter(new FileWriter("customer.txt", true))) {
            bw.write(newCust.toString());
            bw.newLine();
        }

        // ... after writing to file ...
        customerList.add(newCust); // Keep the local list updated
        System.out.println("\n[SUCCESS] Registration Successful! ID: " + newId);

        System.out.println("\n[SUCCESS] Customer Registration Successful! You may now login.");
    }

}