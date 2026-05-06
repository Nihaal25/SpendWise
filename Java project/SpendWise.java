import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;
import java.util.*;

// --- WINDOW 1: LOGIN WINDOW ---
class LoginWindow extends JFrame {
    public LoginWindow() {
        setTitle("SpendWise - Authentication");
        setSize(450, 450); // Slightly taller to fit the register link
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel container = new JPanel(new GridBagLayout());
        container.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Welcome to SpendWise", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        
        JTextField userField = new JTextField(15);
        JPasswordField passField = new JPasswordField(15);
        JButton loginBtn = new JButton("Login to Account");
        
        // --- REGISTER LINK ---
        JButton openRegBtn = new JButton("Don't have an account? Register here");
        openRegBtn.setBorderPainted(false);
        openRegBtn.setContentAreaFilled(false);
        openRegBtn.setForeground(new Color(138, 180, 248));
        openRegBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        loginBtn.setBackground(new Color(138, 180, 248));
        loginBtn.setForeground(Color.BLACK);
        loginBtn.setPreferredSize(new Dimension(0, 40));

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        container.add(title, gbc);
        gbc.gridy = 1; gbc.gridwidth = 1;
        container.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1; container.add(userField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; container.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; container.add(passField, gbc);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        container.add(loginBtn, gbc);
        gbc.gridy = 4;
        container.add(openRegBtn, gbc);

        add(container);

        loginBtn.addActionListener(e -> { this.dispose(); new SpendWise().setVisible(true); });
        
        // Open Registration Window
        openRegBtn.addActionListener(e -> new RegisterWindow().setVisible(true));
    }
}

// --- WINDOW 2: REGISTRATION WINDOW ---
class RegisterWindow extends JFrame {
    public RegisterWindow() {
        setTitle("SpendWise - Create Account");
        setSize(400, 450);
        setLocationRelativeTo(null);
        
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField u = new JTextField(15);
        JTextField email = new JTextField(15);
        JPasswordField p1 = new JPasswordField(15);
        JPasswordField p2 = new JPasswordField(15);
        JButton regBtn = new JButton("Create Account");
        regBtn.setBackground(new Color(129, 201, 149)); // Greenish success color
        regBtn.setForeground(Color.BLACK);

        gbc.gridx = 0; gbc.gridy = 0; p.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1; p.add(u, gbc);
        gbc.gridx = 0; gbc.gridy = 1; p.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; p.add(email, gbc);
        gbc.gridx = 0; gbc.gridy = 2; p.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; p.add(p1, gbc);
        gbc.gridx = 0; gbc.gridy = 3; p.add(new JLabel("Confirm Pass:"), gbc);
        gbc.gridx = 1; p.add(p2, gbc);
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        p.add(regBtn, gbc);

        add(p);
        regBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Success! Account Created. You can now log in.");
            this.dispose();
        });
    }
}

// --- MAIN PROJECT FRAME ---
public class SpendWise extends JFrame {
    private final String DB_URL = "jdbc:mysql://localhost:3306/FinanceDB";
    private final String DB_USER = "root";
    private final String DB_PASS = "Apple2501@";

    private CardLayout cardLayout = new CardLayout();
    private JPanel mainContentPanel = new JPanel(cardLayout);
    private JLabel statusLabel = new JLabel(" System Ready");
    
    private String currentSearchKeyword = "";

    private DefaultTableModel tableModel = new DefaultTableModel(
        new String[]{"ID", "Description", "Amount", "Category/Type"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };

    private JTable dashboardTable = new JTable(tableModel);
    private JLabel totalIncomeLabel = new JLabel("$0.00"),
                   totalExpenseLabel = new JLabel("$0.00"),
                   netBalanceLabel = new JLabel("$0.00");
    private JPanel reportItemsList = new JPanel();

    public SpendWise() {
        setTitle("SpendWise Pro - Financial Management System");
        setSize(1280, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel rootPanel = new JPanel(new BorderLayout());
        setContentPane(rootPanel);

        // Sidebar
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(250, 0));
        sidebar.setBackground(new Color(30, 30, 30));
        sidebar.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 15));

        JLabel logo = new JLabel("SpendWise v1.0");
        logo.setForeground(Color.WHITE);
        logo.setFont(new Font("SansSerif", Font.ITALIC, 18));
        logo.setBorder(new EmptyBorder(20, 0, 30, 0));
        sidebar.add(logo);

        sidebar.add(createSidebarButton("Dashboard", "DASHBOARD_VIEW"));
        sidebar.add(createSidebarButton("Full Reports", "REPORTS_VIEW"));
        
        JButton searchBtn = new JButton("Search / Filter");
        searchBtn.setPreferredSize(new Dimension(200, 45));
        searchBtn.addActionListener(e -> new SearchWindow(this).setVisible(true));
        sidebar.add(searchBtn);

        JButton resetBtn = new JButton("Reset All Data");
        resetBtn.setPreferredSize(new Dimension(200, 45));
        resetBtn.setForeground(new Color(255, 100, 100));
        resetBtn.addActionListener(e -> resetDatabaseAction());
        sidebar.add(resetBtn);

        rootPanel.add(sidebar, BorderLayout.WEST);
        mainContentPanel.add(createDashboardPanel(), "DASHBOARD_VIEW");
        mainContentPanel.add(createReportsPanel(), "REPORTS_VIEW");
        rootPanel.add(mainContentPanel, BorderLayout.CENTER);

        statusLabel.setPreferredSize(new Dimension(0, 30));
        statusLabel.setForeground(Color.LIGHT_GRAY);
        rootPanel.add(statusLabel, BorderLayout.SOUTH);

        // Highlight renderer
        dashboardTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value != null && !currentSearchKeyword.isEmpty() &&
                        value.toString().toLowerCase().contains(currentSearchKeyword.toLowerCase())) {
                    c.setBackground(new Color(255, 255, 150));
                    c.setForeground(Color.BLACK);
                } else {
                    c.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
                    c.setForeground(isSelected ? table.getSelectionForeground() : table.getForeground());
                }
                return c;
            }
        });

        fetchDataFromDB(""); 
    }

    private JButton createSidebarButton(String text, String cardName) {
        JButton b = new JButton(text);
        b.setPreferredSize(new Dimension(200, 45));
        b.addActionListener(e -> {
            if (cardName.equals("REPORTS_VIEW")) refreshReportData();
            else if (cardName.equals("DASHBOARD_VIEW")) {
                currentSearchKeyword = "";
                fetchDataFromDB("");
            }
            cardLayout.show(mainContentPanel, cardName);
        });
        return b;
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(new Color(35, 35, 35));
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JButton btnAdd = new JButton("+ Add Transaction Entry");
        btnAdd.addActionListener(e -> showAddEntryDialog());
        JButton btnDelete = new JButton("Delete Selected Row");
        btnDelete.setBackground(new Color(230, 80, 80));
        btnDelete.addActionListener(e -> deleteSelectedRow());

        header.add(btnAdd, BorderLayout.WEST);
        header.add(btnDelete, BorderLayout.EAST);
        panel.add(header, BorderLayout.NORTH);

        dashboardTable.setRowHeight(40);
        panel.add(new JScrollPane(dashboardTable), BorderLayout.CENTER);
        return panel;
    }

    public void fetchDataFromDB(String keyword) {
        currentSearchKeyword = keyword;
        new Thread(() -> {
            java.util.List<Object[]> rows = new ArrayList<>();
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
                PreparedStatement ps = conn.prepareStatement(
                    "SELECT * FROM transactions WHERE description LIKE ? OR type LIKE ? ORDER BY id DESC"
                );
                ps.setString(1, "%" + keyword + "%");
                ps.setString(2, "%" + keyword + "%");
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    rows.add(new Object[]{
                        rs.getInt("id"), rs.getString("description"),
                        rs.getDouble("amount"), rs.getString("type").toUpperCase()
                    });
                }
            } catch (SQLException ex) { ex.printStackTrace(); }

            SwingUtilities.invokeLater(() -> {
                tableModel.setRowCount(0);
                for (Object[] row : rows) tableModel.addRow(row);
                statusLabel.setText(" Showing " + rows.size() + " records");
                dashboardTable.repaint();
                cardLayout.show(mainContentPanel, "DASHBOARD_VIEW");
            });
        }).start();
    }

    private void showAddEntryDialog() {
        JTextField d = new JTextField(); JTextField a = new JTextField();
        JComboBox<String> t = new JComboBox<>(new String[]{"Income", "Expense"});
        Object[] msg = {"Description:", d, "Amount:", a, "Type:", t};
        if (JOptionPane.showConfirmDialog(this, msg, "New Entry", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            new Thread(() -> {
                try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
                    PreparedStatement ps = conn.prepareStatement("INSERT INTO transactions (description, amount, type) VALUES (?, ?, ?)");
                    ps.setString(1, d.getText()); 
                    ps.setDouble(2, Double.parseDouble(a.getText()));
                    ps.setString(3, t.getSelectedItem().toString().toLowerCase());
                    ps.executeUpdate();
                    fetchDataFromDB(""); 
                } catch (Exception ex) { ex.printStackTrace(); }
            }).start();
        }
    }

    private void deleteSelectedRow() {
        int r = dashboardTable.getSelectedRow();
        if (r == -1) return;
        int id = (int) tableModel.getValueAt(r, 0);
        new Thread(() -> {
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
                PreparedStatement ps = conn.prepareStatement("DELETE FROM transactions WHERE id = ?");
                ps.setInt(1, id); ps.executeUpdate();
                fetchDataFromDB("");
            } catch (SQLException ex) { ex.printStackTrace(); }
        }).start();
    }

    private void resetDatabaseAction() {
        if (JOptionPane.showConfirmDialog(this, "Wipe all data?", "Warning", JOptionPane.YES_NO_OPTION) == JOptionPane.OK_OPTION) {
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
                conn.createStatement().execute("TRUNCATE TABLE transactions");
                fetchDataFromDB("");
            } catch (Exception ex) { ex.printStackTrace(); }
        }
    }

    private JPanel createReportsPanel() {
        JPanel p = new JPanel(new BorderLayout(25, 25));
        p.setBackground(new Color(35, 35, 35));
        p.setBorder(new EmptyBorder(30, 30, 30, 30));
        JPanel stats = new JPanel(new GridLayout(1, 3, 20, 0));
        stats.setOpaque(false);
        stats.add(createAnalyticsCard("Total Income", totalIncomeLabel, new Color(129, 201, 149)));
        stats.add(createAnalyticsCard("Total Expense", totalExpenseLabel, new Color(242, 139, 130)));
        stats.add(createAnalyticsCard("Net Savings", netBalanceLabel, new Color(138, 180, 248)));
        reportItemsList.setLayout(new BoxLayout(reportItemsList, BoxLayout.Y_AXIS));
        reportItemsList.setBackground(new Color(45, 45, 45));
        p.add(stats, BorderLayout.NORTH);
        p.add(new JScrollPane(reportItemsList), BorderLayout.CENTER);
        return p;
    }

    private JPanel createAnalyticsCard(String t, JLabel v, Color c) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(55, 55, 55));
        card.setBorder(new EmptyBorder(20, 20, 20, 20));
        JLabel title = new JLabel(t); title.setForeground(Color.LIGHT_GRAY);
        v.setFont(new Font("SansSerif", Font.BOLD, 28)); v.setForeground(c);
        card.add(title, BorderLayout.NORTH); card.add(v, BorderLayout.SOUTH);
        return card;
    }

    private void refreshReportData() {
        new Thread(() -> {
            double inc = 0, exp = 0;
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
                ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM transactions");
                SwingUtilities.invokeLater(() -> reportItemsList.removeAll());
                while (rs.next()) {
                    double a = rs.getDouble("amount"); String t = rs.getString("type");
                    if (t.equalsIgnoreCase("income")) inc += a; else exp += a;
                    String line = String.format("  • %s | $%.2f | %s", rs.getString("description"), a, t.toUpperCase());
                    SwingUtilities.invokeLater(() -> {
                        JLabel lbl = new JLabel(line); lbl.setForeground(Color.WHITE);
                        lbl.setBorder(new EmptyBorder(5, 10, 5, 10));
                        reportItemsList.add(lbl);
                    });
                }
                double finalInc = inc, finalExp = exp;
                SwingUtilities.invokeLater(() -> {
                    totalIncomeLabel.setText("$" + String.format("%.2f", finalInc));
                    totalExpenseLabel.setText("$" + String.format("%.2f", finalExp));
                    netBalanceLabel.setText("$" + String.format("%.2f", finalInc - finalExp));
                    reportItemsList.revalidate(); reportItemsList.repaint();
                });
            } catch (SQLException ex) { ex.printStackTrace(); }
        }).start();
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(new FlatMacDarkLaf()); } catch (Exception e) {}
        SwingUtilities.invokeLater(() -> new LoginWindow().setVisible(true));
    }
}

// --- SEARCH WINDOW ---
class SearchWindow extends JFrame {
    public SearchWindow(SpendWise parent) {
        setTitle("Filter Dashboard");
        setSize(400, 220);
        setLocationRelativeTo(parent);
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(new EmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JTextField searchInput = new JTextField(18);
        JButton btnApply = new JButton("Filter Dashboard");
        gbc.gridx = 0; gbc.gridy = 0; p.add(new JLabel("Search Description:"), gbc);
        gbc.gridy = 1; p.add(searchInput, gbc);
        gbc.gridy = 2; p.add(btnApply, gbc);
        add(p);
        btnApply.addActionListener(e -> {
            parent.fetchDataFromDB(searchInput.getText().trim());
            dispose();
        });
        searchInput.addActionListener(e -> {
            parent.fetchDataFromDB(searchInput.getText().trim());
            dispose();
        });
    }
}