package CRM;

import CRM.FileStorage;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

public class TimeSlotBooking implements FileStorage {
    private final String[] availableTimeSlot = {"10:00:00","12:00:00","14:00:00","16:00:00","18:00:00"};
    private String ticketID;
    private LocalDateTime dateTime;

    public TimeSlotBooking() {

    }

    public TimeSlotBooking(String ticketID) {
        this.ticketID = ticketID;
    }

    public  TimeSlotBooking(String ticketID,LocalDateTime dateTime) {
        this.ticketID = ticketID;
        this.dateTime = dateTime;
    }

    public String getTicketID() {
        return ticketID;
    }
    public void setTicketID(String ticketID) {
        this.ticketID = ticketID;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public void selectTimeSlotBooking(String id) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        LocalDateTime dateTime = null;
        Scanner sc = new Scanner(System.in);
        boolean flag = true;

        do {
            System.out.println("Enter your desired date (DD-MM-YYYY):");
            String date = sc.nextLine();

            if (LocalDate.parse(date, DateTimeFormatter.ofPattern("dd-MM-yyyy")).isBefore(LocalDate.now())) {
                System.out.println("Invalid date");
            } else {
                System.out.println("Select TimeSlot Booking");
                for (int i = 0; i < availableTimeSlot.length; i++) {
                    System.out.println((i + 1) + ". " + availableTimeSlot[i]);
                }
                int time = sc.nextInt();
                dateTime = LocalDateTime.parse((date + " " + availableTimeSlot[time - 1]), DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
                setTicketID(id);
                setDateTime(dateTime);
                //System.out.println(getDateTime());
                flag = false;
            }
        } while (flag == true);
        System.out.println("Booking Successful");
    }

    public ArrayList<TimeSlotBooking> loadTimeSlotBookingToList() {
        ArrayList<TimeSlotBooking> timeSlotBookings = new ArrayList<>();

        String path = System.getProperty("user.dir");
        File file = new File(path + File.separator + "BookingTimeSlot.txt");

        try(BufferedReader br = new BufferedReader(new FileReader(file))) {

            String line;
            while ((line = br.readLine()) != null) {
                String[] strings = line.split(",");
                timeSlotBookings.add(new TimeSlotBooking(ticketID,dateTime));
            }
        }  catch (IOException e) {
            throw new RuntimeException(e);
        }
        return timeSlotBookings;
    }

    @Override
    public void writeFile(String data) throws IOException {
        String path = System.getProperty("user.dir");
        File file = new File(path + File.separator + "BookingTimeSlot.txt");

        if (!file.exists()) {
            boolean newFile = file.createNewFile();
            System.out.println(newFile);
        }

        BufferedWriter bw = new BufferedWriter(new FileWriter(file,true));

        bw.write(data,0,data.length());
        bw.newLine();
        bw.close();
    }

    @Override
    public String toString() {
        return ticketID + "," +  dateTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
    }
}
