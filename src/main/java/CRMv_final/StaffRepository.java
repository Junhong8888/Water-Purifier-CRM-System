package CRMv_final;

import java.io.*;
import java.util.ArrayList;

public class StaffRepository implements FileStorage{

    public StaffRepository() {

    }

    /**
     * Loads staff data from the text file.
     * Includes fixes for missing files, malformed lines, and number parsing.
     */
    public ArrayList<Staff> loadStaffToList() throws IOException {
        ArrayList<Staff> staffList = new ArrayList<>();
        String path = System.getProperty("user.dir") + File.separator + "staff.txt";
        File file = new File(path);

        if (!file.exists()) {
            return staffList;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNumber = 0;

            while ((line = br.readLine()) != null) {
                lineNumber++;
                String[] parts = line.split(",", -1);

                if (parts.length < 5) {
                    System.out.printf("  [Warning] Skipping malformed line %d%n", lineNumber);
                    continue;
                }

                // Standardize null/blank values
                for (int i = 0; i < parts.length; i++) {
                    if (parts[i] == null || parts[i].equalsIgnoreCase("null") || parts[i].isBlank()) {
                        parts[i] = null;
                    }
                }

                double salary = 0.0;
                try {
                    if (parts[4] != null) {
                        salary = Double.parseDouble(parts[4]);
                    }
                } catch (NumberFormatException e) {
                    // Log warning but keep salary at 0.0
                }

                try {
                    staffList.add(new Staff(parts[0], parts[1], parts[2], parts[3], salary));
                } catch (Exception e) {
                    System.err.println("Error creating staff from file: " + e.getMessage());
                }
            }
        }
        return staffList;
    }

    // ---------------------------------------------------------------
    // PERSISTENCE (FILE I/O)
    // ---------------------------------------------------------------

    @Override
    public void writeFile(String data) throws IOException {
        String path = System.getProperty("user.dir");
        File file = new File(path + File.separator + "staff.txt");
        ensureDir(file);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
            bw.write(data);
            bw.newLine();
        }
    }

    public void rewriteStaffFile(ArrayList<Staff> list) throws IOException {
        String path = System.getProperty("user.dir") + File.separator + "staff.txt";
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path, false))) {
            for (Staff s : list) {
                bw.write(s.toString());
                bw.newLine();
            }
        }
    }

    private void ensureDir(File file) {
        if (file.getParentFile() != null && !file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
    }
}
