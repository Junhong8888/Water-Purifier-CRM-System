package CRMver2;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Customer extends User {
    private String name;
    private String contactNumber;
    private Address homeAddress;
    private WaterPurifier purifierModel;


    public Customer() {
    }


    public Customer(String id, String username, String password, String name, 
                    String contactNumber, Address homeAddress, WaterPurifier purifierModel) {
        super(id, username, password);
        this.name = name;
        this.contactNumber = contactNumber;
        this.homeAddress = homeAddress;
        this.purifierModel = purifierModel;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
    public Address getHomeAddress() { return homeAddress; }
    public void setHomeAddress(Address homeAddress) { this.homeAddress = homeAddress; }
    public WaterPurifier getPurifierModel() { return purifierModel; }
    public void setPurifierModel(WaterPurifier purifierModel) { this.purifierModel = purifierModel; }


    @Override
    public String toString() {
        return getId() + "," + getUsername() + "," + getPassword() + "," + 
               name + "," + contactNumber + "," + homeAddress + "," + 
               purifierModel.toString();
    }
}