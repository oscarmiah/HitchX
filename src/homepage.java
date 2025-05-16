/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

/**
 *
 * @author 63995
 */

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
public class homepage extends javax.swing.JFrame {
 
    private String username;
    private homepage homepage;
   public homepage(homepage homeage, String Username) {
    this.username = Username;
    initComponents();
        try {
            Connection();
            loadPendingDeliveries();
           loadAcceptedDeliveries();
           loadPendingDeliveries();
           loadAgencies();
        } catch (SQLException ex) {
            Logger.getLogger(homepage.class.getName()).log(Level.SEVERE, null, ex);
        }
        jLabel11.setText(username);
    loadUserData();   
   

// Load user data after initializing components
    // ... rest of your constructor code
}
   
    Connection con;
    
    // SQL Statement
    Statement st;
    //prepared statement
    PreparedStatement pst;
     ResultSet rs;
    // Required for connections
    //DbName, Driver, Url, Username, Password
    private static final String DbName = "hitchx";
    private static final String DbDriver = "com.mysql.cj.jdbc.Driver";
    private static final String DbUrl = "jdbc:mysql://localhost:3306/"+DbName;
    private static final String DbUsername = "root";
    private static final String DbPassword = "";
    
    // Create a method for connection
    public void Connection () throws SQLException{
        try {
            Class.forName(DbDriver);
            // Url, Username, Password
            con = DriverManager.getConnection(DbUrl, DbUsername, DbPassword);
            st = con.createStatement();
            if (con != null) {
                System.out.println("Connection successful");
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Registration.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

 //LOADERS 
private void loadUserData() {
        DefaultTableModel model = new DefaultTableModel(); // Initialize the model
        // Add columns to the model (you'll need to add a JTable to your form)
        model.addColumn("Name");
        model.addColumn("Username");
        model.addColumn("User Type");
        model.addColumn("Date Joined");

        try {
            String query = "SELECT name, accUsername as username, date_joined FROM users";
            pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("name"),
                    rs.getString("username"),
                    rs.getString("date_joined"),
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading client data: " + ex.getMessage(), 
                "Database Error", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(homepage.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
private void loadPendingDeliveries() {
    try {
        DefaultTableModel model = (DefaultTableModel) pendingTABLE.getModel();
        model.setRowCount(0); // Clear existing data
        
        String query = "SELECT Num_boxes, Cargo_type, Address, Status, Created_at, agency_name " +
                      "FROM delivery_form WHERE status = 'Pending' AND username = ?";
        pst = con.prepareStatement(query);
        pst.setString(1, username);
        ResultSet rs = pst.executeQuery();
        
        while (rs.next()) {
            model.addRow(new Object[]{
                rs.getString("Num_boxes"),
                rs.getString("cargo_type"),
                rs.getString("address"),
                rs.getString("status"),
                rs.getString("created_at"),
                rs.getString("agency_name")
            });
        }
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, "Error loading pending deliveries: " + ex.getMessage(), 
            "Database Error", JOptionPane.ERROR_MESSAGE);
        Logger.getLogger(homepage.class.getName()).log(Level.SEVERE, null, ex);
    }
}
private void loadAcceptedDeliveries() {
    try {
        DefaultTableModel model = (DefaultTableModel) acceptedTABLE.getModel();
        model.setRowCount(0); // Clear existing data
        
        String query = "SELECT Num_boxes, cargo_type, address, status, created_at, agency_name " +
                      "FROM delivery_form WHERE status = 'Approved' AND username = ?";
        pst = con.prepareStatement(query);
        pst.setString(1, username); // Filter by the current username
        ResultSet rs = pst.executeQuery();
        
        while (rs.next()) {
            model.addRow(new Object[]{
                rs.getString("Num_boxes"),
                rs.getString("cargo_type"),
                rs.getString("address"),
                rs.getString("status"),
                rs.getString("created_at"),
                rs.getString("agency_name")
            });
        }
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, "Error loading accepted deliveries: " + ex.getMessage(), 
            "Database Error", JOptionPane.ERROR_MESSAGE);
        Logger.getLogger(homepage.class.getName()).log(Level.SEVERE, null, ex);
    }
}
private void loadAgencies() {
    try {
        agencyBOX.removeAllItems(); // Clear existing items
        
        String query = "SELECT Agency_name FROM users";
        pst = con.prepareStatement(query);
        ResultSet rs = pst.executeQuery();
        
        while (rs.next()) {
            agencyBOX.addItem(rs.getString("Agency_name"));
        }
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, "Error loading agencies: " + ex.getMessage(), 
            "Database Error", JOptionPane.ERROR_MESSAGE);
        Logger.getLogger(homepage.class.getName()).log(Level.SEVERE, null, ex);
    }
}

//METHODS
private void submitDeliveryRequest() {
    try {
        String customerName = txtCustomername.getText();
        String numBoxes = txtNumberofcarton.getText();
        String address = txtAddress.getText();
        String message = txtDiscription.getText();
        String cargoType = cargoList.getSelectedValue();
        String selectedAgency = (String) agencyBOX.getSelectedItem();
        
        if (customerName.isEmpty() || numBoxes.isEmpty() || address.isEmpty() || cargoType == null || selectedAgency == null) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields and select a cargo type and agency", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Include the username and agency in the insert
        String query = "INSERT INTO delivery_form (Name, Num_boxes, address, Message, cargo_type, status, username, agency_name) " +
                      "VALUES (?, ?, ?, ?, ?, 'Pending', ?, ?)";
        pst = con.prepareStatement(query);
        pst.setString(1, customerName);
        pst.setString(2, numBoxes);
        pst.setString(3, address);
        pst.setString(4, message);
        pst.setString(5, cargoType);
        pst.setString(6, username);
        pst.setString(7, selectedAgency);
        pst.executeUpdate();
        
        JOptionPane.showMessageDialog(this, "Delivery request submitted successfully!");
        
        // Clear form and refresh tables
        txtCustomername.setText("");
        txtNumberofcarton.setText("");
        txtAddress.setText("");
        txtDiscription.setText("");
        loadPendingDeliveries(); // Refresh the pending deliveries table
        
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, "Error submitting request: " + ex.getMessage(), 
            "Database Error", JOptionPane.ERROR_MESSAGE);
        Logger.getLogger(homepage.class.getName()).log(Level.SEVERE, null, ex);
    }
}
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */

    @SuppressWarnings("unchecked")
    
    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        navigator = new javax.swing.JPanel();
        about = new javax.swing.JButton();
        profile = new javax.swing.JButton();
        help = new javax.swing.JButton();
        settings = new javax.swing.JButton();
        labelTXTusername = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        cargoList = new javax.swing.JList<>();
        jLabel10 = new javax.swing.JLabel();
        deliverBTN = new javax.swing.JToggleButton();
        parent = new javax.swing.JPanel();
        Deliverform = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtCustomername = new javax.swing.JTextField();
        txtNumberofcarton = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtAddress = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        SUBMIT = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtDiscription = new javax.swing.JEditorPane();
        jLabel12 = new javax.swing.JLabel();
        agencyBOX = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        Profile = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        Information = new javax.swing.JLabel();
        Firstname = new javax.swing.JLabel();
        Username = new javax.swing.JLabel();
        Lastname = new javax.swing.JLabel();
        Delivery = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        toggleBTN = new javax.swing.JToggleButton();
        ParentD = new javax.swing.JPanel();
        Accepted = new javax.swing.JPanel();
        acceptedSCL = new javax.swing.JScrollPane();
        acceptedTABLE = new javax.swing.JTable();
        Pending = new javax.swing.JPanel();
        pendingSCL = new javax.swing.JScrollPane();
        pendingTABLE = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 255));

        navigator.setBackground(new java.awt.Color(0, 51, 255));
        navigator.setForeground(new java.awt.Color(255, 255, 255));

        about.setBackground(new java.awt.Color(255, 255, 255));
        about.setFont(new java.awt.Font("Segoe UI", 0, 10)); // NOI18N
        about.setForeground(new java.awt.Color(0, 0, 0));
        about.setText("CREATE AGENCY");
        about.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutActionPerformed(evt);
            }
        });

        profile.setBackground(new java.awt.Color(255, 255, 255));
        profile.setForeground(new java.awt.Color(0, 0, 0));
        profile.setText("PROFILE");
        profile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                profileActionPerformed(evt);
            }
        });

        help.setBackground(new java.awt.Color(255, 255, 255));
        help.setForeground(new java.awt.Color(0, 0, 0));
        help.setText("HELP");
        help.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                helpActionPerformed(evt);
            }
        });

        settings.setBackground(new java.awt.Color(255, 255, 255));
        settings.setForeground(new java.awt.Color(0, 0, 0));
        settings.setText("SETTINGS");
        settings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                settingsActionPerformed(evt);
            }
        });

        labelTXTusername.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        labelTXTusername.setForeground(new java.awt.Color(255, 255, 255));
        labelTXTusername.setText("USERNAME");

        jLabel11.setFont(new java.awt.Font("Segoe UI Black", 1, 14)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("USER");

        jPanel2.setBackground(new java.awt.Color(51, 102, 255));

        cargoList.setBackground(new java.awt.Color(255, 255, 255));
        cargoList.setForeground(new java.awt.Color(0, 0, 0));
        cargoList.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Heavy Cargo", "Large Cargo", "Medium Cargo", "Small Cargo", "Light Cargo" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        cargoList.setAlignmentX(1.0F);
        cargoList.setAlignmentY(1.0F);
        cargoList.setFixedCellHeight(30);
        jScrollPane2.setViewportView(cargoList);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(253, 251, 251));
        jLabel10.setText("Type of Cargo:");

        deliverBTN.setBackground(new java.awt.Color(255, 255, 255));
        deliverBTN.setForeground(new java.awt.Color(0, 0, 0));
        deliverBTN.setText("My Deliveries");
        deliverBTN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deliverBTNActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout navigatorLayout = new javax.swing.GroupLayout(navigator);
        navigator.setLayout(navigatorLayout);
        navigatorLayout.setHorizontalGroup(
            navigatorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(navigatorLayout.createSequentialGroup()
                .addGroup(navigatorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(navigatorLayout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addGroup(navigatorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(labelTXTusername, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(navigatorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(help, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(profile, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(about, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE)
                                .addComponent(settings, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(navigatorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(deliverBTN, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(navigatorLayout.createSequentialGroup()
                        .addGap(51, 51, 51)
                        .addComponent(jLabel10)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        navigatorLayout.setVerticalGroup(
            navigatorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(navigatorLayout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(labelTXTusername)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel11)
                .addGap(18, 18, 18)
                .addComponent(deliverBTN, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(help, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(profile)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(about, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(settings, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(67, Short.MAX_VALUE))
        );

        parent.setBackground(new java.awt.Color(255, 255, 255));
        parent.setLayout(new java.awt.CardLayout());

        Deliverform.setBackground(new java.awt.Color(51, 102, 255));

        jLabel1.setBackground(new java.awt.Color(155, 190, 249));
        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Delivery Details Form");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Name");

        txtCustomername.setBackground(new java.awt.Color(255, 255, 255));
        txtCustomername.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCustomernameActionPerformed(evt);
            }
        });

        txtNumberofcarton.setBackground(new java.awt.Color(255, 255, 255));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(254, 247, 247));
        jLabel6.setText("Address:");

        txtAddress.setBackground(new java.awt.Color(255, 255, 255));

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(254, 247, 247));
        jLabel9.setText("Message(Optional)");

        SUBMIT.setBackground(new java.awt.Color(0, 51, 204));
        SUBMIT.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        SUBMIT.setForeground(new java.awt.Color(255, 255, 255));
        SUBMIT.setText("SUBMIT REQUEST");
        SUBMIT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SUBMITActionPerformed(evt);
            }
        });

        txtDiscription.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane1.setViewportView(txtDiscription);

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setText("Number of boxes");

        agencyBOX.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                agencyBOXActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Available Agency:");

        javax.swing.GroupLayout DeliverformLayout = new javax.swing.GroupLayout(Deliverform);
        Deliverform.setLayout(DeliverformLayout);
        DeliverformLayout.setHorizontalGroup(
            DeliverformLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(DeliverformLayout.createSequentialGroup()
                .addGroup(DeliverformLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(DeliverformLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1))
                    .addGroup(DeliverformLayout.createSequentialGroup()
                        .addGap(103, 103, 103)
                        .addGroup(DeliverformLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtNumberofcarton, javax.swing.GroupLayout.PREFERRED_SIZE, 378, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtAddress, javax.swing.GroupLayout.PREFERRED_SIZE, 378, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6)
                            .addComponent(jLabel12)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(DeliverformLayout.createSequentialGroup()
                                .addGroup(DeliverformLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtCustomername, javax.swing.GroupLayout.PREFERRED_SIZE, 378, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel2))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(DeliverformLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(agencyBOX, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addGap(79, 79, 79))
            .addGroup(DeliverformLayout.createSequentialGroup()
                .addGap(106, 106, 106)
                .addGroup(DeliverformLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(SUBMIT, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 424, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 291, Short.MAX_VALUE))
        );
        DeliverformLayout.setVerticalGroup(
            DeliverformLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(DeliverformLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGroup(DeliverformLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(DeliverformLayout.createSequentialGroup()
                        .addGap(47, 47, 47)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCustomername, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, DeliverformLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(agencyBOX, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtNumberofcarton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtAddress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(SUBMIT, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(112, Short.MAX_VALUE))
        );

        parent.add(Deliverform, "card3");

        Profile.setBackground(new java.awt.Color(255, 255, 255));

        jPanel1.setBackground(new java.awt.Color(51, 102, 255));

        Information.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        Information.setForeground(new java.awt.Color(255, 255, 255));
        Information.setText("Personal Information");

        Firstname.setForeground(new java.awt.Color(0, 0, 0));
        Firstname.setText("Firstname");

        Username.setForeground(new java.awt.Color(0, 0, 0));
        Username.setText("Username");

        Lastname.setForeground(new java.awt.Color(0, 0, 0));
        Lastname.setText("Lastname");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(Username, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(Information)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(Firstname, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 184, Short.MAX_VALUE)
                        .addComponent(Lastname, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(167, 167, 167))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(Information)
                .addGap(71, 71, 71)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Firstname)
                    .addComponent(Lastname))
                .addGap(86, 86, 86)
                .addComponent(Username)
                .addContainerGap(339, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout ProfileLayout = new javax.swing.GroupLayout(Profile);
        Profile.setLayout(ProfileLayout);
        ProfileLayout.setHorizontalGroup(
            ProfileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ProfileLayout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        ProfileLayout.setVerticalGroup(
            ProfileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        parent.add(Profile, "card3");

        Delivery.setBackground(new java.awt.Color(255, 255, 255));
        Delivery.setForeground(new java.awt.Color(255, 255, 255));

        jLabel3.setBackground(new java.awt.Color(0, 0, 0));
        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 0, 0));
        jLabel3.setText("Deliveries");

        toggleBTN.setBackground(new java.awt.Color(0, 102, 204));
        toggleBTN.setForeground(new java.awt.Color(255, 255, 255));
        toggleBTN.setText("ACCEPTED");
        toggleBTN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleBTNActionPerformed(evt);
            }
        });

        ParentD.setLayout(new java.awt.CardLayout());

        Accepted.setBackground(new java.awt.Color(204, 204, 204));
        Accepted.setForeground(new java.awt.Color(204, 204, 204));

        acceptedTABLE.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Num of Boxes", "Cargo Type", "Address", "Status", "Created At", "Agency"
            }
        ));
        acceptedSCL.setViewportView(acceptedTABLE);

        javax.swing.GroupLayout AcceptedLayout = new javax.swing.GroupLayout(Accepted);
        Accepted.setLayout(AcceptedLayout);
        AcceptedLayout.setHorizontalGroup(
            AcceptedLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(acceptedSCL, javax.swing.GroupLayout.DEFAULT_SIZE, 619, Short.MAX_VALUE)
        );
        AcceptedLayout.setVerticalGroup(
            AcceptedLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(acceptedSCL, javax.swing.GroupLayout.DEFAULT_SIZE, 425, Short.MAX_VALUE)
        );

        ParentD.add(Accepted, "card2");

        Pending.setBackground(new java.awt.Color(255, 102, 51));

        pendingTABLE.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Num of Boxes", "Cargo Type", "Address", "Status", "Created at", "Agency"
            }
        ));
        pendingSCL.setViewportView(pendingTABLE);

        javax.swing.GroupLayout PendingLayout = new javax.swing.GroupLayout(Pending);
        Pending.setLayout(PendingLayout);
        PendingLayout.setHorizontalGroup(
            PendingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pendingSCL, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 619, Short.MAX_VALUE)
        );
        PendingLayout.setVerticalGroup(
            PendingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pendingSCL, javax.swing.GroupLayout.DEFAULT_SIZE, 425, Short.MAX_VALUE)
        );

        ParentD.add(Pending, "card3");

        jButton1.setText("refresh");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout DeliveryLayout = new javax.swing.GroupLayout(Delivery);
        Delivery.setLayout(DeliveryLayout);
        DeliveryLayout.setHorizontalGroup(
            DeliveryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(DeliveryLayout.createSequentialGroup()
                .addGroup(DeliveryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(DeliveryLayout.createSequentialGroup()
                        .addGap(31, 31, 31)
                        .addGroup(DeliveryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(DeliveryLayout.createSequentialGroup()
                                .addComponent(toggleBTN, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jButton1))
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(DeliveryLayout.createSequentialGroup()
                        .addGap(121, 121, 121)
                        .addComponent(ParentD, javax.swing.GroupLayout.PREFERRED_SIZE, 619, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        DeliveryLayout.setVerticalGroup(
            DeliveryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(DeliveryLayout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(DeliveryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(toggleBTN, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ParentD, javax.swing.GroupLayout.PREFERRED_SIZE, 425, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(19, Short.MAX_VALUE))
        );

        parent.add(Delivery, "card4");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(navigator, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(parent, javax.swing.GroupLayout.PREFERRED_SIZE, 821, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(parent, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(navigator, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void SUBMITActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SUBMITActionPerformed
        submitDeliveryRequest();
    }//GEN-LAST:event_SUBMITActionPerformed

    private void txtCustomernameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCustomernameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCustomernameActionPerformed

    private void helpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_helpActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_helpActionPerformed

    private void profileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_profileActionPerformed
        // TODO add your handling code here:

        parent.removeAll();
        parent.add(Profile);
        parent.repaint();
        parent.revalidate();

    }//GEN-LAST:event_profileActionPerformed

    private void aboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutActionPerformed
        // TODO add your handling code here:
        try {
            String query = "SELECT * FROM users WHERE accUsername = ?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, username);
            ResultSet rs = pst.executeQuery();

            if (rs.next() && rs.getString("Agency_name") != null) {
                // If a store is found, open SellerPage with all required parameters
                String agencyname = rs.getString("Agency_name");
                int userid = rs.getInt("User_id");
                String address = rs.getString("Address");
                String contact = rs.getString("Contact");

                Dashboard dashboard = new Dashboard(
                        agencyname,
                        username,
                        address,
                        userid,
                        contact
                );
                dashboard.setVisible(true);
                dashboard.pack();
                dashboard.setLocationRelativeTo(null);
            } else {
                // If no store is found, ask if they want to register
                int confirm = JOptionPane.showConfirmDialog(
                        this,
                        "Ther is no Agency Registered in your name. Would you like to register now?",
                        "Not Registered",
                        JOptionPane.YES_NO_OPTION
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    // Open the SellerRegistration form
                    AgencyReg reg = new AgencyReg(this, username);
                    reg.setVisible(true);
                    reg.pack();
                    reg.setLocationRelativeTo(null);
                }
            }
            rs.close();
            pst.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Database error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_aboutActionPerformed

    private void toggleBTNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toggleBTNActionPerformed
        // TODO add your handling code here:
   if (toggleBTN.isSelected()) {
       ParentD.removeAll();
       ParentD.add(Pending);
       ParentD.repaint();
       ParentD.revalidate();
        loadPendingDeliveries();
        toggleBTN.setText("PENDING");
        
    } else {
        // Show Accepted deliveries
        ParentD.removeAll();
       ParentD.add(Accepted);
       ParentD.repaint();
       ParentD.revalidate();
        loadAcceptedDeliveries();
        toggleBTN.setText("APPROVED");
    }
    }//GEN-LAST:event_toggleBTNActionPerformed

    private void deliverBTNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deliverBTNActionPerformed
        // TODO add your handling code here:
        if (deliverBTN.isSelected()) {
                    // Toggled ON
                    // Add your custom logic here
                     parent.removeAll();
                     parent.add(Deliverform);
                     parent.repaint();
                     parent.revalidate();
                    
                } else {
                    // Toggled OFF
                    // Add your custom logic here
                    parent.removeAll();
                     parent.add(Delivery);
                     parent.repaint();
                     parent.revalidate();
                }
    }//GEN-LAST:event_deliverBTNActionPerformed

    private void settingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_settingsActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_settingsActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_jButton1ActionPerformed

    private void agencyBOXActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_agencyBOXActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_agencyBOXActionPerformed

    /**
     * @param args the command line arguments
     */
    public void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(homepage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(homepage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(homepage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(homepage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                String Username = "Username";
               new homepage(homepage, Username).setVisible(true);
            }
        });
    }
        

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel Accepted;
    private javax.swing.JPanel Deliverform;
    private javax.swing.JPanel Delivery;
    private javax.swing.JLabel Firstname;
    private javax.swing.JLabel Information;
    private javax.swing.JLabel Lastname;
    private javax.swing.JPanel ParentD;
    private javax.swing.JPanel Pending;
    private javax.swing.JPanel Profile;
    private javax.swing.JButton SUBMIT;
    private javax.swing.JLabel Username;
    private javax.swing.JButton about;
    private javax.swing.JScrollPane acceptedSCL;
    private javax.swing.JTable acceptedTABLE;
    private javax.swing.JComboBox<String> agencyBOX;
    private javax.swing.JList<String> cargoList;
    private javax.swing.JToggleButton deliverBTN;
    private javax.swing.JButton help;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel labelTXTusername;
    private javax.swing.JPanel navigator;
    private javax.swing.JPanel parent;
    private javax.swing.JScrollPane pendingSCL;
    private javax.swing.JTable pendingTABLE;
    private javax.swing.JButton profile;
    private javax.swing.JButton settings;
    private javax.swing.JToggleButton toggleBTN;
    private javax.swing.JTextField txtAddress;
    private javax.swing.JTextField txtCustomername;
    private javax.swing.JEditorPane txtDiscription;
    private javax.swing.JTextField txtNumberofcarton;
    // End of variables declaration//GEN-END:variables

}
    

