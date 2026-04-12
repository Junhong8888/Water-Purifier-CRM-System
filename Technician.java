package Other;

import java.io.IOException;

public class Technician extends Staff{

    public Technician() throws IOException {
    }

    public Technician(String id, String username, String password, String role, double salary) throws IOException {
        super(id, username, password, role, salary);
    }


}
