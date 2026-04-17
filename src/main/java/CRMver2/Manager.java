package CRMver2;

import java.io.IOException;

public class Manager extends Staff{
    public Manager() throws IOException {
    }

    public Manager(String id, String username, String password, String role, double salary) throws IOException {
        super(id, username, password, role, salary);
    }
}
