package controller;

import koneksi.koneksi;
import java.sql.Connection;
import java.util.ArrayList;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;

public class adminController {

    private Connection conn;

    public adminController() {
        this.conn = new koneksi().connect();
    }

    public ArrayList<ArrayList<String>> getUsers(int limit) {
        ArrayList<ArrayList<String>> usersList = new ArrayList<>(); // List utama untuk menampung pengguna

        String query = "SELECT users.id AS user_id, users.username, users.password, users.fullname, " +
                "accounts.account_id, accounts.balance, accounts.no_rek, accounts.created_at " +
                "FROM users " +
                "INNER JOIN accounts " +
                "ON users.id = accounts.account_id " +
                "LIMIT ?";

        try (PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setInt(1, limit); // Set the limit parameter
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    ArrayList<String> user = new ArrayList<>(); // List untuk setiap pengguna

                    // Ambil kolom-kolom yang relevan
                    user.add(String.valueOf(rs.getInt("user_id"))); // user_id
                    user.add(rs.getString("username")); // username
                    user.add(rs.getString("password")); // password
                    user.add(rs.getString("fullname")); // fullname
                    user.add(String.valueOf(rs.getDouble("balance"))); // balance
                    user.add(rs.getString("no_rek")); // noRek

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    user.add(dateFormat.format(rs.getTimestamp("created_at"))); // formattedDate

                    usersList.add(user); // Tambahkan user ke daftar utama
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Consider using a logger here
        }
        return usersList; // Kembalikan daftar pengguna
    }

    public ArrayList<ArrayList<String>> getTransfer(int limit) {
        ArrayList<ArrayList<String>> transferList = new ArrayList<>(); // List utama untuk menyimpan data transfer

        String query = "SELECT * FROM transfers LIMIT ?"; // Gunakan LIMIT untuk 'limit'

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, limit); // Set parameter limit
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ArrayList<String> transfer = new ArrayList<>(); // Buat list untuk setiap transfer

                    // Ambil data dari ResultSet
                    transfer.add(String.valueOf(rs.getInt("transfer_id"))); // id
                    transfer.add(String.valueOf(rs.getInt("sender_account_id"))); // sender
                    transfer.add(String.valueOf(rs.getInt("receiver_account_id"))); // receiver
                    transfer.add(String.valueOf(rs.getDouble("amount"))); // nominal

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    transfer.add(dateFormat.format(rs.getTimestamp("transfer_date"))); // date

                    transferList.add(transfer); // Tambahkan list transfer ke list utama
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Consider using a logger here
        }
        return transferList; // Kembalikan list transfer
    }

    public ArrayList<ArrayList<String>> getTransaction(int limit) {
        ArrayList<ArrayList<String>> transactionList = new ArrayList<>(); // List utama untuk menyimpan data transaksi

        String query = "SELECT * FROM transactions LIMIT ?"; // Gunakan LIMIT untuk 'limit'

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, limit); // Set parameter limit
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ArrayList<String> transaction = new ArrayList<>(); // Buat list untuk setiap transaksi

                    // Ambil data dari ResultSet
                    transaction.add(String.valueOf(rs.getInt("transaction_id"))); // id_transaction
                    transaction.add(String.valueOf(rs.getInt("account_id"))); // id_user
                    transaction.add(rs.getString("transaction_type")); // type
                    transaction.add(String.valueOf(rs.getDouble("amount"))); // amount

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    transaction.add(dateFormat.format(rs.getTimestamp("transaction_date"))); // date

                    transactionList.add(transaction); // Tambahkan list transaksi ke list utama
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Consider using a logger here
        }
        return transactionList; // Kembalikan list transaksi
    }
}
