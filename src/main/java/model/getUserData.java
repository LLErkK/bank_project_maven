package model;
import koneksi.koneksi;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class getUserData {
    private int id;
    private String username;
    private String password;
    private String fullname;
    private String created_at;
    koneksi con = new koneksi();

    public getUserData(int id) {
        this.id = id;
        loadUserData();  // Memuat data user dalam satu kali query
    }

    private void loadUserData() {
        String sql = "SELECT username, password, fullname, created_at FROM users WHERE id = ?";
        try (Connection connection = con.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                this.username = resultSet.getString("username");
                this.password = resultSet.getString("password");
                this.fullname = resultSet.getString("fullname");

                // Mengambil nilai TIMESTAMP dari database dan format ke String
                Timestamp timestamp = resultSet.getTimestamp("created_at");
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                this.created_at = dateFormat.format(timestamp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Getter untuk setiap field, setelah nilai diambil oleh loadUserData()
    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getFullname() {
        return fullname;
    }

    public String getCreated_at() {
        return created_at;
    }
}
