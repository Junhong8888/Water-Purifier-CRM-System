package CRMver2;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Staff extends User{
    private String role;
    private double salary;

    private static int counter = 1;
    public static ArrayList<Staff> staffList = new ArrayList<>();

    static {
        try {
            // Load once at startup
            staffList = new StaffRepository().loadStaffToList();
            initCounter();
        } catch (IOException e) {
            System.err.println("CRITICAL: Failed to load staff list.");
        }
    }


    // ---------------------------------------------------------------
    // CONSTRUCTORS
    // ---------------------------------------------------------------

    public Staff() throws IOException {

    }

    public Staff(String id, String username, String password,
                 String role, double salary) throws IOException {
        super(id, username, password);
        this.role = role;
        this.salary = salary;

    }

    // ---------------------------------------------------------------
    // ID MANAGEMENT
    // ---------------------------------------------------------------

    private static void initCounter() {
        int max = 0;
        for (Staff s : staffList) {
            String id = s.getId();
            if (id != null && id.length() > 1) {
                try {
                    // Extract numeric part (e.g., "m5" -> 5)
                    int num = Integer.parseInt(id.substring(1));
                    if (num > max) max = num;
                } catch (NumberFormatException ignored) {}
            }
        }
        counter = max + 1;
    }

    public void assignID() throws IOException {
        StaffRepository repo = new StaffRepository();
        ArrayList<Staff> currentList = repo.loadStaffToList();

        // 1. Determine the prefix based on role
        String prefix = "S";
        if (this.role.equalsIgnoreCase("Technician")) prefix = "T";
        else if (this.role.equalsIgnoreCase("Manager")) prefix = "M";

        // 2. Find the highest existing number for THIS prefix
        int maxNumber = 0;
        for (Staff s : currentList) {
            if (s.getId().toUpperCase().startsWith(prefix)) {
                try {
                    // Extract number from "T5" -> 5
                    int currentNum = Integer.parseInt(s.getId().substring(1));
                    if (currentNum > maxNumber) {
                        maxNumber = currentNum;
                    }
                } catch (Exception e) {
                    // Skip lines with bad ID formats
                }
            }
        }

        // 3. Set the new ID (Max + 1)
        this.setId(prefix + (maxNumber + 1));
    }


    public String getID() {
        return getId() + counter;
    }

    // ---------------------------------------------------------------
    // GETTERS & SETTERS
    // ---------------------------------------------------------------

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }



    // ---------------------------------------------------------------
    // AUTHENTICATION & VERIFICATION
    // ---------------------------------------------------------------

    public boolean credentials(String id, String username, String password) {
        for (Staff staff : staffList) {
            if (staff.getId().equals(id)
                    && staff.getUsername().equals(username)
                    && staff.getPassword().equals(password)) {
                return true;
            }
        }
        System.out.println("  [ERROR] Invalid credentials.");
        return false;
    }


    @Override
    public String toString() {
        return getId() + "," + getUsername() + "," + getPassword() + "," + role + "," + salary;
    }
}