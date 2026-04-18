package CRMver2;

import java.io.*;
import java.util.ArrayList;

public class CustomerRepository implements FileStorage{
    ArrayList<Customer> customerList = new  ArrayList<>();

    public ArrayList<Customer> loadCustomersToList() throws IOException {
        customerList.clear();
        File file = new File("customer.txt"); // Java looks in user.dir by default

        if (!file.exists()) {
            System.out.println("File not found, creating new list.");
            return customerList;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue; // Skip blank lines

                String[] data = line.split(",");
                if (data.length >= 7) { // Adjust based on your actual comma count
                    // Use data[0] for ID, data[1] for username, etc.
                    Customer c = new Customer(
                            data[0], data[1], data[2], data[3], data[4],
                            new Address(data[5]),
                            new WaterPurifier(data[6], data[7])
                    );
                    customerList.add(c);
                }
            }
        }
        return customerList;
    }

    @Override
    public void writeFile(String data) throws IOException {
        String currentDirectory = System.getProperty("user.dir");
        File file = new File(currentDirectory + File.separator + "customer.txt");

        if (!file.exists()) {
            file.createNewFile();
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
            bw.write(data);
            bw.newLine();
        }
    }
}
