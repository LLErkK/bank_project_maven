package controller;
import koneksi.koneksi;
import java.sql.Connection;
import java.sql.SQLException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;


import javax.swing.JOptionPane;
public class depositController {
    //validasi angka?? biar gak input 0?
    //method menmbahkan atau update balance
private Connection conn;
private int id;
    public depositController(int id) {
        this.id =id;
        this.conn = new koneksi().connect();
    }
    public boolean isDepositValid(double nominal)
    {
        if(nominal>0){
            depositUpdate(nominal);
            return true;
        }
        return false;
    }
    private void depositUpdate(double nominal) {
        String query = "SELECT * FROM accounts WHERE user_id = ?";
        String updateQuery = "UPDATE accounts SET balance = ? WHERE user_id = ?";
        double balance = 0.0;

        try (PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setInt(1, this.id);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    balance = rs.getDouble("balance");
                    balance += nominal;

                    try (PreparedStatement pstUpdate = conn.prepareStatement(updateQuery)) {
                        pstUpdate.setDouble(1, balance);  // Saldo yang baru
                        pstUpdate.setInt(2, this.id);
                        pstUpdate.executeUpdate();

                        // Update riwayat transaksi
                        updateHistory("deposit", nominal);
                    }
                } else {
                    // Handle jika akun tidak ditemukan
                    System.out.println("Account not found.");

                }
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                System.out.println("Error closing connection: " + e.getMessage());
            }


        }
    }


    private void updateHistory(String type,double nominal)
    {
        String query="INSERT INTO transactions (account_id, transaction_type, amount) VALUES(?,?,?)";
        try {
            PreparedStatement pStUpdate = conn.prepareStatement(query);
            pStUpdate.setInt(1, this.id);
            pStUpdate.setString(2, type);
            pStUpdate.setDouble(3, nominal);

            pStUpdate.executeUpdate();
        } catch (Exception e) {
            System.out.println("eror: "+e.getMessage());
        }finally{
            try {
                if(conn !=  null) conn.close();
            } catch (Exception e) {
                System.out.println("eror closing connection: "+e.getMessage());
            }
        }
    }

}
