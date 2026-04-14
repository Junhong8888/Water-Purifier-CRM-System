package CRM;

import java.io.*;
import java.util.List;

public class Ticket implements Comparable<Ticket>, FileStorage {

    private String id;
    private String customerID;
    private String ticketStatus;
    private String priorityLevel;
    private String technician;
    private String date;
    private String resolveTime;
    private String description;
    private String content;
    private String response;
    private TimeSlotBooking timeSlotBooking;

    // FIX 2: count is still used for in-session ordering,
    // but the real persisted ID comes from the file (see loadTicketToList)
    static int count = 1;

    // ---------------------------------------------------------------
    // CONSTRUCTORS
    // ---------------------------------------------------------------

    public Ticket() {
        this.id = assignID();
    }

    public Ticket(String customerID, String ticketStatus, String priorityLevel,
                  String technician, String date) {
        this.id = assignID();
        this.customerID = customerID;
        this.ticketStatus = ticketStatus;
        this.priorityLevel = priorityLevel;
        this.technician = technician;
        this.date = date;
    }

    public Ticket(String customerID, String ticketStatus, String priorityLevel,
                  String technician, String date, String resolveTime,
                  String description, String content, String response) {
        this.id = assignID();
        this.customerID = customerID;
        this.ticketStatus = ticketStatus;
        this.priorityLevel = priorityLevel;
        this.technician = technician;
        this.date = date;
        this.resolveTime = resolveTime;
        this.description = description;
        this.content = content;
        this.response = response;
    }

    // FIX 2: package-private constructor used by loadTicketToList()
    // to restore a ticket with its SAVED id (not a new auto-generated one)
    Ticket(String id, String customerID, String ticketStatus, String priorityLevel,
           String technician, String date, String resolveTime,
           String description, String content, String response) {
        this.id = id;                   // restore persisted ID directly
        this.customerID = customerID;
        this.ticketStatus = ticketStatus;
        this.priorityLevel = priorityLevel;
        this.technician = technician;
        this.date = date;
        this.resolveTime = resolveTime;
        this.description = description;
        this.content = content;
        this.response = response;
    }

    // ---------------------------------------------------------------
    // ID ASSIGNMENT
    // ---------------------------------------------------------------

    public String assignID() {
        id = "t" + count;
        count++;
        return id;
    }

    // ---------------------------------------------------------------
    // GETTERS & SETTERS
    // ---------------------------------------------------------------

    public String getId() {              // FIX 1: was missing entirely
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerID() { return customerID; }
    public void setCustomerID(String customerID) { this.customerID = customerID; }

    public String getTicketStatus() { return ticketStatus; }
    public void setTicketStatus(String ticketStatus) { this.ticketStatus = ticketStatus; }

    public String getPriorityLevel() { return priorityLevel; }
    public void setPriorityLevel(String priorityLevel) { this.priorityLevel = priorityLevel; }

    public String getTechnician() { return technician; }
    public void setTechnician(String technician) { this.technician = technician; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getResolveTime() { return resolveTime; }
    public void setResolveTime(String resolveTime) { this.resolveTime = resolveTime; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getResponse() { return response; }
    public void setResponse(String response) { this.response = response; }

    public TimeSlotBooking getTimeSlotBooking() { return timeSlotBooking; }
    public void setTimeSlotBooking(TimeSlotBooking timeSlotBooking) {
        this.timeSlotBooking = timeSlotBooking;
    }

    // ---------------------------------------------------------------
    // MAKE BOOKING
    // FIX 7: null-check description before calling equalsIgnoreCase
    // ---------------------------------------------------------------
    public void makeBooking() throws IOException {
        String descr = this.getDescription();
        if (descr != null && (
                descr.equalsIgnoreCase("Repair Water Purifier") ||
                        descr.equalsIgnoreCase("Water Purifier Maintenance"))) {

            TimeSlotBooking booking = new TimeSlotBooking();
            booking.selectTimeSlotBooking(id);
            setTimeSlotBooking(booking);
            timeSlotBooking.writeFile(id + "," + timeSlotBooking.getDateTime());
        }
    }

    // ---------------------------------------------------------------
    // toString
    // FIX 1: id is now the FIRST field so it round-trips through the file
    // FIX 6: only append timeSlotBooking section when it exists,
    //        avoiding a trailing comma that creates a phantom empty field
    // ---------------------------------------------------------------
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(id).append(",")
                .append(customerID).append(",")
                .append(ticketStatus).append(",")
                .append(priorityLevel).append(",")
                .append(technician).append(",")
                .append(date).append(",")
                .append(resolveTime).append(",")
                .append(description).append(",")
                .append(content).append(",")
                .append(response);

        // FIX 6: only add the comma+booking when a booking actually exists
        if (timeSlotBooking != null) {
            sb.append(",").append(timeSlotBooking.toString());
        }

        return sb.toString();
    }

    // ---------------------------------------------------------------
    // compareTo
    // FIX 3: null-safe comparisons to prevent NullPointerException
    // ---------------------------------------------------------------
    @Override
    public int compareTo(Ticket o) {
        int i = compareNullSafe(this.getPriorityLevel(), o.getPriorityLevel());
        i = i == 0 ? compareNullSafe(this.getDate(),         o.getDate())         : i;
        i = i == 0 ? compareNullSafe(this.getTicketStatus(), o.getTicketStatus()) : i;
        i = i == 0 ? compareNullSafe(this.getTechnician(),   o.getTechnician())   : i;
        i = i == 0 ? compareNullSafe(this.getCustomerID(),   o.getCustomerID())   : i;
        return i;
    }

    // Null-safe comparator: nulls sort last
    private int compareNullSafe(String a, String b) {
        if (a == null && b == null) return 0;
        if (a == null) return 1;   // nulls go to end
        if (b == null) return -1;
        return a.compareTo(b);
    }

    // ---------------------------------------------------------------
    // FILE I/O
    // FIX 4: try-with-resources so writer always closes
    // FIX 5: removed System.out.println(newFile) debug noise
    // ---------------------------------------------------------------
    @Override
    public void writeFile(String data) throws IOException {
        File file = new File("C:\\crmSystem\\ticket.txt");

        if (!file.exists()) {
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs(); // create C:\crmSystem\ if it doesn't exist
            }
            file.createNewFile(); // FIX 5: no println
        }

        // FIX 4: try-with-resources guarantees bw.close() even on exception
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
            bw.write(data);
            bw.newLine();
        }
    }

    // FIX 8: throws IOException instead of silently swallowing it
    public void writeAllTickets(List<String> lines) throws IOException {
        File file = new File("C:\\crmSystem\\ticket.txt");
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            for (String line : lines) {
                bw.write(line);
                bw.newLine();
            }
        }
        // IOException now propagates to the caller — no silent failure
    }

    // ---------------------------------------------------------------
// INIT COUNT
// Reads ticket.txt and sets the static counter to (highest ID + 1)
// so new tickets never collide with IDs already saved in the file.
// Call once at program startup before any Ticket objects are created.
// ---------------------------------------------------------------
    public static void initCount() {
        File file = new File("C:\\crmSystem\\ticket.txt");

        // If file doesn't exist yet, start from 1
        if (!file.exists()) {
            count = 1;
            return;
        }

        int max = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNumber = 0;

            while ((line = br.readLine()) != null) {
                lineNumber++;

                // Skip blank lines
                if (line.isBlank()) continue;

                String[] parts = line.split(",", -1);

                // id is the first field — must start with 't'
                if (parts.length == 0 || parts[0].isBlank()) {
                    System.out.printf("  Warning: no ID found on line %d — skipping.%n", lineNumber);
                    continue;
                }

                String id = parts[0].trim();

                if (!id.startsWith("t")) {
                    System.out.printf("  Warning: unexpected ID format \"%s\" on line %d — skipping.%n",
                            id, lineNumber);
                    continue;
                }

                // Strip the 't' prefix and parse the number
                try {
                    int num = Integer.parseInt(id.substring(1));
                    if (num > max) max = num;
                } catch (NumberFormatException e) {
                    System.out.printf("  Warning: non-numeric ID suffix \"%s\" on line %d — skipping.%n",
                            id, lineNumber);
                }
            }

        } catch (IOException e) {
            System.out.println("  Warning: could not read ticket.txt to init ID counter — starting from 1.");
            count = 1;
            return;
        }

        // Set counter to one above the highest found ID
        count = max + 1;
        System.out.println("  ✓ Ticket ID counter initialised — next ID will be t" + count);
    }
}