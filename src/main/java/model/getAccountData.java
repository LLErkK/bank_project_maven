package model;
import koneksi.koneksi;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class getAccountData {
    private int account_id, user_id;
    private double balance;
    private String no_rek;
    private String created_at;
    koneksi con = new koneksi();

    public getAccountData(int id) {
        this.account_id = id;
        loadAccountData();  // Memuat data akun dalam satu kali query
    }

    private void loadAccountData() {
        String sql = "SELECT user_id, balance, no_rek, created_at FROM accounts WHERE account_id = ?";
        try (Connection connection = con.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, account_id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                this.user_id = resultSet.getInt("user_id");
                this.balance = resultSet.getDouble("balance");
                this.no_rek = resultSet.getString("no_rek");

                // Mengambil nilai TIMESTAMP dari database dan format ke String
                Timestamp timestamp = resultSet.getTimestamp("created_at");
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                this.created_at = dateFormat.format(timestamp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getUser_id() {
        return user_id;
    }

    public double getBalance() {
        return balance;
    }

    public String getNo_rek() {
        return no_rek;
    }

    public String getCreated_at() {
        return created_at;
    }
}
