package Other;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class StaffService {

    public StaffService() {

    }

    //Create a ticket object from the file then store into a list
    public ArrayList<Staff> loadStaffToList() throws IOException {
        //Read data from the text file,then store staff info to a ArrayList
        ArrayList<Staff> staffList = new ArrayList<>();
        String currentDirectory = System.getProperty("user.dir");
        File file = new File(currentDirectory + File.separator + "staff.txt");

        try(BufferedReader br = new BufferedReader(new FileReader(file))) {

            String line;
            while ((line = br.readLine()) != null) {
                String[] strings = line.split(",");
                staffList.add(new Staff(strings[0],strings[1], strings[2], strings[3],Double.parseDouble(strings[4])));
            }
        }
        //br.close();
        return staffList;
    }
}
