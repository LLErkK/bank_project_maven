/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package myapp;

import java.util.ArrayList;

/**
 *
 * @author hp
 */
public class coba {
    public static void main(String[] args) {
        System.out.println("tai");
        Operasi opp = new Operasi();
        ArrayList<ArrayList<String>> contoh = opp.getUsers(5);
        
        // Mencetak ArrayList
        for (ArrayList<String> sublist : contoh) {
            for (String item : sublist) {
                System.out.print(item + " "); // Mencetak setiap item dalam sublist
            }
            System.out.println(); // Baris baru setelah setiap sublist
        }
    }
}
