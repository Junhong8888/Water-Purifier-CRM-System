package CRMver2;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class TimeSlotBookingMenu {
    private TimeSlotBookingService timeSlotBookingService;
    private TimeSlotBookingRepository timeSlotBookingRepository;
    private TimeSlotBooking timeSlotBooking;


    public TimeSlotBookingMenu() {
        this.timeSlotBookingService = new TimeSlotBookingService();
        this.timeSlotBookingRepository = new TimeSlotBookingRepository();
        this.timeSlotBooking = new TimeSlotBooking();
    }

    public void selectTimeSlotBooking(String id) throws IOException {
        Scanner sc = new Scanner(System.in);
        boolean booked = false;

        do {
            // --- Date input ---
            System.out.print("  Enter your desired date (DD-MM-YYYY): ");
            String dateInput = sc.nextLine().trim();

            LocalDate chosenDate;
            try {
                chosenDate = LocalDate.parse(dateInput, TimeSlotBooking.DATE_FMT);
            } catch (DateTimeParseException e) {
                // FIX 6: bad format re-prompts instead of crashing
                System.out.println("  Invalid date format — please use DD-MM-YYYY.");
                continue;
            }

            if (chosenDate.isBefore(LocalDate.now())) {
                System.out.println("  Date cannot be in the past. Please enter a future date.");
                continue;
            }
            System.out.println(chosenDate);
            // --- Time slot selection ---
            System.out.println("\n  Available Time Slots:");
            String[] timeSlot = TimeSlotBooking.AVAILABLE_TIME_SLOTS;
            for (int i = 0; i < timeSlot.length; i++) {
                System.out.printf("    [%d] %s%n", i + 1, timeSlot[i]);
            }
            System.out.print("  Select a time slot (1-" + timeSlot.length + "): ");

            int slotIndex;
            try {
                // FIX 4: catch non-integer input
                slotIndex = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("  Invalid input — please enter a number.");
                continue;
            }

            // FIX 5: bounds check before array access
            if (slotIndex < 1 || slotIndex > timeSlot.length) {
                System.out.printf("  Invalid choice — please select between 1 and %d.%n",
                        timeSlot.length);
                continue;
            }

            LocalDateTime chosen = LocalDateTime.parse(
                    dateInput + " " + timeSlot[slotIndex - 1], TimeSlotBooking.DATETIME_FMT);

            // FIX 10: check if this slot is already taken
            if (timeSlotBookingService.isSlotTaken(chosen)) {
                System.out.println("  That time slot is already booked. Please choose another.");
                continue;
            }

            // All checks passed — commit the booking
            timeSlotBooking.setTicketID(id);
            timeSlotBooking.setDateTime(chosen);
            booked = true;
            String dateTime = chosen.format(timeSlotBooking.DATETIME_FMT);

            timeSlotBookingRepository.writeFile(timeSlotBooking.toString());

            System.out.println("  ✓ Booking confirmed: " + dateTime);

        } while (!booked);
    }
}
