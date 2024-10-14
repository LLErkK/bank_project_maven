/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package myapp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.swing.JOptionPane;



/**
 *
 * @author hp
 */
public class Operasi {
private double interest;  
    public Operasi(){
        this.interest=0.005;
    }
    //fungsi connect ke database
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
    //fungsi mengembalikan nilai balance jika setelah depo
    public double  deposit(int parameter, double nominal){
        double balance = 0;
        Connection conn = connect();
        if (conn == null) {
            JOptionPane.showMessageDialog(null, "Cannot connect to the database!");
            return balance;
        }

        String query = "SELECT * FROM accounts WHERE user_id = ?";
        String updateQuery ="UPDATE accounts SET balance = ? WHERE user_id =?";
        try {
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setInt(1, parameter);
            

            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                balance = rs.getDouble("balance");
                balance +=nominal;
                PreparedStatement pstUpdate = conn.prepareStatement(updateQuery);
                pstUpdate.setDouble(1, balance);  // Saldo yang baru
                pstUpdate.setInt(2, parameter); 

                pstUpdate.executeUpdate();
                updateHistory(parameter, "deposit", nominal);
            } else {
                // Login gagal
                return balance;
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return balance;
        } finally {
            try {
                conn.close();
            } catch (Exception e) {
                System.out.println("Error closing connection: " + e.getMessage());
            }
        }
        
        return balance;
    }
    //fungsi cek saldo
    public double cekSaldo(int parameter){
        double balance = 0;
        Connection conn = connect();
        if (conn == null) {
            JOptionPane.showMessageDialog(null, "Cannot connect to the database!");
            return balance;
        }
        String querry ="SELECT balance FROM accounts WHERE user_id = ?";
        try {
            PreparedStatement pStSelect = conn.prepareStatement(querry);
            pStSelect.setInt(1, parameter);
            ResultSet rs = pStSelect.executeQuery();
            if(rs.next()){
                balance = rs.getDouble("balance");
            }
        } catch (Exception e) {
            System.out.println("eror: "+e.getMessage());
        }
        return balance;
    }

    //cek saldo masa depan
    public double cekSaldoMasaDepan(int parameter, int bulan){
        double balance = 0;
        double bunga;
        Connection conn = connect();
        if (conn == null) {
            JOptionPane.showMessageDialog(null, "Cannot connect to the database!");
            return balance;
        }
        String querry ="SELECT balance FROM accounts WHERE user_id = ?";
        try {
            PreparedStatement pStSelect = conn.prepareStatement(querry);
            pStSelect.setInt(1, parameter);
            ResultSet rs = pStSelect.executeQuery();
            if(rs.next()){
                balance = rs.getDouble("balance");
                bunga = balance *(interest/100)*(bulan/12);
                balance = balance + bunga;
                return balance;
            }
        } catch (Exception e) {
            System.out.println("eror: "+e.getMessage());
        }
        return balance;
    }

    public double tarik(int parameter,double nominal){
        double balance = 0;
        Connection conn = connect();
        if (conn == null) {
            JOptionPane.showMessageDialog(null, "Cannot connect to the database!");
            return balance;
        }
        String query="SELECT * FROM accounts WHERE user_id = ?";
        String updateQuery="UPDATE accounts SET balance = ? WHERE user_id =?";
        try {
            PreparedStatement pStSelect = conn.prepareStatement(query);
            pStSelect.setInt(1, parameter);
            ResultSet rs = pStSelect.executeQuery();
            if(rs.next()){
                balance = rs.getDouble("balance");
                if(nominal <= balance){
                    balance -= nominal;
                    PreparedStatement pstUpdate = conn.prepareStatement(updateQuery);
                    pstUpdate.setDouble(1, balance);
                    pstUpdate.setInt(2, parameter);
                    pstUpdate.executeUpdate();

                    updateHistory(parameter, "withdraw", nominal);
                }else{
                    JOptionPane.showMessageDialog(null, "Saldo Tidak Cukup!");
                }
                
            }
        } catch (Exception e) {
            System.out.println("eror: "+e.getMessage());
        }
        return balance;
    }
    //fungsi validasi apakah transfer bisa dilakukan
    public boolean validTranfer(int parameter, String no_rek, double nominal) {
        Connection conn = connect();
        if (conn == null) {
            JOptionPane.showMessageDialog(null, "Cannot connect to the database!");
            return false;
        }
    
        String cekSaldo = "SELECT balance FROM accounts WHERE user_id = ?";
        String cekRekening = "SELECT 1 FROM accounts WHERE no_rek = ?";
    
        try {
            // Mengecek saldo pengirim
            PreparedStatement pstSelectBalance = conn.prepareStatement(cekSaldo);
            pstSelectBalance.setInt(1, parameter);
            ResultSet rsBal = pstSelectBalance.executeQuery();
    
            // Jika saldo ditemukan
            if (rsBal.next()) {
                double balance = rsBal.getDouble("balance");
                if (balance < nominal) {
                    // Saldo tidak cukup
                    JOptionPane.showMessageDialog(null, "Saldo tidak mencukupi!");
                    return false;
                }
    
                // Mengecek apakah nomor rekening penerima valid
                PreparedStatement pstSelectNoRek = conn.prepareStatement(cekRekening);
                pstSelectNoRek.setString(1, no_rek);
                ResultSet rsRek = pstSelectNoRek.executeQuery();
    
                // Jika nomor rekening valid
                if (rsRek.next()) {
                    return true;
                } else {
                    JOptionPane.showMessageDialog(null, "Nomor rekening tujuan tidak ditemukan!");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Pengirim tidak ditemukan!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        } finally {
            // Menutup koneksi dalam blok finally untuk memastikan selalu tertutup
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error closing connection: " + e.getMessage());
            }
        }
    
        return false;
    }
    
    //buat fungsi tranfer ketika syarat transfer tervalidasi
    //ini yang dipanggil
    public void actionTransfer(int parameter, String no_rek, double nominal) {
        if (validTranfer(parameter, no_rek, nominal)) {
            Connection conn = connect();
            if (conn == null) {
                JOptionPane.showMessageDialog(null, "Cannot connect to the database!");
                return;
            }
    
            String sender = "UPDATE accounts SET balance = ? WHERE user_id = ?";
            String receiver = "UPDATE accounts SET balance = ? WHERE no_rek = ?";
            String getSenderBalance = "SELECT balance FROM accounts WHERE user_id = ? FOR UPDATE";
            String getReceiverBalance = "SELECT balance FROM accounts WHERE no_rek = ? FOR UPDATE";
            
            try {
                // Disable auto-commit mode to start transaction
                conn.setAutoCommit(false);
    
                // Mendapatkan saldo pengirim dan mengunci barisnya
                PreparedStatement pstGetSenderBalance = conn.prepareStatement(getSenderBalance);
                pstGetSenderBalance.setInt(1, parameter);
                ResultSet rsSend = pstGetSenderBalance.executeQuery();
                double balanceSender = 0;
                if (rsSend.next()) {
                    balanceSender = rsSend.getDouble("balance");
                } else {
                    JOptionPane.showMessageDialog(null, "Pengirim tidak ditemukan!");
                    conn.rollback();
                    return;
                }
    
                // Mendapatkan saldo penerima dan mengunci barisnya
                PreparedStatement pstGetReceiverBalance = conn.prepareStatement(getReceiverBalance);
                pstGetReceiverBalance.setString(1, no_rek);
                ResultSet rsReceive = pstGetReceiverBalance.executeQuery();
                double balanceReceiver = 0;
                if (rsReceive.next()) {
                    balanceReceiver = rsReceive.getDouble("balance");
                } else {
                    JOptionPane.showMessageDialog(null, "Penerima tidak ditemukan!");
                    conn.rollback();
                    return;
                }
    
                // Cek apakah saldo pengirim cukup
                if (balanceSender < nominal) {
                    JOptionPane.showMessageDialog(null, "Saldo pengirim tidak cukup!");
                    conn.rollback();
                    return;
                }
    
                // Update saldo pengirim
                PreparedStatement pstSetSenderBalance = conn.prepareStatement(sender);
                pstSetSenderBalance.setDouble(1, balanceSender - nominal);
                pstSetSenderBalance.setInt(2, parameter);
                pstSetSenderBalance.executeUpdate();
    
                // Update saldo penerima
                PreparedStatement pstSetReceiverBalance = conn.prepareStatement(receiver);
                pstSetReceiverBalance.setDouble(1, balanceReceiver + nominal);
                pstSetReceiverBalance.setString(2, no_rek);
                pstSetReceiverBalance.executeUpdate();
    
                
    
                // Commit the transaction
                conn.commit();

                // Mencatat riwayat transfer
                TransferHistory(parameter, no_rek, nominal);
            } catch (Exception e) {
                try {
                    // Rollback transaction in case of error
                    conn.rollback();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Rollback Error: " + ex.getMessage());
                }
                JOptionPane.showMessageDialog(null, "Transfer Error: " + e.getMessage());
            } finally {
                try {
                    // Enable auto-commit mode again
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(null, "Connection Close Error: " + e.getMessage());
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Saldo anda kurang atau no rekening tujuan salah!");
        }
    }
    
    //fungsi atau method menyimpan history transfer
    //memerlukan sender dan receiver id dan amount atau nominalnya
    private void TransferHistory(int pengirim, String no_rek, double nominal) {
        Connection conn = null;
        int id_receiver = 0;
    
        try {
            conn = connect();
            if (conn == null) {
                JOptionPane.showMessageDialog(null, "Cannot connect to the database!");
                return;
            }
    
            // Disable auto-commit to manage the transaction manually
            conn.setAutoCommit(false);
    
            // Mendapatkan id receiver
            String getIdReceiver = "SELECT account_id FROM accounts WHERE no_rek = ?";
            PreparedStatement pst = conn.prepareStatement(getIdReceiver);
            pst.setString(1, no_rek);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                id_receiver = rs.getInt("account_id");
            } else {
                JOptionPane.showMessageDialog(null, "Receiver account not found!");
                return;
            }
    
            // Setelah mendapatkan id receiver waktunya update history transfer
            String updateTransfer = "INSERT INTO transfers(sender_account_id, receiver_account_id, amount) VALUES (?, ?, ?)";
            PreparedStatement pstUpdate = conn.prepareStatement(updateTransfer);
            pstUpdate.setInt(1, pengirim);
            pstUpdate.setInt(2, id_receiver);
            pstUpdate.setDouble(3, nominal);
            pstUpdate.executeUpdate();
    
            // Commit the transaction
            conn.commit();
            JOptionPane.showMessageDialog(null, "Transfer history updated successfully!");
    
        } catch (Exception e) {
            try {
                if (conn != null) {
                    conn.rollback(); // Rollback transaction in case of error
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            JOptionPane.showMessageDialog(null, "Unable to save transfer history: " + e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true); // Restore auto-commit mode
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    

    //fungsi membuat history user
    private void updateHistory(int parameter,String Type, double nominal){
        Connection conn = connect();
        if (conn == null) {
            JOptionPane.showMessageDialog(null, "Cannot connect to the database!(*UpdateHistory)");
        }
        String query="INSERT INTO transactions (account_id, transaction_type, amount) VALUES(?,?,?)";
        try {
            PreparedStatement pStUpdate = conn.prepareStatement(query);
            pStUpdate.setInt(1, parameter);
            pStUpdate.setString(2, Type);
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
    //untuk fungsi menampilkan history aku ingin pakai metode "Opsi 1: Menggunakan List<Object[]>" dan ditampilkan dalam bentuk jtabel
    //mendapatkan data users
    public ArrayList<ArrayList<String>> getUsers(int banyak) {
        ArrayList<ArrayList<String>> usersList = new ArrayList<>(); // List utama untuk menampung pengguna
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
    
        try {
            conn = connect();
            if (conn == null) {
                JOptionPane.showMessageDialog(null, "Cannot connect to the database!");
                return usersList; // Return empty list if no connection
            }
    
            // Query untuk menggabungkan tabel users dan accounts
            String query =  "SELECT users.id AS user_id, users.username, users.password, users.fullname, "+
                            "accounts.account_id, accounts.balance, accounts.no_rek, accounts.created_at "+
                            "FROM users "+ 
                            "INNER JOIN accounts "+ 
                            "ON users.id = accounts.account_id "+ 
                            "LIMIT ?";
     
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, banyak); // Set the limit parameter
            rs = pstmt.executeQuery();
    
            while (rs.next()) {
                ArrayList<String> user = new ArrayList<>(); // List untuk setiap pengguna
    
                // Ambil kolom-kolom yang relevan dari tabel users dan accounts
                String userId = Integer.toString(rs.getInt("user_id"));
                String username = rs.getString("username");
                String password = rs.getString("password");
                String fullname = rs.getString("fullname");
                String balance = Double.toString(rs.getDouble("balance"));
                String noRek = rs.getString("no_rek");
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String formattedDate = dateFormat.format(rs.getTimestamp("created_at"));
    
    
                // Tambahkan data ke list user
                // Check for null values before adding to the list
                user.add(userId != null ? userId : "");
                user.add(username != null ? username : "");
                user.add(password != null ? password : "");
                user.add(fullname != null ? fullname : "");
                user.add(balance != null ? balance : "");
                user.add(noRek != null ? noRek : "");
                user.add(formattedDate != null ? formattedDate : "");
    
                // Tambahkan user ke daftar utama
                usersList.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Or use a logger
        } finally {
            // Menutup sumber daya
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return usersList;
    }
    

    //mendapatkan arraylist data dari transfer
    public ArrayList<ArrayList<String>> getTransfer(int banyak) {
        ArrayList<ArrayList<String>> transferList = new ArrayList<>(); // List utama untuk menyimpan data transfer
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String id, sender, receiver, nominal, date;
        
        try {
            conn = connect(); // Hubungkan ke database
            if (conn == null) {
                JOptionPane.showMessageDialog(null, "Cannot connect to the database!");
                return transferList; // Kembalikan list kosong jika tidak bisa terhubung
            }
    
            String query = "SELECT * FROM transfers LIMIT ?"; // Gunakan LIMIT untuk 'banyak'
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, banyak); // Set parameter limit
            rs = pstmt.executeQuery();
    
            while (rs.next()) {
                ArrayList<String> user = new ArrayList<>(); // Buat list untuk setiap transfer

                // Ambil data dari ResultSet
                id = Integer.toString(rs.getInt("transfer_id"));
                sender = Integer.toString(rs.getInt("sender_account_id"));
                receiver = Integer.toString(rs.getInt("receiver_account_id"));
                nominal = Double.toString(rs.getDouble("amount"));

                // Format tanggal
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                date = dateFormat.format(rs.getTimestamp("transfer_date")); // Menggunakan getTimestamp untuk waktu
                
                // Tambahkan data ke dalam list pengguna
                user.add(id);
                user.add(sender);
                user.add(receiver);
                user.add(nominal);
                user.add(date);
                
                transferList.add(user); // Tambahkan list user ke list utama
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Tampilkan kesalahan SQL
        } finally {
            // Tutup sumber daya
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace(); // Tampilkan kesalahan penutupan
            }
        }
        return transferList; // Kembalikan list transfer
    }

    //mendaptkan data transaksi
    public ArrayList<ArrayList<String>> getTransaction(int banyak) {
        ArrayList<ArrayList<String>> transactionList = new ArrayList<>(); // List utama untuk menyimpan data transfer
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String id_transaction,id_user,type,amount,date;
        
        try {
            conn = connect(); // Hubungkan ke database
            if (conn == null) {
                JOptionPane.showMessageDialog(null, "Cannot connect to the database!");
                return transactionList; // Kembalikan list kosong jika tidak bisa terhubung
            }
    
            String query = "SELECT * FROM transactions LIMIT ?"; // Gunakan LIMIT untuk 'banyak'
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, banyak); // Set parameter limit
            rs = pstmt.executeQuery();
    
            while (rs.next()) {
                ArrayList<String> user = new ArrayList<>(); // Buat list untuk setiap transfer

                // Ambil data dari ResultSet
                id_transaction = Integer.toString(rs.getInt("transaction_id"));
                id_user = Integer.toString(rs.getInt("account_id"));
                amount = Double.toString(rs.getDouble("amount"));
                type = rs.getString("transaction_type");
                // Format tanggal
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                date = dateFormat.format(rs.getTimestamp("transaction_date")); // Menggunakan getTimestamp untuk waktu
                
                // Tambahkan data ke dalam list pengguna
                user.add(id_transaction);
                user.add(id_user);
                user.add(type);
                user.add(amount);
                user.add(date);
                
                transactionList.add(user); // Tambahkan list user ke list utama
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Tampilkan kesalahan SQL
        } finally {
            // Tutup sumber daya
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace(); // Tampilkan kesalahan penutupan
            }
        }
        return transactionList; // Kembalikan list transfer
    }
    
    

    
    
    
}
