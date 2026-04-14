package CRM;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;

public class TimeSlotBooking implements FileStorage {

    private static final String[] AVAILABLE_TIME_SLOTS =
            {"10:00:00", "12:00:00", "14:00:00", "16:00:00", "18:00:00"};

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final DateTimeFormatter DATETIME_FMT =
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    private static final String FILE_PATH =
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

    public String getTicketID() { return ticketID; }
    public void setTicketID(String ticketID) { this.ticketID = ticketID; }

    public LocalDateTime getDateTime() { return dateTime; }
    public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }

    // ---------------------------------------------------------------
    // SELECT TIME SLOT BOOKING
    // FIX 4: try-catch around time slot integer input
    // FIX 5: bounds check on slot selection
    // FIX 6: try-catch around date parse — re-prompts on bad format
    // FIX 7: sc.nextLine() after nextInt() to clear buffer
    // FIX 10: checks existing bookings for conflicts
    // ---------------------------------------------------------------
    public void selectTimeSlotBooking(String id) {
        Scanner sc = new Scanner(System.in);
        boolean booked = false;

        do {
            // --- Date input ---
            System.out.print("  Enter your desired date (DD-MM-YYYY): ");
            String dateInput = sc.nextLine().trim();

            LocalDate chosenDate;
            try {
                chosenDate = LocalDate.parse(dateInput, DATE_FMT);
            } catch (DateTimeParseException e) {
                // FIX 6: bad format re-prompts instead of crashing
                System.out.println("  Invalid date format — please use DD-MM-YYYY.");
                continue;
            }

            if (chosenDate.isBefore(LocalDate.now())) {
                System.out.println("  Date cannot be in the past. Please enter a future date.");
                continue;
            }

            // --- Time slot selection ---
            System.out.println("\n  Available Time Slots:");
            for (int i = 0; i < AVAILABLE_TIME_SLOTS.length; i++) {
                System.out.printf("    [%d] %s%n", i + 1, AVAILABLE_TIME_SLOTS[i]);
            }
            System.out.print("  Select a time slot (1-" + AVAILABLE_TIME_SLOTS.length + "): ");

            int slotIndex;
            try {
                // FIX 4: catch non-integer input
                slotIndex = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("  Invalid input — please enter a number.");
                continue;
            }

            // FIX 5: bounds check before array access
            if (slotIndex < 1 || slotIndex > AVAILABLE_TIME_SLOTS.length) {
                System.out.printf("  Invalid choice — please select between 1 and %d.%n",
                        AVAILABLE_TIME_SLOTS.length);
                continue;
            }

            LocalDateTime chosen = LocalDateTime.parse(
                    dateInput + " " + AVAILABLE_TIME_SLOTS[slotIndex - 1], DATETIME_FMT);

            // FIX 10: check if this slot is already taken
            if (isSlotTaken(chosen)) {
                System.out.println("  That time slot is already booked. Please choose another.");
                continue;
            }

            // All checks passed — commit the booking
            setTicketID(id);
            setDateTime(chosen);
            booked = true;
            System.out.println("  ✓ Booking confirmed: " + chosen.format(DATETIME_FMT));

        } while (!booked);
    }

    // FIX 10: returns true if any existing booking has the same dateTime
    private boolean isSlotTaken(LocalDateTime proposed) {
        ArrayList<TimeSlotBooking> existing = loadTimeSlotBookingToList();
        for (TimeSlotBooking b : existing) {
            if (b.getDateTime() != null && b.getDateTime().equals(proposed)) {
                return true;
            }
        }
        return false;
    }

    // ---------------------------------------------------------------
    // LOAD TIME SLOT BOOKINGS FROM FILE
    // FIX 1: use parsed values from file, not the object's own fields
    // FIX 2: parse dateTime string back to LocalDateTime properly
    // FIX 3: return empty list if file doesn't exist
    // ---------------------------------------------------------------
    public ArrayList<TimeSlotBooking> loadTimeSlotBookingToList() {
        ArrayList<TimeSlotBooking> bookings = new ArrayList<>();
        File file = new File(FILE_PATH);

        // FIX 3: gracefully handle missing file
        if (!file.exists()) {
            return bookings;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNumber = 0;

            while ((line = br.readLine()) != null) {
                lineNumber++;
                String[] parts = line.split(",", -1);

                if (parts.length < 2) {
                    System.out.printf("  Warning: skipping malformed booking line %d: %s%n",
                            lineNumber, line);
                    continue;
                }

                String parsedTicketID = parts[0].trim();

                // FIX 2: actually parse the dateTime string from the file
                LocalDateTime parsedDateTime = null;
                try {
                    parsedDateTime = LocalDateTime.parse(parts[1].trim(), DATETIME_FMT);
                } catch (DateTimeParseException e) {
                    System.out.printf("  Warning: invalid dateTime on line %d (\"%s\")%n",
                            lineNumber, parts[1]);
                }

                // FIX 1: use the values read from file, not the object's own fields
                bookings.add(new TimeSlotBooking(parsedTicketID, parsedDateTime));
            }

        } catch (IOException e) {
            System.out.println("  Error reading booking file: " + e.getMessage());
        }

        return bookings;
    }

    // ---------------------------------------------------------------
    // WRITE FILE
    // FIX 8: try-with-resources — no resource leak on exception
    // ---------------------------------------------------------------
    @Override
    public void writeFile(String data) throws IOException {
        File file = new File(FILE_PATH);

        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }

        // FIX 8: try-with-resources guarantees writer is always closed
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
            bw.write(data);
            bw.newLine();
        }
    }

    // ---------------------------------------------------------------
    // toString
    // FIX 9: null-safe — returns placeholder if dateTime not set yet
    // ---------------------------------------------------------------
    @Override
    public String toString() {
        String formattedDate = (dateTime != null)
                ? dateTime.format(DATETIME_FMT)
                : "null";
        return ticketID + "," + formattedDate;
    }
}