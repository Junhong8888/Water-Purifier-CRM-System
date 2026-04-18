package CRMv_final;

public class TicketDetails {
    private String status;
    private String priorityLevel;
    private String description;

    public TicketDetails() {

    }

    public TicketDetails(String status, String priorityLevel, String description) {
        this.status = status;
        this.priorityLevel = priorityLevel;
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public String getPriorityLevel() {
        return priorityLevel;
    }

    public String getDescription() {
        return description;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setPriorityLevel(String priorityLevel) {
        this.priorityLevel = priorityLevel;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}