package CRMver2;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

public class TimeSlotBookingRepository implements FileStorage{

    public TimeSlotBookingRepository() {

    }

    public ArrayList<TimeSlotBooking> loadTimeSlotBookingToList() {
        ArrayList<TimeSlotBooking> bookings = new ArrayList<>();
        File file = new File(TimeSlotBooking.FILE_PATH);

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
                    parsedDateTime = LocalDateTime.parse(parts[1].trim(), TimeSlotBooking.DATETIME_FMT);
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
        File file = new File(TimeSlotBooking.FILE_PATH);

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
}
