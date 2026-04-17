package CRMver2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class TechnicianService implements RegistrationService<Technician> {
    Technician technician;

    public TechnicianService() throws IOException {
        this.technician = new Technician();
    }


    @Override
    public void register(Technician user) throws IOException {
        Scanner sc = new Scanner(System.in);
        StaffRepository staffRepository = new StaffRepository();
        ArrayList<Staff> currentStaff = staffRepository.loadStaffToList();

        System.out.println("\n--- Technician Registration ---");

        String username;
        while (true) {
            System.out.print("Enter Username: ");
            username = sc.nextLine().trim();

            if (username.isEmpty() || username.contains(",")) {
                System.out.println("[ERROR] Username cannot be empty or contain commas.");
                continue;
            }

            boolean isTaken = false;
            for (Staff s : currentStaff) {
                if (s.getUsername().equalsIgnoreCase(username) && s.getRole().equalsIgnoreCase("Technician")) {
                    isTaken = true;
                    break;
                }
            }
            if (isTaken) System.out.println("[ERROR] A Technician with this username already exists.");
            else break;
        }

        String password;
        while (true) {
            System.out.print("Enter Password (min 6 chars): ");
            password = sc.nextLine().trim();
            if (password.length() >= 6 && !password.contains(",")) break;
            System.out.println("[ERROR] Password must be at least 6 characters and contain no commas.");
        }

        double salary = 0;
        while (true) {
            try {
                System.out.print("Enter Monthly Salary: ");
                salary = Double.parseDouble(sc.nextLine().trim());
                if (salary > 0) break;
                else System.out.println("[ERROR] Salary must be greater than 0.");
            } catch (Exception e) {
                System.out.println("[ERROR] Please enter a valid number for salary.");
            }
        }

        user.setUsername(username);
        user.setPassword(password);
        user.setSalary(salary);
        user.setRole("Manager");
        user.assignID();

        new StaffRepository().writeFile(user.toString());

        System.out.println("\n[SUCCESS] Technician registered successfully! You may now login.");
    }
}
