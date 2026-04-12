package Other;

import Main.FileStorage;

import java.io.IOException;
import java.util.Scanner;

public class Technician extends Staff implements Menu, FileStorage {

    public Technician() throws IOException {
    }

    public Technician(String id, String username, String password, String role, double salary) throws IOException {
        super(id, username, password, role, salary);
    }

    public void dashboard() throws IOException {
        staffList.stream();
    }

    @Override
    public void displayMenu() throws IOException {
        Scanner sc = new Scanner(System.in);
        int choice;

        do{
            System.out.println("Technician Menu");
            System.out.println("1. Dashboard");
            System.out.println("2. Assign Ticket To Other Technician");
            System.out.println("3. Exit");

            System.out.println("Select an option");
            choice = sc.nextInt();

            switch (choice) {
                case 1 ->System.out.println("Technician Dashboard");
                case 2 ->System.out.println("Assign Ticket To Other Technician");
                default ->System.out.println("Invalid choice");
            }
        }while(choice != 3);
    }


}
