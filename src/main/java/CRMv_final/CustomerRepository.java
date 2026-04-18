package CRMv_final;

import java.io.*;
import java.util.ArrayList;

public class CustomerRepository implements FileStorage {

    ArrayList<Customer> customerList;

    // BUG FIX 1: Added constructor to initialize customerList
    // Without this, customerList = null and customerList.clear() crashes immediately
    public CustomerRepository() {
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
                if (line.trim().isEmpty()) continue;
                String[] data = line.split(",", -1);
                if (data.length >= 7) {
                    // BUG FIX 3: Actually load address and purifier data from file
                    // Before: new Address() and new WaterPurifier() = blank objects, data lost
                    // After:  new Address(data[5]) and new WaterPurifier(data[6], data[7])
                    String address     = data.length > 5 ? data[5] : "";
                    String model       = data.length > 6 ? data[6] : "";
                    String installDate = data.length > 7 ? data[7] : "";
                    customerList.add(new Customer(
                            data[0], data[1], data[2], data[3], data[4],
                            new Address(address),
                            new WaterPurifier(model, installDate)
                    ));
                }
            }
        }
        return customerList;
    }

    @Override
    public void writeFile(String data) throws IOException {
        String currentDirectory = System.getProperty("user.dir");
        File file = new File(currentDirectory + File.separator + "customer.txt");
        if (!file.exists()) file.createNewFile();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
            bw.write(data);
            bw.newLine();
        }
    }
}
