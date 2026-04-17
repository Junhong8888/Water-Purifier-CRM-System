package CRMver2;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class TimeSlotBookingService {

    private static final String[] AVAILABLE_TIME_SLOTS =
            {"10:00:00", "12:00:00", "14:00:00", "16:00:00", "18:00:00"};

    private TimeSlotBookingRepository timeSlotBookingRepository;


    public TimeSlotBookingService() {
        this.timeSlotBookingRepository = new TimeSlotBookingRepository();
    }

    public boolean isSlotTaken(LocalDateTime proposed) {
        ArrayList<TimeSlotBooking> existing = timeSlotBookingRepository.loadTimeSlotBookingToList();
        for (TimeSlotBooking b : existing) {
            if (b.getDateTime() != null && b.getDateTime().equals(proposed)) {
                return true;
            }
        }
        return false;
    }

    public void checkTimeSlot(LocalDate localDate) {
        ArrayList<TimeSlotBooking> existing = timeSlotBookingRepository.loadTimeSlotBookingToList();
        boolean booked = false;
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

        for (TimeSlotBooking b : existing) {
            System.out.println(b.getDateTime());
            if (b.getDateTime().toLocalDate().equals(localDate)) {
                booked = true;
            }
        }

        System.out.println("\n  Available Time Slots:");
        for (int i = 0; i < AVAILABLE_TIME_SLOTS.length; i++) {
            if(booked) {
                System.out.printf("%s    [%d] %s %s%n",TimeSlotBooking.STRIKE, i + 1, AVAILABLE_TIME_SLOTS[i],TimeSlotBooking.RESET);
            } else{
                System.out.printf("    [%d] %s%n", i + 1, AVAILABLE_TIME_SLOTS[i]);
            }
        }
    }
}
