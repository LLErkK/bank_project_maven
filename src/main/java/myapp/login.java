package myapp;

import javax.swing.JOptionPane;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class login extends javax.swing.JFrame {

    public login() {
        initComponents();
        setLocationRelativeTo(null); // Pusatkan window
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        jPasswordField1 = new javax.swing.JPasswordField();
        jLabel1 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        button1 = new java.awt.Button();
        button2 = new java.awt.Button();
        jToggleButton1 = new javax.swing.JToggleButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        // Password field initial setup
        jPasswordField1.setText("");
        jPasswordField1.setEchoChar('\u2022'); // Gunakan bullet untuk menyembunyikan password
        
        // Labels
        jLabel1.setText("Password");
        jLabel2.setText("Username");

        // Username field setup
        jTextField2.setText("");
        jTextField2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField2ActionPerformed(evt);
            }
            

        });
        jTextField2.addKeyListener(new KeyAdapter() {
        public void keyTyped(KeyEvent e){
            char karakter = e.getKeyChar();
            if(Character.isWhitespace(karakter)){
                e.consume();
            }
        }
        });
        jPasswordField1.addKeyListener(new KeyAdapter() {
            public  void keyTyped(KeyEvent e){
                char karakter = e.getKeyChar();
                if(Character.isWhitespace(karakter)){
                    e.consume();
                }
            }
        });

        // Login button
        button1.setLabel("Login");
        button1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button1ActionPerformed(evt);
            }
        });

        // Register button
        button2.setLabel("Daftar");
        button2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button2ActionPerformed(evt);
            }
        });
        jTextField2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                jPasswordField1.requestFocus();
            }
            });

        // Toggle button untuk menampilkan/menyembunyikan password
        jToggleButton1.setText("Show");
        jToggleButton1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if (jToggleButton1.isSelected()) {
                    jPasswordField1.setEchoChar((char) 0);  // Tampilkan password
                    jToggleButton1.setText("Hide");
                } else {
                    jPasswordField1.setEchoChar('\u2022');  // Sembunyikan password
                    jToggleButton1.setText("Show");
                }
            }
        });

        // Layout settings
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(121, 121, 121)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jTextField2)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPasswordField1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jToggleButton1))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(button1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(114, 114, 114))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(69, 69, 69)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jPasswordField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jToggleButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(button2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(103, Short.MAX_VALUE))
        );

        pack(); // Adjust window size automatically
    }

    private void jTextField2ActionPerformed(java.awt.event.ActionEvent evt) {
        // Optional: add handling code if needed
    }

    private void button1ActionPerformed(java.awt.event.ActionEvent evt) {
        // Login button pressed
        String username = jTextField2.getText();
        String password = new String(jPasswordField1.getPassword());

        handler handler = new handler(); // Inisialisasi handler dengan ID default 0
        if (handler.login(username, password)) {
            JOptionPane.showMessageDialog(this, "Login berhasil!");
            handler session = handler.getInstance(0);
            int id = handler.getid();
            new menu(id).setVisible(true); // Tampilkan menu
            dispose(); // Tutup jendela login
        } else {
            JOptionPane.showMessageDialog(this, "Login gagal. Silakan coba lagi.");
        }
    }

    private void button2ActionPerformed(java.awt.event.ActionEvent evt) {
        // Register button pressed
        new daftar().setVisible(true); // Tampilkan form daftar
        dispose(); // Tutup jendela login
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new login().setVisible(true); // Jalankan form login
            }
        });
    }

    // Variables declaration
    private java.awt.Button button1;
    private java.awt.Button button2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPasswordField jPasswordField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JToggleButton jToggleButton1;
    // End of variables declaration
}
