package Inventory;

import java.awt.Color;
import java.awt.Font;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

final class EmployeeForm extends JDialog {

    private final Invent parent;
    private final int userId;
    private final JTextField fullName = new JTextField();
    private final JTextField address = new JTextField();
    private final JTextField username = new JTextField();
    private final JPasswordField password = new JPasswordField();
    private final JComboBox<String> status = new JComboBox<>(new String[]{"Active", "Inactive"});

    EmployeeForm(Invent parent, int userId) {
        super(parent, userId < 0 ? "Add Employee" : "Edit Employee", true);
        this.parent = parent;
        this.userId = userId;
        build();
        if (userId >= 0) {
            loadEmployee();
        }
        pack();
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    private void build() {
        Color ink = new Color(47, 21, 26);
        Color muted = new Color(110, 98, 92);
        Font labelFont = new Font("Segoe UI", Font.PLAIN, 13);
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 14);
        getContentPane().setBackground(new Color(246, 242, 236));

        JLabel title = new JLabel(userId < 0 ? "Add Employee" : "Edit Employee");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(ink);

        JLabel nameLabel = label("Fullname", labelFont, muted);
        JLabel addressLabel = label("Address", labelFont, muted);
        JLabel userLabel = label("Username", labelFont, muted);
        JLabel passLabel = label("Password", labelFont, muted);
        JLabel statusLabel = label("Status", labelFont, muted);
        fullName.setFont(fieldFont);
        address.setFont(fieldFont);
        username.setFont(fieldFont);
        password.setFont(fieldFont);
        status.setFont(fieldFont);

        JButton save = new JButton("Save");
        save.setFont(new Font("Segoe UI", Font.BOLD, 13));
        save.setBackground(new Color(122, 36, 48));
        save.setForeground(Color.WHITE);
        save.addActionListener(e -> saveEmployee());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(28, 28, 28)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(title)
                                        .addComponent(nameLabel)
                                        .addComponent(fullName, 280, 280, 280)
                                        .addComponent(addressLabel)
                                        .addComponent(address, 280, 280, 280)
                                        .addComponent(userLabel)
                                        .addComponent(username, 280, 280, 280)
                                        .addComponent(passLabel)
                                        .addComponent(password, 280, 280, 280)
                                        .addComponent(statusLabel)
                                        .addComponent(status, 280, 280, 280)
                                        .addComponent(save, 120, 120, 120))
                                .addGap(28, 28, 28))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(24, 24, 24)
                                .addComponent(title)
                                .addGap(16, 16, 16)
                                .addComponent(nameLabel)
                                .addGap(6, 6, 6)
                                .addComponent(fullName, 32, 32, 32)
                                .addGap(12, 12, 12)
                                .addComponent(addressLabel)
                                .addGap(6, 6, 6)
                                .addComponent(address, 32, 32, 32)
                                .addGap(12, 12, 12)
                                .addComponent(userLabel)
                                .addGap(6, 6, 6)
                                .addComponent(username, 32, 32, 32)
                                .addGap(12, 12, 12)
                                .addComponent(passLabel)
                                .addGap(6, 6, 6)
                                .addComponent(password, 32, 32, 32)
                                .addGap(12, 12, 12)
                                .addComponent(statusLabel)
                                .addGap(6, 6, 6)
                                .addComponent(status, 32, 32, 32)
                                .addGap(18, 18, 18)
                                .addComponent(save, 34, 34, 34)
                                .addGap(24, 24, 24))
        );
    }

    private static JLabel label(String text, Font font, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(color);
        return label;
    }

    private void loadEmployee() {
        String sql = "SELECT Fullname, Address, Username, Password, AccountStatus FROM employee WHERE UserID = ?";
        try (Connection con = DB_Connection.getConnection();
                PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, userId);
            try (ResultSet rs = pst.executeQuery()) {
                if (!rs.next()) {
                    JOptionPane.showMessageDialog(this, "That employee was not found.");
                    dispose();
                    return;
                }
                fullName.setText(rs.getString("Fullname"));
                address.setText(rs.getString("Address"));
                username.setText(rs.getString("Username"));
                password.setText(rs.getString("Password"));
                status.setSelectedItem(rs.getString("AccountStatus"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Cannot load the employee.\n" + e.getMessage());
            dispose();
        }
    }

    private void saveEmployee() {
        String name = fullName.getText().trim();
        String home = address.getText().trim();
        String user = username.getText().trim();
        String pass = new String(password.getPassword());
        String accountStatus = String.valueOf(status.getSelectedItem());
        if (name.isEmpty() || user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter the full name, username, and password.");
            return;
        }
        String duplicate = "SELECT Username FROM employee WHERE Username = ? AND UserID <> ? "
                + "UNION SELECT Username FROM admin WHERE Username = ?";
        String insert = "INSERT INTO employee (Fullname, Address, Username, Password, AccountStatus) "
                + "VALUES (?, ?, ?, ?, ?)";
        String update = "UPDATE employee SET Fullname = ?, Address = ?, Username = ?, Password = ?, AccountStatus = ? "
                + "WHERE UserID = ?";
        try (Connection con = DB_Connection.getConnection()) {
            try (PreparedStatement check = con.prepareStatement(duplicate)) {
                check.setString(1, user);
                check.setInt(2, userId < 0 ? 0 : userId);
                check.setString(3, user);
                try (ResultSet rs = check.executeQuery()) {
                    if (rs.next()) {
                        JOptionPane.showMessageDialog(this, "That username is already used.");
                        return;
                    }
                }
            }
            if (userId < 0) {
                try (PreparedStatement pst = con.prepareStatement(insert)) {
                    pst.setString(1, name);
                    pst.setString(2, home);
                    pst.setString(3, user);
                    pst.setString(4, pass);
                    pst.setString(5, accountStatus);
                    pst.executeUpdate();
                }
            } else {
                try (PreparedStatement pst = con.prepareStatement(update)) {
                    pst.setString(1, name);
                    pst.setString(2, home);
                    pst.setString(3, user);
                    pst.setString(4, pass);
                    pst.setString(5, accountStatus);
                    pst.setInt(6, userId);
                    pst.executeUpdate();
                }
            }
            parent.reloadEmployees();
            dispose();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Could not save the employee.\n" + e.getMessage());
        }
    }
}
