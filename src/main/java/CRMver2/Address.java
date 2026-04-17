package CRMver2;

public class Address {
    private String fullAddress;

    public Address() {

    }

    public Address(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    @Override
    public String toString() {
        // Returns just the address string so it saves to the text file nicely
        return fullAddress;
    }
}
