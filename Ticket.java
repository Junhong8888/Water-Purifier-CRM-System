package Other;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Ticket implements Comparable<Ticket>, FileStorage {
    private String customerID;
    private String ticketStatus;
    private String priorityLevel;
    private String technician;
    private String date;
    private String resolveTime;
    private String description;
    private String content;
    private String response;



    public Ticket() {

    }

    public Ticket(String customerID, String ticketStatus, String priorityLevel, String technician,
                  String date) {
        this.customerID = customerID;
        this.ticketStatus = ticketStatus;
        this.priorityLevel = priorityLevel;
        this.technician = technician;
        this.date = date;
    }

    public Ticket(String customerID, String ticketStatus, String priorityLevel, String technician,
                  String date, String resolveTime, String description, String content, String response) {
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

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public String getTicketStatus() {
        return ticketStatus;
    }

    public void setTicketStatus(String ticketStatus) {
        this.ticketStatus = ticketStatus;
    }

    public String getPriorityLevel() {
        return priorityLevel;
    }

    public void setPriorityLevel(String priorityLevel) {
        this.priorityLevel = priorityLevel;
    }

    public String getTechnician() {
        return technician;
    }

    public void setTechnician(String technician) {
        this.technician = technician;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getResolveTime() {
        return resolveTime;
    }

    public void setResolveTime(String resolveTime) {
        this.resolveTime = resolveTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    @Override
    public String toString() {
        return customerID + "," +  ticketStatus + "," + priorityLevel + "," + technician + "," + date
        + "," + resolveTime + "," + description + "," + content + "," + response;

    }


    @Override
    public int compareTo(Ticket o) {
        //Sorting the order start from priority level,then date,ticket status,technician,customer
        int i = this.getPriorityLevel().compareTo(o.getPriorityLevel());
        i = i == 0 ? this.getDate().compareTo(o.getDate()) : i;
        i = i == 0 ? this.getTicketStatus().compareTo(o.getTicketStatus()) : i;
        i = i == 0 ? this.getTechnician().compareTo(o.getTechnician()) : i;
        i = i == 0 ? this.getCustomerID().compareTo(o.getCustomerID()) : i;
        return i;
    }

    @Override
    public void writeFile(String data) throws IOException {
        String path = System.getProperty("user.dir");
        File file = new File(path + File.separator + "ticket.txt");

        if (!file.exists()) {
            boolean newFile = file.createNewFile();
            System.out.println(newFile);
        }

        BufferedWriter bw = new BufferedWriter(new  FileWriter(file,true));

        bw.write(data,0,data.length());
        bw.newLine();
        bw.close();
    }

    public void writeAllTickets(List<String> lines) {
        // Opening the BufferedWriter here once wipes the old file
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("ticket.txt"))) {
            for (String line : lines) {
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
