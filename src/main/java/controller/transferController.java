package controller;

import koneksi.koneksi;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import javax.swing.JOptionPane;
public class transferController {
    //mengvalidasi apakah nomor rekeningnya dan apakah saldo cukup
    //jika true maka mengurangi saldo pengirim dan menambah saldo penerima
    private int id;
//    koneksi conn = new koneksi();
    private Connection conn;

    public transferController(int id) {
        this.id = id;
        this.conn = new koneksi().connect();
    }

    public boolean validTransfer(String rekening,double nominal){
        String cekSaldo = "SELECT balance FROM accounts WHERE user_id = ?";
        String cekRekening = "SELECT 1 FROM accounts WHERE rekening = ?";

        try {
            // Mengecek saldo pengirim
            PreparedStatement pstSelectBalance = conn.prepareStatement(cekSaldo);
            pstSelectBalance.setInt(1, this.id);
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
                pstSelectNoRek.setString(1, rekening);
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

    public void actionTransfer(String rekening,double nominal){
        if(validTransfer(rekening,nominal)){
            String sender = "UPDATE accounts SET balance = ? WHERE user_id = ?";
            String receiver = "UPDATE accounts SET balance = ? WHERE rekening = ?";
            String getSenderBalance = "SELECT balance FROM accounts WHERE user_id = ? FOR UPDATE";
            String getReceiverBalance = "SELECT balance FROM accounts WHERE rekening = ? FOR UPDATE";

            try {
                // Mulai transaksi dengan menonaktifkan auto-commit
                conn.setAutoCommit(false);

                // Mendapatkan saldo pengirim dan mengunci barisnya
                PreparedStatement pstGetSenderBalance = conn.prepareStatement(getSenderBalance);
                pstGetSenderBalance.setInt(1, this.id);
                ResultSet rsSend = pstGetSenderBalance.executeQuery();
                double balanceSender = 0;
                if (rsSend.next()) {
                    balanceSender = rsSend.getDouble("balance");
                }

                // Mendapatkan saldo penerima dan mengunci barisnya
                PreparedStatement pstGetReceiverBalance = conn.prepareStatement(getReceiverBalance);
                pstGetReceiverBalance.setString(1, rekening);
                ResultSet rsReceive = pstGetReceiverBalance.executeQuery();
                double balanceReceiver = 0;
                if (rsReceive.next()) {
                    balanceReceiver = rsReceive.getDouble("balance");
                }

                // Update saldo pengirim
                PreparedStatement pstSetSenderBalance = conn.prepareStatement(sender);
                pstSetSenderBalance.setDouble(1, balanceSender - nominal);
                pstSetSenderBalance.setInt(2, this.id);
                pstSetSenderBalance.executeUpdate();

                // Update saldo penerima
                PreparedStatement pstSetReceiverBalance = conn.prepareStatement(receiver);
                pstSetReceiverBalance.setDouble(1, balanceReceiver + nominal);
                pstSetReceiverBalance.setString(2, rekening);
                pstSetReceiverBalance.executeUpdate();

                // Commit transaksi
                conn.commit();

                // Mencatat riwayat transfer
                TransferHistory( rekening, nominal);
            } catch (Exception e) {
                try {
                    // Rollback jika terjadi error
                    conn.rollback();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Rollback Error: " + ex.getMessage());
                }
                JOptionPane.showMessageDialog(null, "Transfer Error: " + e.getMessage());
            } finally {
                try {
                    // Mengaktifkan kembali auto-commit dan menutup koneksi
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(null, "Connection Close Error: " + e.getMessage());
                }
            }

        }
    }
    private void TransferHistory(String rekening, double nominal){

        int id_receiver;

        try {


            // Disable auto-commit to manage the transaction manually
            conn.setAutoCommit(false);

            // Mendapatkan id receiver
            String getIdReceiver = "SELECT account_id FROM accounts WHERE rekening = ?";
            PreparedStatement pst = conn.prepareStatement(getIdReceiver);
            pst.setString(1, rekening);
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
            pstUpdate.setInt(1, this.id);
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

}
