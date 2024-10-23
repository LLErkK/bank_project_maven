package model;

import koneksi.koneksi;

import java.sql.*;
import java.text.SimpleDateFormat;

public class getTransferData {
    koneksi con = new koneksi();
    private int transfer_id,sender_id,receiver_id;
    private double amount;
    private String transfer_date;

    public getTransferData(int transfer_id) {
        this.transfer_id = transfer_id;
        loadData();
    }
    private void loadData(){
        String sql = "SELECT sender_account_id, receiver_account_id, amount, transfer_date FROM transfers WHERE transfer_id = ?";
        try (Connection connection = con.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, this.transfer_id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                this.sender_id = resultSet.getInt("sender_account_id");
                this.receiver_id = resultSet.getInt("receiver_account_id");
                this.amount = resultSet.getDouble("amount");

                // Mengambil nilai TIMESTAMP dari database dan format ke String
                Timestamp timestamp = resultSet.getTimestamp("transfer_date");
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                this.transfer_date = dateFormat.format(timestamp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getTransfer_id() {
        return transfer_id;
    }

    public int getSender_id() {
        return sender_id;
    }

    public int getReceiver_id() {
        return receiver_id;
    }

    public double getAmount() {
        return amount;
    }

    public String getTransfer_date() {
        return transfer_date;
    }
}
