// Ticket.java

package CRM;

public class Ticket {
    // Unified class with v1 robust ID management and v2 dynamic file paths
    private String ticketID;
    private String filePath;

    public Ticket(String ticketID, String filePath) {
        this.ticketID = ticketID;
        this.filePath = filePath;
    }

    public String getTicketID() {
        return ticketID;
    }

    public void setTicketID(String ticketID) {
        this.ticketID = ticketID;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void displayInfo() {
        System.out.println("Ticket ID: " + ticketID + " | File Path: " + filePath);
    }
}