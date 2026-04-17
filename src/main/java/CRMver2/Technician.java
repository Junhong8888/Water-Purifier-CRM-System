package CRMver2;

import java.io.IOException;

public class Technician extends Staff{
    private int rating;

    public Technician() throws IOException {

    }

    public Technician(String id, String username, String password, String role, double salary,int rating) throws IOException {
        super(id, username, password, role, salary);
    }


}
