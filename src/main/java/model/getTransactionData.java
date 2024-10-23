package model;

import koneksi.koneksi;

import java.sql.*;
import java.text.SimpleDateFormat;

public class getTransactionData {
    koneksi con = new koneksi();
    private int transaction_id,account_id;
    private String transaction_type;
    private double amount;
    private String transaction_date;

    public getTransactionData(int id) {
        this.account_id = id;
        loadData();
    }
    private void loadData(){
        String sql = "SELECT transaction_id, transaction_type, amount, transaction_date FROM transactions WHERE account_id = ?";
        try (Connection connection = con.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, this.account_id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                this.transaction_id = resultSet.getInt("transaction_id");
                this.transaction_type = resultSet.getString("transaction_type");//type di databasenya enum('deposit;,'withdraw')
                this.amount = resultSet.getDouble("amount");

                // Mengambil nilai TIMESTAMP dari database dan format ke String
                Timestamp timestamp = resultSet.getTimestamp("transaction_date");
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                this.transaction_date = dateFormat.format(timestamp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getTransaction_id() {
        return transaction_id;
    }

    public int getAccount_id() {
        return account_id;
    }

    public String getTransaction_type() {
        return transaction_type;
    }

    public double getAmount() {
        return amount;
    }

    public String getTransaction_date() {
        return transaction_date;
    }
}
