package myapp;

import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JOptionPane;

//refactor code ini agar lebih cepat dan efisien
//hanya sekali menghubungkan ke database kemudia atributnya disimpan ke variable global
//contoh this.username = rs.getString("username");

public class handler {
private static handler instance;
private int id;


    public handler(){
        this.id = id;
    }
    public static handler getInstance(int id){
        if(instance == null){
            instance = new handler();
        }
        return instance;
    }
    public int getID(){
        return id;
    }
    
    public static void clearSession() {
        instance = null;
    }


    

    private Connection connect() {
        // Ganti sesuai dengan konfigurasi MySQL Anda
        String url = "jdbc:mysql://localhost:3306/bank";
        String user = "root";
        String password = "";
        Connection con = null;

        try {
            // Load driver JDBC
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            System.out.println("Connection Failed: " + e.getMessage());
        }

        return con;
    }


    //ini adalah fungsi mengecek username dan password di database
    public boolean login(String username, String password) {
        Connection con = connect();
        if (con == null) {
            JOptionPane.showMessageDialog(null, "Cannot connect to the database!");
            return false;
        }

        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try {
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, username);
            pst.setString(2, password);

            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                // Login berhasil
                this.id = rs.getInt("id");
                //nanti dibuat fungsi yang dipanggil disini untuk jadi parameter getter untuk interface
                return true;
            } else {
                // Login gagal
                return false;
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        } finally {
            try {
                con.close();
            } catch (Exception e) {
                System.out.println("Error closing connection: " + e.getMessage());
            }
        }
    }
    //fungsi getter id
    public int getid() {
        return id;
    }
    //fungsi getter account lengkap berdasarkan id berhasil login
    
    //fungsi daftar
    // nomor rekening disimpan account
    public void daftar(String username, String password, String fullname) {
        if(username==null||password==null||fullname==null){
            JOptionPane.showMessageDialog(null, "Tidak boleh ada yang kosong!");
            return;
        }
        if(username.isEmpty()||password.isEmpty()||fullname.isEmpty()){
            JOptionPane.showMessageDialog(null, "Tidak boleh ada yang kosong!");
            daftar backdaftar = new daftar();
            backdaftar.setVisible(true);
            return;
        }
        if(username.length()<5){
            JOptionPane.showMessageDialog(null, "Username minimal 5 character");
            daftar backdaftar = new daftar();
            backdaftar.setVisible(true);
            backdaftar.setFullname(fullname);
            backdaftar.setPassword(password);
            return;
        }
        if(!isAlphabetOnly(fullname)){
            JOptionPane.showMessageDialog(null, "Full name hanya bisa di isi angka alphabet");
            daftar backdaftar = new daftar();
            backdaftar.setVisible(true);
            backdaftar.setUsername(username);
            backdaftar.setPassword(password);
            return;
        }
        String sql = "INSERT INTO users(username, password, fullname) VALUES (?, ?, ?)";
        String account = "INSERT INTO accounts(user_id, balance, no_rek) VALUES(?, ?, ?)";
        //buat mengecek atau mencoba jika berhasil connect ke db
        try (Connection conn = this.connect();
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
                            pstmtAccount.setLong(1, userid);
                            pstmtAccount.setDouble(2, 0.0); // Saldo awal, misal 0.0
                            pstmtAccount.setString(3, generateUniqueRekening()); // Nomor rekening unik
                            
                            pstmtAccount.executeUpdate();
                        }
                        
                        System.out.println("User dan akun berhasil disimpan!");
                        JOptionPane.showMessageDialog(null, "Selamat, Anda berhasil mendaftar!");
                        new login().setVisible(true);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "EROR :"+e.getMessage());
            daftar backdaftar = new daftar();
            backdaftar.setVisible(true);
            backdaftar.setFullname(fullname);
            backdaftar.setPassword(password);
            return;
        }


    }
    //fungsi mengecek apakah inputan hanya huruf
    public boolean isAlphabetOnly(String input) {
        if (input == null || input.isEmpty()) {
            return false; // Jika null atau kosong, dianggap tidak valid
        }
        return input.matches("^[a-zA-Z\\s]+$");
    }
    
    


    //fungsi generate nomor rekening
    public String generaterek(){
        SecureRandom random = new SecureRandom();
        StringBuilder no_rek = new StringBuilder(10);
        for (int i = 0; i < 10; i++) {
            int digit = random.nextInt(10);
            no_rek.append(digit);
        }

        return no_rek.toString();
    }

    // Mengecek apakah nomor rekening sudah ada di database
    public boolean isRekeningExists(String rekeningNumber) {
        boolean exists = false;
        
        // Koneksi ke database
        try (Connection connection = connect()) {
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
    //fungsi getter untuk dapat atribut user
    //username dan fullname dari users
    
    public String getUserName(int id){
        String username;
        Connection conn = connect();
        try {
            String query="SELECT username FROM users WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, id);
            ResultSet res = ps.executeQuery();
            res.next();
            username = res.getString("username");
            
            return username; 
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
        
        return "EROR";
    }
    public String getFullName(int id){
        String fullname;
        Connection conn = connect();
        try {
            String query="SELECT fullname FROM users WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, id);
            ResultSet res = ps.executeQuery();
            res.next();
            fullname = res.getString("fullname");
            
            return fullname; 
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
        
        return "EROR";
    }
    //saldo dan norek dari accounts
    public String getNoRek(int id){
        String norek;
        Connection conn = connect();
        try {
            String query="SELECT no_rek FROM accounts WHERE user_id = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, id);
            ResultSet res = ps.executeQuery();
            res.next();
            norek = res.getString("no_rek");
            
            return norek; 
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
        
        return "EROR";
    }
    public Double getSaldo(int id){
        Double saldo;
        Connection conn = connect();
        try {
            String query="SELECT balance FROM accounts WHERE user_id = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, id);
            ResultSet res = ps.executeQuery();
            res.next();
            saldo = res.getDouble("balance");
            
            return saldo; 
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
        
        return 0.0;
    }
    

}
