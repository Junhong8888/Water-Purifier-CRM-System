package CRMver2;

import java.io.IOException;

public interface RegistrationService<T extends User>{
    void register(T user) throws IOException;
}
