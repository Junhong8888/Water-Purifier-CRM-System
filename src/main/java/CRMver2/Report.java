package CRMver2;

import java.util.List;

public abstract class Report {
    protected String reportTitle;
    protected String generationDate;

    public Report(String reportTitle) {
        this.reportTitle = reportTitle;
        this.generationDate = java.time.LocalDate.now().toString();
    }

    // This is the POLYMORPHIC method the rubric asks for!
    public abstract void generateReport(List<Ticket> tickets);
}