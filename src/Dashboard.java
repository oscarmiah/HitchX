/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
import java.sql.*;
import java.util.logging.*;
import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class Dashboard extends javax.swing.JFrame {
private String name;
private String username;
private String address;
private String contact;
private int user_id;

private Connection con = null;
private Statement st = null;
private PreparedStatement pst;
    



 
    
public Dashboard(String Name, String Username, String Address, int User_id, String Contact) {
        this.name = Name;
        this.username = Username;
        this.address = Address;
        this.user_id = User_id;
        this.contact = Contact;
        initComponents();
        
requestSCROLL.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
requestSCROLL.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
requestTAB.addTab("Requests", requestSCROLL); // Use BorderLayout for the main panel
        initializeCustomComponents();
        table.setModel(new DefaultTableModel(
        new Object[][]{}, 
        new String[]{"Name", "Username", "User Type", "Joined"}
    )); 
        
        updateDateTime();
        try {
            Connection();
            loadUserData();
        } catch (SQLException ex) {
            Logger.getLogger(Dashboard.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

      
// Database configuration
private static final String DbName = "hitchx";
private static final String DbDriver = "com.mysql.cj.jdbc.Driver";
private static final String DbUrl = "jdbc:mysql://localhost:3306/" + DbName;
private static final String DbUsername = "root";
private static final String DbPassword = "";
public void Connection () throws SQLException{
        try {
            Class.forName(DbDriver);
            // Url, Username, Password
            con = java.sql.DriverManager.getConnection(DbUrl, DbUsername, DbPassword);
            st = con.createStatement();
            if (con != null) {
                System.out.println("Connection successful");
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Registration.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
      
private void updateDateTime() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, d'%s' MMMM yyyy - hh:mm:ss a");
                    String daySuffix = getDayNumberSuffix(new Date());
                    String formattedDate = String.format(dateFormat.format(new Date()), daySuffix);
                    jLabel26.setText(formattedDate);
                });
            }
        }, 0, 1000);
    }
private void initializeCustomComponents() {
        requestTAB = new javax.swing.JTabbedPane();
        requestTAB.setTabPlacement(JTabbedPane.TOP);
        requestTAB.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        jLabel26.setFont(new java.awt.Font("Segoe UI", 0, 14));
        jLabel26.setForeground(new java.awt.Color(0, 0, 0));
        jLabel26.setText("Loading date/time...");
        adminNameTXT.setText(name);
    }
private String getDayNumberSuffix(Date date) {
        SimpleDateFormat dayFormat = new SimpleDateFormat("d");
        int day = Integer.parseInt(dayFormat.format(date));
        if (day >= 11 && day <= 13) return "th";
        switch (day % 10) {
            case 1: return "st";
            case 2: return "nd";
            case 3: return "rd";
            default: return "th";
        }
    }

//METHIODS
private void addFormField(JPanel panel, String label, String value) {
    JPanel fieldPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    fieldPanel.setBackground(Color.WHITE);
    
    JLabel labelLbl = new JLabel(label + " ");
    labelLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
    fieldPanel.add(labelLbl);
    
    JLabel valueLbl = new JLabel(value);
    valueLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    fieldPanel.add(valueLbl);
    
    panel.add(fieldPanel);
    panel.add(Box.createVerticalStrut(5));
}
private String formatDate(String dbDate) {
    try {
        SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat displayFormat = new SimpleDateFormat("MMMM d yyyy");
        return displayFormat.format(dbFormat.parse(dbDate));
    } catch (Exception e) {
        return dbDate; // Return original if parsing fails
    }
}
private void updateRequestStatus(int requestId, String status) {
    try {
        String query = "UPDATE delivery_form SET status = ? WHERE request_id = ?";
        pst = con.prepareStatement(query);
        pst.setString(1, status);
        pst.setInt(2, requestId);
        pst.executeUpdate();
        
        // Refresh the display
        loadDeliveryRequests();
    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error updating status");
    }
}


//LOADERS
private void loadUserData() {
    try {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        
        // Simple test query - remove WHERE clause to see all users
        String query = "SELECT Name, accUsername, Date_joined  FROM users LIMIT 10";
        ResultSet rs = st.executeQuery(query);
        
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        
        // Print column names for debugging
        for (int i = 1; i <= columnCount; i++) {
            System.out.println("Column " + i + ": " + metaData.getColumnName(i));
        }
        
        while (rs.next()) {
            Object[] row = new Object[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                row[i-1] = rs.getObject(i);
            }
            model.addRow(row);
        }
        
        rs.close();
    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
    }
}
private void loadDeliveryRequests() {
    try {
        // Create container panel
        JPanel cardsPanel = new JPanel();
        cardsPanel.setLayout(new BoxLayout(cardsPanel, BoxLayout.Y_AXIS));
        cardsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Get data from database
        String query = "SELECT * FROM delivery_form WHERE status = 'Pending' ORDER BY created_at DESC";
        ResultSet rs = st.executeQuery(query);
        
        while (rs.next()) {
            int requestId = rs.getInt("request_id");
            String name = rs.getString("Name");
            String boxes = rs.getString("Num_boxes");
            String cargoType = rs.getString("Cargo_type");
            String message = rs.getString("Message");
            String date = rs.getString("created_at");
            
            // Create card for each request
            JPanel card = createRequestCard(name, boxes, cargoType, message, date, requestId);
            cardsPanel.add(card);
            cardsPanel.add(Box.createVerticalStrut(10)); // Add spacing between cards
        }
        
        // Wrap in scroll pane
        requestSCROLL.setViewportView(cardsPanel);
        requestSCROLL.getVerticalScrollBar().setUnitIncrement(16);
        
    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error loading requests: " + ex.getMessage());
    }
}
private void loadDeliveryAccepted() {
    try {
        // Create container panel
        JPanel cardsPanel = new JPanel();
        cardsPanel.setLayout(new BoxLayout(cardsPanel, BoxLayout.Y_AXIS));
        cardsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Get data from database
        String query = "SELECT * FROM delivery_form WHERE status = 'Accepted' ORDER BY created_at DESC";
        ResultSet rs = st.executeQuery(query);
        
        while (rs.next()) {
            int requestId = rs.getInt("request_id");
            String name = rs.getString("Name");
            String boxes = rs.getString("Num_boxes");
            String cargoType = rs.getString("Cargo_type");
            String message = rs.getString("Message");
            String date = rs.getString("created_at");
            
            // Create card for each request
            JPanel card = createRequestCard(name, boxes, cargoType, message, date, requestId);
            cardsPanel.add(card);
            cardsPanel.add(Box.createVerticalStrut(10)); // Add spacing between cards
        }
        
        // Wrap in scroll pane
        requestSCROLL.setViewportView(cardsPanel);
        requestSCROLL.getVerticalScrollBar().setUnitIncrement(16);
        
    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error loading requests: " + ex.getMessage());
    }
}



// CREATORS
private JPanel createRequestCard(String name, String boxes, String cargoType, 
                               String message, String date, int requestId) {
    JPanel card = new JPanel();
    card.setLayout(new BorderLayout(10, 10));
    card.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(0xEEEEEE)),
        BorderFactory.createEmptyBorder(15, 15, 15, 15)
    ));
    card.setBackground(Color.WHITE);
    card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

    // Date label (top right)
    JLabel dateLabel = new JLabel("Date Requested: " + formatDate(date));
    dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    dateLabel.setHorizontalAlignment(SwingConstants.RIGHT);
    card.add(dateLabel, BorderLayout.NORTH);

    // Main content
    JPanel contentPanel = new JPanel();
    contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
    contentPanel.setBackground(Color.WHITE);
    
    addFormField(contentPanel, "Name:", name);
    addFormField(contentPanel, "Num of Boxes:", boxes);
    addFormField(contentPanel, "Ship Type:", cargoType);
    
    contentPanel.add(Box.createVerticalStrut(10));
    
    // Message (italic)
    JLabel messageLabel = new JLabel("<html><i>" + message + "</i></html>");
    messageLabel.setFont(new Font("Segoe UI", Font.ITALIC, 13));
    contentPanel.add(messageLabel);
    
    card.add(contentPanel, BorderLayout.CENTER);

    // Action buttons
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    buttonPanel.setBackground(Color.WHITE);
    
    JButton rejectBtn = createActionButton("REJECT", new Color(244, 67, 54));
    rejectBtn.addActionListener(e -> updateRequestStatus(requestId, "Rejected"));
    
    JButton confirmBtn = createActionButton("CONFIRM", new Color(76, 175, 80));
    confirmBtn.addActionListener(e -> updateRequestStatus(requestId, "Approved"));
    
    buttonPanel.add(rejectBtn);
    buttonPanel.add(confirmBtn);
    
    card.add(buttonPanel, BorderLayout.SOUTH);

    return card;
}
private JButton createActionButton(String text, Color bgColor) {
    JButton button = new JButton(text);
    button.setBackground(bgColor);
    button.setForeground(Color.WHITE);
    button.setFocusPainted(false);
    button.setPreferredSize(new Dimension(100, 30));
    return button;
}
//DISPLAYER



    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        parent = new javax.swing.JPanel();
        Dashboard = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        Shipping = new javax.swing.JPanel();
        Clients = new javax.swing.JPanel();
        Requests = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        requestTAB = new javax.swing.JTabbedPane();
        requestSCROLL = new javax.swing.JScrollPane();
        navigator = new javax.swing.JPanel();
        requestBTN = new javax.swing.JButton();
        shippinBTN = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        adminNameTXT = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        dashboardBTN = new javax.swing.JButton();
        LogoutBTN = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        parent.setLayout(new java.awt.CardLayout());

        Dashboard.setBackground(new java.awt.Color(255, 255, 255));
        Dashboard.setForeground(new java.awt.Color(17, 14, 167));

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Name", "Username", "User type", "Joined"
            }
        ));
        jScrollPane2.setViewportView(table);

        jPanel3.setBackground(new java.awt.Color(155, 190, 249));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(17, 14, 167));
        jLabel6.setText("Admin Overview");

        jLabel26.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel26.setForeground(new java.awt.Color(0, 0, 0));
        jLabel26.setText("Sunday,26th May 2025");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addComponent(jLabel6))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel26)))
                .addContainerGap(172, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel26)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel6)
                .addGap(118, 118, 118))
        );

        javax.swing.GroupLayout DashboardLayout = new javax.swing.GroupLayout(Dashboard);
        Dashboard.setLayout(DashboardLayout);
        DashboardLayout.setHorizontalGroup(
            DashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, DashboardLayout.createSequentialGroup()
                .addContainerGap(37, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 845, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(35, 35, 35))
            .addGroup(DashboardLayout.createSequentialGroup()
                .addGap(132, 132, 132)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        DashboardLayout.setVerticalGroup(
            DashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(DashboardLayout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        parent.add(Dashboard, "card2");

        Shipping.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout ShippingLayout = new javax.swing.GroupLayout(Shipping);
        Shipping.setLayout(ShippingLayout);
        ShippingLayout.setHorizontalGroup(
            ShippingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 917, Short.MAX_VALUE)
        );
        ShippingLayout.setVerticalGroup(
            ShippingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 620, Short.MAX_VALUE)
        );

        parent.add(Shipping, "card3");

        Clients.setBackground(new java.awt.Color(153, 153, 153));
        Clients.setForeground(new java.awt.Color(204, 204, 204));

        javax.swing.GroupLayout ClientsLayout = new javax.swing.GroupLayout(Clients);
        Clients.setLayout(ClientsLayout);
        ClientsLayout.setHorizontalGroup(
            ClientsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 917, Short.MAX_VALUE)
        );
        ClientsLayout.setVerticalGroup(
            ClientsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 620, Short.MAX_VALUE)
        );

        parent.add(Clients, "card5");

        Requests.setBackground(new java.awt.Color(204, 204, 204));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 48)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 0, 0));
        jLabel2.setText("Delivery Requests");

        requestTAB.addTab("Requests", requestSCROLL);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(requestTAB, javax.swing.GroupLayout.DEFAULT_SIZE, 887, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(requestTAB, javax.swing.GroupLayout.DEFAULT_SIZE, 494, Short.MAX_VALUE)
        );

        requestTAB.getAccessibleContext().setAccessibleName("Requests");

        javax.swing.GroupLayout RequestsLayout = new javax.swing.GroupLayout(Requests);
        Requests.setLayout(RequestsLayout);
        RequestsLayout.setHorizontalGroup(
            RequestsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(RequestsLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(RequestsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addContainerGap(16, Short.MAX_VALUE))
        );
        RequestsLayout.setVerticalGroup(
            RequestsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(RequestsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(37, 37, 37)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(28, Short.MAX_VALUE))
        );

        parent.add(Requests, "card6");

        navigator.setBackground(new java.awt.Color(66, 133, 244));

        requestBTN.setBackground(new java.awt.Color(255, 255, 255));
        requestBTN.setForeground(new java.awt.Color(0, 0, 0));
        requestBTN.setText("REQUEST");
        requestBTN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                requestBTNActionPerformed(evt);
            }
        });

        shippinBTN.setBackground(new java.awt.Color(255, 255, 255));
        shippinBTN.setForeground(new java.awt.Color(0, 0, 0));
        shippinBTN.setText("SHIPPING");
        shippinBTN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                shippinBTNActionPerformed(evt);
            }
        });

        jButton4.setBackground(new java.awt.Color(255, 255, 255));
        jButton4.setForeground(new java.awt.Color(0, 0, 0));
        jButton4.setText("MESSAGE");

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 0, 0));
        jLabel1.setText("MENU");

        jLabel27.setIcon(new javax.swing.ImageIcon("C:\\Users\\63995\\Documents\\1ST YEAR BSCS(2ND SEM)\\hitchx assets\\Screenshot-2025-05-03-020200.jpg")); // NOI18N
        jLabel27.setText("jLabel27");

        adminNameTXT.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        adminNameTXT.setForeground(new java.awt.Color(255, 255, 255));
        adminNameTXT.setText("ADMIN NAME");

        jLabel28.setText("Admin");

        dashboardBTN.setBackground(new java.awt.Color(255, 255, 255));
        dashboardBTN.setForeground(new java.awt.Color(0, 0, 0));
        dashboardBTN.setText("DASHBOARD");
        dashboardBTN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dashboardBTNActionPerformed(evt);
            }
        });

        LogoutBTN.setBackground(new java.awt.Color(255, 255, 255));
        LogoutBTN.setForeground(new java.awt.Color(0, 0, 0));
        LogoutBTN.setText("LOG OUT");
        LogoutBTN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LogoutBTNActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout navigatorLayout = new javax.swing.GroupLayout(navigator);
        navigator.setLayout(navigatorLayout);
        navigatorLayout.setHorizontalGroup(
            navigatorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, navigatorLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(navigatorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(LogoutBTN, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(adminNameTXT, javax.swing.GroupLayout.PREFERRED_SIZE, 211, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(requestBTN, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(navigatorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(dashboardBTN, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 122, Short.MAX_VALUE)
                        .addComponent(shippinBTN, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(89, 89, 89))
            .addGroup(navigatorLayout.createSequentialGroup()
                .addGap(107, 107, 107)
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        navigatorLayout.setVerticalGroup(
            navigatorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(navigatorLayout.createSequentialGroup()
                .addGap(79, 79, 79)
                .addGroup(navigatorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel27)
                    .addGroup(navigatorLayout.createSequentialGroup()
                        .addComponent(adminNameTXT)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel28)))
                .addGap(18, 18, 18)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(dashboardBTN, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(shippinBTN, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(requestBTN, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 130, Short.MAX_VALUE)
                .addComponent(LogoutBTN, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(navigator, javax.swing.GroupLayout.PREFERRED_SIZE, 297, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 928, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(302, 302, 302)
                    .addComponent(parent, javax.swing.GroupLayout.PREFERRED_SIZE, 917, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(navigator, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(parent, javax.swing.GroupLayout.PREFERRED_SIZE, 620, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 1, Short.MAX_VALUE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    //ACTIONS
    private void dashboardBTNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dashboardBTNActionPerformed
        // TODO add your handling code here:
        
       parent.removeAll();
       parent.add(Dashboard);
       parent.repaint();
       parent.revalidate();
    }//GEN-LAST:event_dashboardBTNActionPerformed
    private void shippinBTNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_shippinBTNActionPerformed
        // TODO add your handling code here:
       parent.removeAll();
       parent.add(Shipping);
       parent.repaint();
       parent.revalidate();
    }//GEN-LAST:event_shippinBTNActionPerformed
    private void LogoutBTNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LogoutBTNActionPerformed
        // TODO add your handling code here:
        int confirm = JOptionPane.showConfirmDialog(
            this.Dashboard,
            "Are you sure you want to LOG OUT?",
            "Confirm LOG OUT",
            JOptionPane.YES_NO_OPTION);
         if (confirm == JOptionPane.YES_OPTION) {
            Login dashboard = new Login();
        dashboard.setVisible(true);
        dashboard.pack();
        dashboard.setLocationRelativeTo(null);
        this.dispose();
        }
  
    }//GEN-LAST:event_LogoutBTNActionPerformed
    private void requestBTNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_requestBTNActionPerformed
                                                                                        
    parent.removeAll();
    parent.add(Requests);
    parent.repaint();
    parent.revalidate();
    loadDeliveryRequests();
    }//GEN-LAST:event_requestBTNActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
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
            java.util.logging.Logger.getLogger(Dashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Dashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Dashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Dashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                String Name = null, Username = null, Address = null, Contact = null;
                int User_id = 0;
                new Dashboard(Name, Username, Address, User_id, Contact).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel Clients;
    private javax.swing.JPanel Dashboard;
    private javax.swing.JButton LogoutBTN;
    private javax.swing.JPanel Requests;
    private javax.swing.JPanel Shipping;
    private javax.swing.JLabel adminNameTXT;
    private javax.swing.JButton dashboardBTN;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPanel navigator;
    private javax.swing.JPanel parent;
    private javax.swing.JButton requestBTN;
    private javax.swing.JScrollPane requestSCROLL;
    private javax.swing.JTabbedPane requestTAB;
    private javax.swing.JButton shippinBTN;
    private javax.swing.JTable table;
    // End of variables declaration//GEN-END:variables
}

