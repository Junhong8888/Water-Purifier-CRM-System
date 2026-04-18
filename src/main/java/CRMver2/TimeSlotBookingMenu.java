package CRMver2;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;

public class TimeSlotBookingMenu {
    private TimeSlotBookingService timeSlotBookingService;
    private TimeSlotBookingRepository timeSlotBookingRepository;
    private TimeSlotBooking timeSlotBooking;


    public TimeSlotBookingMenu() {
        this.timeSlotBookingService = new TimeSlotBookingService();
        this.timeSlotBookingRepository = new TimeSlotBookingRepository();
        this.timeSlotBooking = new TimeSlotBooking();
    }

    public void viewTimeSlotBooking(User user) throws IOException {
        ArrayList<TimeSlotBooking> allBookings = timeSlotBookingRepository.loadTimeSlotBookingToList();
        ArrayList<Ticket> allTickets = new TicketRepository().loadAll();
        ArrayList<Customer> allCustomers = new CustomerRepository().loadCustomersToList();

        boolean found = false;

        if (user instanceof Customer customer) {
            System.out.println("\n--- My Appointments ---");
            System.out.printf("%-15s | %-20s | %-10s%n", "Ticket ID", "Date & Time", "Status");
            System.out.println("------------------------------------------------------------");

            for (TimeSlotBooking booking : allBookings) {
                for (Ticket ticket : allTickets) {
                    if (ticket.getId().equals(booking.getTicketID()) &&
                            ticket.getCustomerID().equals(customer.getId())) {

                        System.out.printf("%-15s | %-20s | %-10s%n",
                                booking.getTicketID(),
                                booking.getDateTime().format(TimeSlotBooking.DATETIME_FMT),
                                ticket.getTicketStatus());
                        found = true;
                    }
                }
            }

        } else if (user instanceof Staff technician) {
            System.out.println("\n--- Technician Schedule ---");
            System.out.printf("%-15s | %-20s | %-15s | %-15s%n", "Ticket ID", "Date & Time", "Cust ID", "Cust Name");
            System.out.println("--------------------------------------------------------------------------------");

            for (TimeSlotBooking booking : allBookings) {
                for (Ticket ticket : allTickets) {
                    // 1. Check if the ticket belongs to this booking and this technician
                    if (ticket.getId().equals(booking.getTicketID()) &&
                            ticket.getTechnician().equalsIgnoreCase(technician.getUsername())) {

                        // 2. SEARCH for the specific customer name for THIS ticket
                        String currentCustName = "Unknown";
                        for (Customer c : allCustomers) {
                            if (c.getId().equals(ticket.getCustomerID())) {
                                currentCustName = c.getUsername();
                                break;
                            }
                        }

                        System.out.printf("%-15s | %-20s | %-15s | %-15s%n",
                                booking.getTicketID(),
                                booking.getDateTime().format(TimeSlotBooking.DATETIME_FMT),
                                ticket.getCustomerID(),
                                currentCustName);
                        found = true;
                    }
                }
            }
        }

        if (!found) {
            System.out.println("  [!] No scheduled bookings found.");
        }
    }

    public void selectTimeSlotBooking(String id) throws IOException {
        Scanner sc = new Scanner(System.in);
        boolean booked = false;

        do {
            System.out.print("  Enter your desired date (DD-MM-YYYY): ");
            String dateInput = sc.nextLine().trim();

            LocalDate chosenDate;
            try {
                chosenDate = LocalDate.parse(dateInput, TimeSlotBooking.DATE_FMT);
            } catch (DateTimeParseException e) {
                System.out.println("  [ERROR] Invalid date format — please use DD-MM-YYYY.");
                continue;
            }

            if (chosenDate.isBefore(LocalDate.now())) {
                System.out.println("  [ERROR] Date cannot be in the past.");
                continue;
            }

            // --- REFINED: Show the slots with strike-throughs ---
            // This calls the method we fixed earlier
            timeSlotBookingService.checkTimeSlot(chosenDate);

            String[] timeSlot = TimeSlotBooking.AVAILABLE_TIME_SLOTS;
            System.out.print("  Select a time slot (1-" + timeSlot.length + "): ");

            int slotIndex;
            try {
                slotIndex = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("  [ERROR] Please enter a valid number.");
                continue;
            }

            if (slotIndex < 1 || slotIndex > timeSlot.length) {
                System.out.printf("  [ERROR] Select between 1 and %d.%n", timeSlot.length);
                continue;
            }

            // Create the LocalDateTime for the specific choice
            LocalDateTime chosen = LocalDateTime.parse(
                    dateInput + " " + timeSlot[slotIndex - 1], TimeSlotBooking.DATETIME_FMT);

            // Double-check availability before saving
            if (timeSlotBookingService.isSlotTaken(chosen)) {
                System.out.println("  [X] This slot was just taken! Please choose a different one.");
                continue;
            }

            // All checks passed — commit the booking
            timeSlotBooking.setTicketID(id);
            timeSlotBooking.setDateTime(chosen);
            booked = true;

            timeSlotBookingRepository.writeFile(timeSlotBooking.toString());
            System.out.println("  ✓ Booking confirmed: " + chosen.format(TimeSlotBooking.DATETIME_FMT));

        } while (!booked);
    }
}
