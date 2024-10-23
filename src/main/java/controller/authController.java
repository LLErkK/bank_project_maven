package controller;

import koneksi.koneksi;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import javax.swing.*;
import java.sql.Connection;

public class authController {

    private int id;  // Variabel untuk menyimpan ID setelah login
    koneksi con = new koneksi();

    public boolean login(String username, String password) {
        Connection kon = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            // Koneksi ke database
            kon = con.connect();
            if (kon == null) {
                JOptionPane.showMessageDialog(null, "Tidak bisa konek ke database");
                return false;
            }

            // Query untuk memeriksa username dan password
            String query = "SELECT id FROM users WHERE username = ? AND password = ?";
            pst = kon.prepareStatement(query);
            pst.setString(1, username);
            pst.setString(2, password);

            // Eksekusi query
            rs = pst.executeQuery();
            if (rs.next()) {
                // Jika login berhasil, simpan ID pengguna
                this.id = rs.getInt("id");
                return true;
            } else {
                // Jika login gagal, berikan feedback
                JOptionPane.showMessageDialog(null, "Username atau password salah");
                return false;
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        } finally {
            // Menutup semua resource
            try {
                if (rs != null) rs.close();
                if (pst != null) pst.close();
                if (kon != null) kon.close();
            } catch (SQLException e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }
    }

    // Getter untuk mengambil ID user setelah login
    public int getId() {
        return this.id;
    }
}
