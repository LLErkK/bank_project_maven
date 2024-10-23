/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package myapp;

import view.LoginSignin.login;
import database.DatabaseSetup;
/**
 *
 * @author hp
 */
public class main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String[] arguments ={};
        DatabaseSetup.main(arguments);
        new login().setVisible(true);
    }
    
}
