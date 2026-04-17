package CRMver2;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Customer extends User implements FileStorage {
    private String name;
    private String contactNumber;
    private String homeAddress;
    private String purifierModel;
    private String installationDate;

    public Customer() {
    }


    public Customer(String id, String username, String password, String name, 
                    String contactNumber, String homeAddress, String purifierModel, String installationDate) {
        super(id, username, password);
        this.name = name;
        this.contactNumber = contactNumber;
        this.homeAddress = homeAddress;
        this.purifierModel = purifierModel;
        this.installationDate = installationDate;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
    public String getHomeAddress() { return homeAddress; }
    public void setHomeAddress(String homeAddress) { this.homeAddress = homeAddress; }
    public String getPurifierModel() { return purifierModel; }
    public void setPurifierModel(String purifierModel) { this.purifierModel = purifierModel; }
    public String getInstallationDate() { return installationDate; }
    public void setInstallationDate(String installationDate) { this.installationDate = installationDate; }

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


    @Override
    public String toString() {
        return getId() + "," + getUsername() + "," + getPassword() + "," + 
               name + "," + contactNumber + "," + homeAddress + "," + 
               purifierModel + "," + installationDate;
    }
}