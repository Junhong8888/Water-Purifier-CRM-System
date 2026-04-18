package CRMv_final;


import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * TicketRepository — STORAGE LAYER
 * Responsibility: ONLY reads and writes ticket.txt.
 * No Scanner, no System.out, no business logic.
 */
public class TicketRepository implements FileStorage{

    private static File ticketFile() {
        return new File(System.getProperty("user.dir") + File.separator + "ticket.txt");
    }

    // ---------------------------------------------------------------
    // FILE I/O & SYSTEM INIT (Maintains your architecture)
    // ---------------------------------------------------------------

    @Override
    public void writeFile(String data) throws IOException {
        File file = new File(System.getProperty("user.dir") + File.separator + "ticket.txt");
        if (file.getParentFile() != null && !file.getParentFile().exists()) file.getParentFile().mkdirs();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
            bw.write(data);
            bw.newLine();
        }
    }

    public void writeAllTickets(List<String> lines) throws IOException {
        File file = new File(System.getProperty("user.dir") + File.separator + "ticket.txt");
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, false))) {
            for (String line : lines) {
                bw.write(line);
                bw.newLine();
            }
        }
    }

    /** Load all tickets from ticket.txt into a sorted ArrayList. */
    public ArrayList<Ticket> loadAll() throws IOException {
        ArrayList<Ticket> list = new ArrayList<>();
        File file = ticketFile();
        if (!file.exists()) return list;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] p = line.split(",", -1);
                if (p.length >= 10) {
                    for (int i = 0; i < p.length; i++) {
                        if ("null".equalsIgnoreCase(p[i])) p[i] = null;
                    }
                    // RESTORATION CONSTRUCTOR — keeps file ID, doesn't touch count
                    list.add(new Ticket(p[0], p[1], p[2], p[3], p[4],
                            p[5], p[6], p[7], p[8], p[9]));
                }
            }
        }
        Collections.sort(list);
        return list;
    }

    /** Overwrite ticket.txt with the entire list. */
    public void saveAll(ArrayList<Ticket> tickets) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ticketFile(), false))) {
            for (Ticket t : tickets) {
                bw.write(t.toString());
                bw.newLine();
            }
        }
    }

    /**
     * FIX 1: append a single NEW ticket to the file.
     * TicketService.submitTicket() now calls this instead of t.writeFile()
     * so ALL file access stays inside the Repository layer.
     */
    public void append(Ticket t) throws IOException {
        File file = ticketFile();
        if (!file.exists()) file.createNewFile();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
            bw.write(t.toString());
            bw.newLine();
        }
    }

    public void update(Ticket updated) throws IOException {
        // 1. Load the current state of the file
        ArrayList<Ticket> all = loadAll();

        // 2. Find and replace the specific ticket by ID
        boolean found = false;
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getId().equalsIgnoreCase(updated.getId())) {
                all.set(i, updated); // Replace the old object with the modified one
                found = true;
                break;
            }
        }

        // 3. If found, overwrite the file with the NEW list
        if (found) {
            saveAll(all);
        } else {
            System.out.println("  [Warning] Update failed: Ticket ID " + updated.getId() + " not found.");
        }
    }

}
