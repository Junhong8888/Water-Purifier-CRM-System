package CRMver2;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class SmartScheduler {

    public static void checkMaintenanceDue(Customer customer) {
        String installDateStr = customer.getPurifierModel().getInstallationDate();
        
        if (installDateStr == null || installDateStr.equalsIgnoreCase("null") || installDateStr.isEmpty()) {
            return; // Cannot schedule without a date
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDate installDate = LocalDate.parse(installDateStr, formatter);
            LocalDate today = LocalDate.now();

            // Calculate months since installation
            long monthsBetween = ChronoUnit.MONTHS.between(installDate, today);

            // Proactive trigger: If it's been exactly a multiple of 6 months
            if (monthsBetween > 0 && monthsBetween % 6 == 0) {
                System.out.println("\n  **********************************************************");
                System.out.println("  [SMART ALERT] ⏰ PROACTIVE MAINTENANCE DUE!");
                System.out.println("  Your purifier model (" + customer.getPurifierModel() + ") was installed " + monthsBetween + " months ago.");
                System.out.println("  It is highly recommended to schedule a general maintenance request today.");
                System.out.println("  **********************************************************");
            }
        } catch (Exception e) {
            // Silently ignore parsing errors so the system doesn't crash for bad dates
        }
    }
}