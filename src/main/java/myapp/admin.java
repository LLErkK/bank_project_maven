package myapp;

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class admin extends JFrame {
    Operasi operasi = new Operasi();
    private JComboBox<String> tableSelector;
    private JComboBox<String> columnSelector;
    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private int banyak;
    public admin() {

        setTitle("Database Table Viewer");
        setSize(1200, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Top panel for selecting table and columns
        JPanel topPanel = new JPanel();
        tableSelector = new JComboBox<>();
        columnSelector = new JComboBox<>();
        JButton loadButton = new JButton("Load Data");
        JTextField input = new JTextField(10); 
        input.addKeyListener(new KeyAdapter() {
        @Override
        public void keyTyped(KeyEvent e){
            char karakter = e.getKeyChar();
            if(!Character.isDigit(karakter)){
                e.consume();
            }
        }
        });
        //menerima inputan user jika kosong maka defaultnya 5
        this.banyak = input.getText().isEmpty() ? 5 : Integer.parseInt(input.getText());
        // Buttons for sorting
        JButton sortAscButton = new JButton("Sort Ascending");
        JButton sortDescButton = new JButton("Sort Descending");

        topPanel.add(new JLabel("Select Table:"));
        topPanel.add(tableSelector);
        topPanel.add(new JLabel("Select Column:"));
        topPanel.add(columnSelector);
        topPanel.add(input);
        topPanel.add(loadButton);
        topPanel.add(sortAscButton);
        topPanel.add(sortDescButton);

        add(topPanel, BorderLayout.NORTH);

        // Table to display data
        tableModel = new DefaultTableModel();
        table = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);
        
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Adding example tables
        tableSelector.addItem("Transaksi");
        tableSelector.addItem("Transfer");
        tableSelector.addItem("Account");

        // Listener to update column selector based on table selection
        tableSelector.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    updateColumnSelector(tableSelector.getSelectedItem().toString());
                }
            }
        });

        // Load data action listener
        loadButton.addActionListener(e -> {
           // Update the 'banyak' variable based on the input field
            String inputText = input.getText();
            this.banyak = inputText.isEmpty() ? 5 : Integer.parseInt(inputText);
    
            String selectedTable = (String) tableSelector.getSelectedItem();
            if (selectedTable != null) {
                loadData(selectedTable);
            }
        });

        // Sort buttons action listeners
        sortAscButton.addActionListener(e -> {
           
        });

        sortDescButton.addActionListener(e -> {
            
        });
    }
    //menampilkan kolom apa saja yang ada di table tersebut
    //masih belum dinamis
    //dan belum berguna
    private void updateColumnSelector(String selectedTable) {
        columnSelector.removeAllItems();
        if ("Transaksi".equals(selectedTable)) {
            columnSelector.addItem("ID Transaksi");
            columnSelector.addItem("Tanggal");
            columnSelector.addItem("Jumlah");
        } else if ("Transfer".equals(selectedTable)) {
            columnSelector.addItem("ID Transfer");
            columnSelector.addItem("Akun Pengirim");
            columnSelector.addItem("Akun Penerima");
            columnSelector.addItem("Jumlah");
        }
    }
    //menetukan table apa yang akan ditampilkan
    private void loadData(String selectedTable) {
        tableModel.setRowCount(0);
        tableModel.setColumnCount(0);
        if ("Transaksi".equals(selectedTable)) {
            tableModel.setColumnIdentifiers(new String[]{"ID Transaksi", "ID User", "Tipe","Jumlah","Tanggal"});
            ArrayList<ArrayList<String>> transaksiData = operasi.getTransaction(banyak);
            for (ArrayList<String> row : transaksiData) {
                tableModel.addRow(row.toArray());
            }
        } else if ("Transfer".equals(selectedTable)) {
            tableModel.setColumnIdentifiers(new String[]{"ID Transfer", "Akun Pengirim", "Akun Penerima", "Jumlah"});
            ArrayList<ArrayList<String>> transaksiData = operasi.getTransfer(banyak);
            for (ArrayList<String> row : transaksiData) {
                tableModel.addRow(row.toArray());
            }
        } else if ("Account".equals(selectedTable)) {
            tableModel.setColumnIdentifiers(new String[]{"ID", "Username", "Password", "fullname", "balance","No.Rek", "tanggal Dibuat"});
            ArrayList<ArrayList<String>> akun = operasi.getUsers(banyak);
            for (ArrayList<String> row : akun) {
                tableModel.addRow(row.toArray());
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new admin().setVisible(true);
        });
    }
}
