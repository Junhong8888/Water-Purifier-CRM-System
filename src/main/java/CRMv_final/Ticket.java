package CRMv_final;

import java.io.*;
import java.util.HashMap;
import java.util.List;


public class Ticket implements Comparable<Ticket>{

    private String id;
    private String customerID;
    private String technician;
    private String date;
    private String content;

    // Composition Objects
    private TicketDetails details;
    private TicketServiceResponse serviceResponse;

    // Aggregation Object
    private TimeSlotBooking timeSlotBooking;

    private static int count = 1;

    // ---------------------------------------------------------------
    // CONSTRUCTORS
    // ---------------------------------------------------------------

    public Ticket() {
        this.id = assignID();
        this.details = new TicketDetails();
        this.serviceResponse = new TicketServiceResponse();
    }


    public Ticket(String customerID, String status, String priority,
                  String technician, String date, String description, String content) {
        this.id = assignID();
        this.customerID = customerID;
        this.technician = technician;
        this.date = date;
        this.content = content;
        this.details = new TicketDetails(status, priority, description);
        this.serviceResponse = new TicketServiceResponse("null", "Pending Response");
    }


    public Ticket(String customerID, String status, String priority,
                  String technician, String date, String resolveTime,
                  String description, String content, String response) {
        this.id = assignID();
        this.customerID = customerID;
        this.technician = technician;
        this.date = date;
        this.content = content;

        // Initializing sub-objects
        this.details = new TicketDetails(status, priority, description);
        this.serviceResponse = new TicketServiceResponse(resolveTime, response);
    }

    public Ticket(String id,String customerID, String status, String priority,
                  String technician, String date, String resolveTime,
                  String description, String content, String response) {
        this.id = id;
        this.customerID = customerID;
        this.technician = technician;
        this.date = date;
        this.content = content;

        // Initializing sub-objects
        this.details = new TicketDetails(status, priority, description);
        this.serviceResponse = new TicketServiceResponse(resolveTime, response);
    }

    private String assignID() {
        return "t" + (count++);
    }

    // ---------------------------------------------------------------
    // PROXY METHODS (Maintains compatibility with your other classes)
    // ---------------------------------------------------------------

    public String getTicketStatus()  { return details.getStatus(); }
    public void setTicketStatus(String s) { details.setStatus(s); }

    public String getPriorityLevel() { return details.getPriorityLevel(); }
    public void setPriorityLevel(String p) { details.setPriorityLevel(p); }

    public String getDescription()   { return details.getDescription(); }

    public String getResolveTime()   { return serviceResponse.getResolveTime(); }
    public void setResolveTime(String r) { serviceResponse.setResolveTime(r); }

    public String getResponse()      { return serviceResponse.getResponse(); }
    public void setResponse(String r) { serviceResponse.setResponse(r); }

    // Standard fields
    public String getId() { return id; }
    public String getCustomerID() { return customerID; }
    public String getTechnician() { return technician; }
    public void setTechnician(String t) { this.technician = t; }
    public String getDate() { return date; }
    public String getContent() { return content; }

    // ---------------------------------------------------------------
    // OPERATIONAL LOGIC & TOSTRING
    // ---------------------------------------------------------------

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        // Index Order: id, cid, status, priority, tech, date, resolveTime, desc, content, response
        sb.append(id).append(",")
                .append(nvl(customerID)).append(",")
                .append(nvl(details.getStatus())).append(",")
                .append(nvl(details.getPriorityLevel())).append(",")
                .append(nvl(technician)).append(",")
                .append(nvl(date)).append(",")
                .append(nvl(serviceResponse.getResolveTime())).append(",")
                .append(nvl(details.getDescription())).append(",")
                .append(nvl(content)).append(",")
                .append(nvl(serviceResponse.getResponse()));

        if (timeSlotBooking != null) {
            sb.append(",").append(timeSlotBooking.toString());
        }
        return sb.toString();
    }

    private String nvl(String val) {
        return (val == null || val.isEmpty() || val.equalsIgnoreCase("null")) ? "null" : val;
    }



    public static void initCount() {
        File file = new File(System.getProperty("user.dir") + File.separator + "ticket.txt");
        if (!file.exists()) return;
        int maxId = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");
                if (p.length > 0 && p[0].startsWith("t")) {
                    try {
                        int num = Integer.parseInt(p[0].substring(1));
                        if (num > maxId) maxId = num;
                    } catch (NumberFormatException ignored) {}
                }
            }
        } catch (IOException ignored) {}
        count = maxId + 1;
    }

    @Override
    public int compareTo(Ticket o) {
        HashMap<String,Integer> map = new HashMap<>();
        map.put("HIGH", 1);
        map.put("MEDIUM", 2);
        map.put("LOW", 3);

        int i = map.get(this.getPriorityLevel()) - map.get(o.getPriorityLevel());
        if (i == 0) i = compareNullSafe(this.date, o.date);
        return i;
    }

    private int compareNullSafe(String a, String b) {
        if (a == b) return 0;
        if (a == null) return 1;
        if (b == null) return -1;
        return a.compareToIgnoreCase(b);
    }

}