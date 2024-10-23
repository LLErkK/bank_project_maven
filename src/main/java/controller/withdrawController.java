package controller;
import koneksi.koneksi;
import model.getAccountData;
import java.sql.Connection;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class withdrawController {
    //validasi apakah nominal nya tidak invalid(negatif atau 0)
    // dan lebih dari balance
    //jika valid maka update ke database
    //update history
    private int id;
    private Connection conn;

    public withdrawController(int id) {
        this.id = id;
        this.conn = new koneksi().connect();
    }

    public boolean isWithdrawValid ( double nominal){
        getAccountData gad = new getAccountData(this.id);
        double balance = gad.getBalance();
        if(nominal<=balance && nominal>0){
            updateBalance(nominal);
            return true;
        }
        return false;
    }

    private void updateBalance(double nominal){
        String query="SELECT * FROM accounts WHERE user_id = ?";
        String updateQuery="UPDATE accounts SET balance = ? WHERE user_id =?";
        try {
            PreparedStatement pStSelect = conn.prepareStatement(query);
            pStSelect.setInt(1, this.id);
            ResultSet rs = pStSelect.executeQuery();
            if(rs.next()){
                double balance = rs.getDouble("balance");
                if(nominal <= balance){
                    balance -= nominal;
                    PreparedStatement pstUpdate = conn.prepareStatement(updateQuery);
                    pstUpdate.setDouble(1, balance);
                    pstUpdate.setInt(2, this.id);
                    pstUpdate.executeUpdate();

                    updateHistory( "withdraw", nominal);
                }else{
                    JOptionPane.showMessageDialog(null, "Saldo Tidak Cukup!");
                }

            }
        } catch (Exception e) {
            System.out.println("eror: "+e.getMessage());
        }
    }




    private void updateHistory(String Type,double nominal)
    {
        String query="INSERT INTO transactions (account_id, transaction_type, amount) VALUES(?,?,?)";
        try {
            PreparedStatement pStUpdate = conn.prepareStatement(query);
            pStUpdate.setInt(1, this.id);
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
}
