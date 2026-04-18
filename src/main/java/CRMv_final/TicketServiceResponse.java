package CRMv_final;

public class TicketServiceResponse {
    private String resolveTime;
    private String response;

    // Constructor for new/empty response
    public TicketServiceResponse() {
        this.resolveTime = "null";
        this.response = "Pending Response";
    }

    // Constructor for loading from file
    public TicketServiceResponse(String resolveTime, String response) {
        this.resolveTime = resolveTime;
        this.response = response;
    }

    // Getters and Setters
    public String getResolveTime() { return resolveTime; }
    public void setResolveTime(String resolveTime) { this.resolveTime = resolveTime; }

    public String getResponse() { return response; }
    public void setResponse(String response) { this.response = response; }

    /**
     * Structure helper to keep the Ticket's toString clean.
     * Matches the expected file structure: resolveTime,response
     */
    @Override
    public String toString() {
        return nvl(resolveTime) + "," + nvl(response);
    }

    private String nvl(String val) {
        return (val == null || val.isEmpty() || val.equals("null")) ? "null" : val;
    }
}
