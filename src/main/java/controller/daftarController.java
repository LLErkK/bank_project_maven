package controller;
import koneksi.koneksi;
import view.LoginSignin.login;

import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JOptionPane;
public class daftarController {
    koneksi con = new koneksi();

    public void daftar(String username, String password, String fullname) {
        // Validasi sederhana untuk memeriksa apakah ada nilai kosong
        if (username == null || password == null || fullname == null ||
                username.isEmpty() || password.isEmpty() || fullname.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Tidak boleh ada yang kosong!");
            return;
        }

        // Validasi panjang username minimal 5 karakter
        if (username.length() < 5) {
            JOptionPane.showMessageDialog(null, "Username minimal 5 karakter");
            return;
        }

        String sql = "INSERT INTO users(username, password, fullname) VALUES (?, ?, ?)";
        String account = "INSERT INTO accounts(user_id, balance, no_rek) VALUES(?, ?, ?)";

        // Proses untuk menyimpan data ke database
        try (Connection conn = con.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            // Mengisi data user
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, fullname);

            // Eksekusi insert user
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                // Mendapatkan generated user_id
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        long userid = generatedKeys.getLong(1); // Ambil user_id yang baru ditambahkan

                        // Melanjutkan untuk insert ke tabel accounts
                        try (PreparedStatement pstmtAccount = conn.prepareStatement(account)) {
                            String noRek = generateUniqueRekening();
                            if (noRek == null || noRek.isEmpty()) {
                                throw new SQLException("Nomor rekening tidak boleh kosong");
                            }

                            pstmtAccount.setLong(1, userid);
                            pstmtAccount.setDouble(2, 0.0); // Saldo awal, misal 0.0
                            pstmtAccount.setString(3, noRek); // Nomor rekening unik

                            pstmtAccount.executeUpdate();
                        }

                        // Pendaftaran sukses
                        JOptionPane.showMessageDialog(null, "Selamat, Anda berhasil mendaftar!");
                        new login().setVisible(true);
                    }
                }
            }
        } catch (SQLException e) {
            // Menangani kesalahan SQL
            e.printStackTrace(); // Cetak stack trace lengkap untuk debugging
            JOptionPane.showMessageDialog(null, "EROR: " + e.getMessage());
        }
    }

    //fungsi generate nomor rekening
    public String generaterek(){
        SecureRandom random = new SecureRandom();
        StringBuilder rekening = new StringBuilder(10);
        for (int i = 0; i < 10; i++) {
            int digit = random.nextInt(10);
            rekening.append(digit);
        }

        return rekening.toString();
    }

    // Mengecek apakah nomor rekening sudah ada di database
    public boolean isRekeningExists(String rekeningNumber) {
        boolean exists = false;

        // Koneksi ke database
        try (Connection connection = con.connect()) {
            String query = "SELECT COUNT(*) FROM accounts WHERE no_rek = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, rekeningNumber);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                exists = resultSet.getInt(1) > 0; // Jika hasil query lebih dari 0, nomor rekening sudah ada
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return exists;
    }

    //fungsi memanggil generate no_rek
    //fungsi yang dipanggil oleh fungsi daftar
    public String generateUniqueRekening() {
        String rekeningNumber;
        do {
            rekeningNumber = generaterek();
        } while (isRekeningExists(rekeningNumber)); // Ulangi jika nomor sudah ada di database

        return rekeningNumber;
    }

}
