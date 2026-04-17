package CRMver2;

import java.io.IOException;

public interface DashBoardService<T extends  User>{
    public void dashBoard(T user) throws IOException;
}
