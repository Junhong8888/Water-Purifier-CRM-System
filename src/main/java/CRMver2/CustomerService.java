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

        // BUG FIX 2: Assign result back to customerList so duplicate check works
        // Before: customerRepo.loadCustomersToList() — result was loaded into repo
        //         but customerList here stayed EMPTY, so duplicate check always passed
        // After:  customerList = customerRepo.loadCustomersToList() — now populated
        customerList = customerRepo.loadCustomersToList();

        System.out.println("\n=== Customer Registration ===");

        // 1. Username — letters only, unique
        String username;
        while (true) {
            System.out.print("Enter Username (Letters only): ");
            username = sc.nextLine().trim();
            if (username.isEmpty() || !username.matches("[a-zA-Z]+")) {
                System.out.println("[ERROR] Username must contain ONLY letters.");
                continue;
            }
            boolean taken = false;
            for (Customer c : customerList) {
                if (c.getUsername().equalsIgnoreCase(username) ) { taken = true; break; }
            }
            if (taken) System.out.println("[ERROR] Username already exists!");
            else break;
        }

        // 2. Password — min 6, no commas
        String password;
        while (true) {
            System.out.print("Enter Password (min 6 chars): ");
            password = sc.nextLine().trim();
            if (password.length() < 6 || password.contains(",")) {
                System.out.println("[ERROR] Password must be at least 6 characters and contain no commas.");
            } else break;
        }

        // 3. Email
        String email;
        while (true) {
            System.out.print("Enter Email Address: ");
            email = sc.nextLine().trim();
            if (email.isEmpty() || email.contains(",") || !email.contains("@") || !email.contains(".")) {
                System.out.println("[ERROR] Please enter a valid Email Address (e.g., user@mail.com).");
            } else break;
        }

        // 4. Contact — digits only
        String contact;
        while (true) {
            System.out.print("Enter Contact Number (Numbers only): ");
            contact = sc.nextLine().trim();
            if (contact.isEmpty() || !contact.matches("\\d+")) {
                System.out.println("[ERROR] Contact must contain ONLY numbers.");
            } else break;
        }

        // 5. Address — no commas
        String address;
        while (true) {
            System.out.print("Enter Home Address: ");
            address = sc.nextLine().trim();
            if (address.isEmpty() || address.contains(",")) {
                System.out.println("[ERROR] Address cannot be empty or contain commas.");
            } else break;
        }

        // 6. Purifier model
        String model;
        while (true) {
            System.out.print("Enter Model (Alkaline / RO System / Mineral): ");
            model = sc.nextLine().trim();
            if (model.equalsIgnoreCase("Alkaline") ||
                    model.equalsIgnoreCase("RO System") ||
                    model.equalsIgnoreCase("Mineral")) break;
            System.out.println("[ERROR] Invalid Model! Please enter Alkaline, RO System, or Mineral.");
        }

        String newId   = "C" + (customerList.size() + 1);
        Customer newCust = new Customer(newId, username, password, email, contact,
                new Address(address), new WaterPurifier(model, "01-01-2026"));

        // Write to file using portable path
        String path = System.getProperty("user.dir") + File.separator + "customer.txt";
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path, true))) {
            bw.write(newCust.toString());
            bw.newLine();
        }

        System.out.println("\n[SUCCESS] Customer Registration Successful! You may now login.");
    }

}