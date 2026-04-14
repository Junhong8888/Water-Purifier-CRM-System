package CRMver2;

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

    // The counter is used to generate NEW IDs.
    // It is initialized via Ticket.initCount() from the text file.
    static int count = 1;

    // ---------------------------------------------------------------
    // CONSTRUCTORS
    // ---------------------------------------------------------------

    // Default constructor for creating a brand new ticket
    public Ticket() {
        this.id = assignID();
    }

    // Constructor for basic ticket submission
    public Ticket(String customerID, String ticketStatus, String priorityLevel,
                  String technician, String date) {
        this.id = assignID();
        this.customerID = customerID;
        this.ticketStatus = ticketStatus;
        this.priorityLevel = priorityLevel;
        this.technician = technician;
        this.date = date;
    }

    // Full constructor for creating a brand new ticket with all details
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

    // RESTORATION CONSTRUCTOR: Used by Service classes to load existing tickets
    // from file without incrementing the global counter or changing the ID.
    public Ticket(String id, String customerID, String ticketStatus, String priorityLevel,
                  String technician, String date, String resolveTime,
                  String description, String content, String response) {
        this.id = id;
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

    private String assignID() {
        String newId = "t" + count;
        count++;
        return newId;
    }

    // ---------------------------------------------------------------
    // GETTERS & SETTERS
    // ---------------------------------------------------------------

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

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
    // OPERATIONAL METHODS
    // ---------------------------------------------------------------

    public void makeBooking() throws IOException {
        if (this.description != null && (
                this.description.equalsIgnoreCase("Repair Water Purifier") ||
                        this.description.equalsIgnoreCase("Water Purifier Maintenance"))) {

            TimeSlotBooking booking = new TimeSlotBooking(this.id);
            booking.selectTimeSlotBooking(this.id);
            setTimeSlotBooking(booking);
            // Save booking reference separately
            timeSlotBooking.writeFile(this.id + "," + timeSlotBooking.getDateTime());
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(id).append(",")
                .append(nvl(customerID)).append(",")
                .append(nvl(ticketStatus)).append(",")
                .append(nvl(priorityLevel)).append(",")
                .append(nvl(technician)).append(",")
                .append(nvl(date)).append(",")
                .append(nvl(resolveTime)).append(",")
                .append(nvl(description)).append(",")
                .append(nvl(content)).append(",")
                .append(nvl(response));

        if (timeSlotBooking != null) {
            sb.append(",").append(timeSlotBooking.toString());
        }

        return sb.toString();
    }

    // Helper to ensure "null" is written to file instead of actual null references
    private String nvl(String val) {
        return (val == null || val.isEmpty()) ? "null" : val;
    }

    // ---------------------------------------------------------------
    // SORTING & COMPARISON
    // ---------------------------------------------------------------

    @Override
    public int compareTo(Ticket o) {
        // Sort order: Priority -> Date -> Status -> Technician -> Customer
        int i = compareNullSafe(this.priorityLevel, o.priorityLevel);
        if (i == 0) i = compareNullSafe(this.date, o.date);
        if (i == 0) i = compareNullSafe(this.ticketStatus, o.ticketStatus);
        if (i == 0) i = compareNullSafe(this.technician, o.technician);
        if (i == 0) i = compareNullSafe(this.customerID, o.customerID);
        return i;
    }

    private int compareNullSafe(String a, String b) {
        if (a == b) return 0;
        if (a == null) return 1;
        if (b == null) return -1;
        return a.compareToIgnoreCase(b);
    }

    // ---------------------------------------------------------------
    // FILE I/O
    // ---------------------------------------------------------------

    @Override
    public void writeFile(String data) throws IOException {
        String path = System.getProperty("user.dir");
        File file = new File(path + File.separator + "ticket.txt");
        ensureDirectoryExists(file);

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
            bw.write(data);
            bw.newLine();
        }
    }

    // ── Bulk-write (overwrites entire file) ────────────────────────────────────
    public void writeAllTickets(List<String> lines) throws IOException {
        String path = System.getProperty("user.dir");
        File file = new File(path + File.separator + "ticket.txt");
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, false))) {
            for (String line : lines) {
                bw.write(line);
                bw.newLine();
            }
        }
    }

    private void ensureDirectoryExists(File file) {
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
    }

    // ---------------------------------------------------------------
    // SYSTEM INITIALIZATION
    // ---------------------------------------------------------------

    /**
     * Scans the data file to find the highest existing ID.
     * Prevents duplicate IDs when the application is restarted.
     */
    public static void initCount() {
        File file = new File("C:\\crmSystem\\ticket.txt");
        if (!file.exists()) {
            count = 1;
            return;
        }

        int maxId = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] parts = line.split(",");
                if (parts.length > 0 && parts[0].startsWith("t")) {
                    try {
                        int currentIdNum = Integer.parseInt(parts[0].substring(1));
                        if (currentIdNum > maxId) maxId = currentIdNum;
                    } catch (NumberFormatException ignored) {}
                }
            }
        } catch (IOException e) {
            System.err.println("Error initializing Ticket counter: " + e.getMessage());
        }
        count = maxId + 1;
    }
}