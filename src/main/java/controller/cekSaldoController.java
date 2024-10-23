package controller;

import model.getAccountData;

public class cekSaldoController {
    private double interest = 0.05;
    private int id;
    private getAccountData getAccount;

    public cekSaldoController(int id){
        this.id = id;
        this.getAccount = new getAccountData(this.id);  // inisialisasi di sini
    }



    public double getInterestSaldo(int bulan){

        double balance,bunga;
        balance = getAccount.getBalance();
        bunga = balance * (interest / 100) * ((double)bulan / 12);
        balance +=bunga;
        System.out.println("Saldo "+balance);
        return balance;
    }
    public double getSaldo(){
        System.out.println("Saldo "+getAccount.getBalance());
        return getAccount.getBalance();
    }

}
