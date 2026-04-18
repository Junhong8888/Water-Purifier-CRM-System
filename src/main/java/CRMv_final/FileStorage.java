package CRMv_final;

import java.io.IOException;

public interface FileStorage {
    public abstract void writeFile(String data) throws IOException;
}
