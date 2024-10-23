package koneksi;

import java.sql.Connection;
import java.sql.DriverManager;

public class koneksi {
    public Connection connect() {
        // Ganti sesuai dengan konfigurasi MySQL Anda
        // Ganti sesuai dengan konfigurasi MySQL Anda
        String url = "jdbc:mysql://localhost :3306/bank";
        String user = "root";
        String password = "";
        Connection con = null;

        try {
            // Load driver JDBC
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(url, user, password);

            if (con != null) {
                System.out.println("Connected to the database!");
            } else {
                System.out.println("Failed to make connection!");
            }
        } catch (Exception e) {
            System.out.println("Connection Failed: " + e.getMessage());
            System.out.println("jika eror");
        }

        return con;
    }
}
