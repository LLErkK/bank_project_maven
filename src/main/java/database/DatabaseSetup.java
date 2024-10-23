package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.DatabaseMetaData;

public class DatabaseSetup {

    private static final String URL = "jdbc:mysql://localhost:3306/bank"; // Ubah sesuai URL MySQL Anda
    private static final String USER = "root"; // Ubah dengan username MySQL Anda
    private static final String PASSWORD = ""; // Ubah dengan password MySQL Anda

    public static void main(String[] args) {
        try {
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);

            if (!tableExists(connection, "users")) {
                createUsersTable(connection);
            } else {
                System.out.println("Table 'users' already exists.");
            }

            if (!tableExists(connection, "accounts")) {
                createAccountsTable(connection);
            } else {
                System.out.println("Table 'accounts' already exists.");
            }

            if (!tableExists(connection, "transactions")) {
                createTransactionsTable(connection);
            } else {
                System.out.println("Table 'transactions' already exists.");
            }

            if (!tableExists(connection, "transfers")) {
                createTransfersTable(connection);
            } else {
                System.out.println("Table 'transfers' already exists.");
            }

            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static boolean tableExists(Connection connection, String tableName) throws SQLException {
        DatabaseMetaData meta = connection.getMetaData();
        try (var rs = meta.getTables(null, null, tableName, null)) {
            return rs.next();
        }
    }

    private static void createUsersTable(Connection connection) throws SQLException {
        String createUsersTableSQL = "CREATE TABLE users ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "username VARCHAR(50) NOT NULL UNIQUE, "
                + "password VARCHAR(255) NOT NULL, "
                + "fullname VARCHAR(100), "
                + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                + ")";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createUsersTableSQL);
            System.out.println("Table 'users' created successfully.");
        }
    }

    private static void createAccountsTable(Connection connection) throws SQLException {
        String createAccountsTableSQL = "CREATE TABLE accounts ("
                + "account_id INT AUTO_INCREMENT PRIMARY KEY, "
                + "user_id INT NOT NULL, "
                + "balance DECIMAL(15,2) DEFAULT 0.00, "
                + "no_rek VARCHAR(10) NOT NULL UNIQUE, "
                + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                + "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE"
                + ")";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createAccountsTableSQL);
            System.out.println("Table 'accounts' created successfully.");
        }
    }

    private static void createTransactionsTable(Connection connection) throws SQLException {
        String createTransactionsTableSQL = "CREATE TABLE transactions ("
                + "transaction_id INT AUTO_INCREMENT PRIMARY KEY, "
                + "account_id INT NOT NULL, "
                + "transaction_type ENUM('deposit', 'withdraw') NOT NULL, "
                + "amount DECIMAL(15,2) NOT NULL, "
                + "transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                + "FOREIGN KEY (account_id) REFERENCES accounts(account_id) ON DELETE CASCADE"
                + ")";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTransactionsTableSQL);
            System.out.println("Table 'transactions' created successfully.");
        }
    }

    private static void createTransfersTable(Connection connection) throws SQLException {
        String createTransfersTableSQL = "CREATE TABLE transfers ("
                + "transfer_id INT AUTO_INCREMENT PRIMARY KEY, "
                + "sender_account_id INT NOT NULL, "
                + "receiver_account_id INT NOT NULL, "
                + "amount DECIMAL(15,2) NOT NULL, "
                + "transfer_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                + "FOREIGN KEY (sender_account_id) REFERENCES accounts(account_id) ON DELETE CASCADE, "
                + "FOREIGN KEY (receiver_account_id) REFERENCES accounts(account_id) ON DELETE CASCADE"
                + ")";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTransfersTableSQL);
            System.out.println("Table 'transfers' created successfully.");
        }
    }
}
