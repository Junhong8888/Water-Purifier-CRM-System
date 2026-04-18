package CRMv_final;

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
        String[] slots = TimeSlotBooking.AVAILABLE_TIME_SLOTS;

        System.out.println("\n  --- Availability for " + localDate.format(TimeSlotBooking.DATE_FMT) + " ---");

        for (int i = 0; i < slots.length; i++) {
            String currentSlotLabel = slots[i];
            boolean isTaken = false;

            // Check if any existing booking matches THIS date and THIS time string
            for (TimeSlotBooking b : existing) {
                if (b.getDateTime() == null) continue;

                // 1. Check if the DATE matches
                boolean dateMatches = b.getDateTime().toLocalDate().equals(localDate);

                // 2. Check if the TIME matches by comparing the formatted strings
                // This ensures that "10:00:00" matches "10:00:00" regardless of the Date part
                String bookingTimeOnly = b.getDateTime().toLocalTime().toString(); // e.g., "10:00"

                // Check if our target slot (e.g. "10:00:00") contains the booking's time
                if (dateMatches && currentSlotLabel.contains(bookingTimeOnly)) {
                    isTaken = true;
                    break;
                }
            }


            if (isTaken) {
                // Display with Strike-through
                System.out.printf("    %s[%d] %s (BOOKED)%s%n",
                        TimeSlotBooking.STRIKE, i + 1, currentSlotLabel, TimeSlotBooking.RESET);
            } else {
                // Display normally
                System.out.printf("    [%d] %s%n", i + 1, currentSlotLabel);
            }
        }
        System.out.println();
    }
}

