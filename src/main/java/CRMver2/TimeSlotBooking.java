package CRMver2;


import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class TimeSlotBooking{
    public static final String STRIKE = "\u001B[9m";
    public static final String RESET = "\u001B[0m";

    public static final String[] AVAILABLE_TIME_SLOTS =
            {"10:00:00", "12:00:00", "14:00:00", "16:00:00", "18:00:00"};

    public static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("dd-MM-yyyy");
    public static final DateTimeFormatter DATETIME_FMT =
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    public static final String FILE_PATH =
            System.getProperty("user.dir") + File.separator + "BookingTimeSlot.txt";

    private String ticketID;
    private LocalDateTime dateTime;

    // ---------------------------------------------------------------
    // CONSTRUCTORS
    // ---------------------------------------------------------------

    public TimeSlotBooking() {}

    public TimeSlotBooking(String ticketID) {
        this.ticketID = ticketID;
    }

    public TimeSlotBooking(String ticketID, LocalDateTime dateTime) {
        this.ticketID = ticketID;
        this.dateTime = dateTime;
    }

    // ---------------------------------------------------------------
    // GETTERS & SETTERS
    // ---------------------------------------------------------------

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

    @Override
    public String toString() {
        String formattedDate = (dateTime != null)
                ? dateTime.format(DATETIME_FMT)
                : "null";
        return ticketID + "," + formattedDate;
    }
}