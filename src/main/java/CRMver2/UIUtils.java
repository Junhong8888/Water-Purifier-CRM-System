package CRMver2;/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author choky
 */
public class UIUtils {
    public static void printHeader(String title) {
        int width = 50;
        String line = "═".repeat(width);
        int padding = (width - title.length()) / 2;
        
        System.out.println("\n╔" + line + "╗");
        System.out.printf("║%" + padding + "s%s%" + (width - padding - title.length()) + "s║%n", "", title, "");
        System.out.println("╚" + line + "╝");
    }
}
