package com.bookswap;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class App {

    private static final String DB_URL =
            "jdbc:mysql://localhost:3306/bookswap?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "1234"; // <-- schimbă aici

    public static void main(String[] args) {
        SwingUtilities.invokeLater(App::showLogin);
    }

    private static void showLogin() {
        JFrame frame = new JFrame("BookSwap - Login");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(820, 520);
        frame.setLocationRelativeTo(null);

        GradientPanel root = new GradientPanel(
                new Color(0xFBE3EE), // roz foarte light
                new Color(0xF6C7D8)  // roz puțin mai intens
        );
        root.setLayout(new GridBagLayout());

        RoundedPanel card = new RoundedPanel(22, Color.WHITE);
        card.setPreferredSize(new Dimension(520, 620));
        card.setMinimumSize(new Dimension(520, 620));

        card.setLayout(new GridBagLayout());
        card.setBorder(BorderFactory.createEmptyBorder(26, 34, 26, 34));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 10, 10, 10);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;

        JLabel title = new JLabel("Log In", SwingConstants.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 34));
        title.setForeground(new Color(0x8A2B4A));

        JLabel emailLabel = new JLabel("Email Address");
        emailLabel.setForeground(new Color(0x333333));
        emailLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JTextField emailField = new JTextField();
        styleField(emailField);

        JLabel passLabel = new JLabel("Password");
        passLabel.setForeground(new Color(0x333333));
        passLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JPasswordField passField = new JPasswordField();
        styleField(passField);

        JLabel forgot = new JLabel("Forget password?");
        forgot.setForeground(new Color(0xB04A6A));
        forgot.setFont(new Font("SansSerif", Font.PLAIN, 12));
        forgot.setHorizontalAlignment(SwingConstants.RIGHT);

        JButton loginBtn = new JButton("Log in");
        stylePrimaryButton(loginBtn);

        JButton registerBtn = new JButton("Nu ai cont? Creează unul");
        registerBtn.setBorderPainted(false);
        registerBtn.setContentAreaFilled(false);
        registerBtn.setForeground(new Color(0x8A2B4A));
        registerBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        registerBtn.setFont(new Font("SansSerif", Font.PLAIN, 13));

        JLabel status = new JLabel(" ", SwingConstants.CENTER);
        status.setFont(new Font("SansSerif", Font.PLAIN, 13));
        status.setForeground(new Color(0xB00020));

        // --- layout în card ---
        c.gridy = 0;
        c.ipady = 0;
        card.add(title, c);

        c.gridy = 1;
        card.add(emailLabel, c);

        c.gridy = 2;
        c.ipady = 10;
        card.add(emailField, c);

        c.gridy = 3;
        c.ipady = 0;
        card.add(passLabel, c);

        c.gridy = 4;
        c.ipady = 10;
        card.add(passField, c);

        c.gridy = 5;
        card.add(forgot, c);

        c.gridy = 6;
        c.ipady = 14;
        card.add(loginBtn, c);

        c.gridy = 7;
        c.ipady = 0;
        card.add(registerBtn, c);

        c.gridy = 8;
        card.add(status, c);

        // --- acțiuni ---
        loginBtn.addActionListener(e -> {
            String email = emailField.getText().trim();
            String parola = new String(passField.getPassword());

            if (email.isEmpty() || parola.isEmpty()) {
                status.setForeground(new Color(0xB00020));
                status.setText("Completează email + parola.");
                return;
            }

            try {
                User user = authenticate(email, parola);
                if (user != null) {
                    frame.dispose();
                    showDashboard(user);
                } else {
                    status.setForeground(new Color(0xB00020));
                    status.setText("Email sau parola greșite.");
                }
            } catch (Exception ex) {
                status.setForeground(new Color(0xB00020));
                status.setText("Eroare DB: " + ex.getMessage());
            }
        });

        registerBtn.addActionListener(e -> {
            try {
                User newUser = showRegisterDialog(frame);
                if (newUser != null) {
                    emailField.setText(newUser.email);
                    passField.setText("");
                    status.setForeground(new Color(0x1B7F3A));
                    status.setText("Cont creat! Acum te poți loga.");
                }
            } catch (Exception ex) {
                String msg = ex.getMessage() == null ? "Eroare" : ex.getMessage();
                status.setForeground(new Color(0xB00020));
                if (msg.toLowerCase().contains("duplicate")) {
                    status.setText("Username sau email deja există.");
                } else {
                    status.setText("Eroare register: " + msg);
                }
            }
        });

        // centru card pe fundal
        GridBagConstraints rootC = new GridBagConstraints();
        rootC.insets = new Insets(20, 20, 20, 20);
        root.add(card, rootC);

        frame.setContentPane(root);
        frame.setVisible(true);
    }


    private static User authenticate(String email, String parola) throws SQLException {
        // IMPORTANT: coloana trebuie să fie exact "parola" în DB
        String sql = "SELECT id, username, email FROM users WHERE email = ? AND parola = ? LIMIT 1";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, parola);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getLong("id"),
                            rs.getString("username"),
                            rs.getString("email")
                    );
                }
                return null;
            }
        }
    }

    private static JTable createBooksTable() {
        String[] cols = {"Titlu", "Autor"};

        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);

        // 🔴 CHEIA: dezactivează mutarea coloanelor
        table.getTableHeader().setReorderingAllowed(false);

        table.setRowHeight(26);
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));

        return table;
    }


    private static void loadMyBooksIntoTable(JTable table, long userId) {
        var model = (javax.swing.table.DefaultTableModel) table.getModel();
        model.setRowCount(0);

        String sql = """
        SELECT b.titlu, b.autor
        FROM user_books ub
        JOIN books b ON b.id = ub.book_id
        WHERE ub.user_id = ?
        ORDER BY b.titlu
        """;

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    model.addRow(new Object[]{rs.getString("titlu"), rs.getString("autor")});
                }
            }
        } catch (SQLException ex) {
            model.addRow(new Object[]{"(DB error) " + ex.getMessage(), ""});
        }
    }

    private static void loadAllBooksIntoTable(JTable table) {
        var model = (javax.swing.table.DefaultTableModel) table.getModel();
        model.setRowCount(0);

        String sql = "SELECT titlu, autor FROM books ORDER BY titlu";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                model.addRow(new Object[]{rs.getString("autor"), rs.getString("titlu")});
            }
        } catch (SQLException ex) {
            model.addRow(new Object[]{"(DB error) " + ex.getMessage(), ""});
        }
    }

    private record BookChoice(long id, String titlu, String autor) {}

    private static BookChoice chooseBookDialog(JFrame parent) throws SQLException {
        DefaultComboBoxModel<BookChoice> model = new DefaultComboBoxModel<>();

        String sql = "SELECT id, titlu, autor FROM books ORDER BY titlu";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                model.addElement(new BookChoice(rs.getLong("id"), rs.getString("titlu"), rs.getString("autor")));
            }
        }

        JComboBox<BookChoice> combo = new JComboBox<>(model);
        combo.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel lbl = new JLabel();
            if (value != null) lbl.setText(value.titlu() + " — " + value.autor());
            lbl.setOpaque(true);
            lbl.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
            if (isSelected) {
                lbl.setBackground(new Color(0xF2D3DE));
            } else {
                lbl.setBackground(Color.WHITE);
            }
            return lbl;
        });

        int option = JOptionPane.showConfirmDialog(parent, combo, "Select a book", JOptionPane.OK_CANCEL_OPTION);
        if (option != JOptionPane.OK_OPTION) return null;

        return (BookChoice) combo.getSelectedItem();
    }

    private static void addBookToUser(long userId, long bookId) throws SQLException {
        String sql = "INSERT INTO user_books(user_id, book_id) VALUES (?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, bookId);
            ps.executeUpdate();
        }
    }


    private static User showRegisterDialog(JFrame parent) throws SQLException {
        JTextField usernameField = new JTextField();
        JTextField emailField = new JTextField();
        JPasswordField passField = new JPasswordField();

        Object[] msg = {
                "Username:", usernameField,
                "Email:", emailField,
                "Parola:", passField
        };

        int option = JOptionPane.showConfirmDialog(
                parent, msg, "Creează cont",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );

        if (option != JOptionPane.OK_OPTION) return null;

        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String parola = new String(passField.getPassword());

        if (username.isEmpty() || email.isEmpty() || parola.isEmpty()) {
            throw new SQLException("Toate câmpurile sunt obligatorii.");
        }

        long newId = insertUser(username, email, parola);
        return new User(newId, username, email);
    }

    private static long insertUser(String username, String email, String parola) throws SQLException {
        String sql = "INSERT INTO users(username, email, parola) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, username);
            ps.setString(2, email);
            ps.setString(3, parola);

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getLong(1);
                throw new SQLException("Nu am putut obține ID-ul userului.");
            }
        }
    }


    private static void showDashboard(User user) {
        JFrame frame = new JFrame("BookSwap - Dashboard");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(980, 620);
        frame.setLocationRelativeTo(null);

        GradientPanel root = new GradientPanel(new Color(0xFBE3EE), new Color(0xF6C7D8));
        root.setLayout(new BorderLayout(12, 12));
        root.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel appTitle = new JLabel("Book Swap");
        appTitle.setFont(new Font("Serif", Font.BOLD, 26));
        appTitle.setForeground(new Color(0x8A2B4A));

        JLabel hello = new JLabel("Logged in as: " + user.username + " (" + user.email + ")");
        hello.setFont(new Font("SansSerif", Font.PLAIN, 13));
        hello.setForeground(new Color(0x333333));

        JPanel leftHeader = new JPanel(new GridLayout(2, 1));
        leftHeader.setOpaque(false);
        leftHeader.add(appTitle);
        leftHeader.add(hello);

        JButton logout = new JButton("Log Out");
        stylePrimaryButton(logout);
        logout.setFont(new Font("SansSerif", Font.BOLD, 14));
        logout.addActionListener(e -> {
            frame.dispose();
            showLogin();
        });

        header.add(leftHeader, BorderLayout.WEST);
        header.add(logout, BorderLayout.EAST);

        // Card
        RoundedPanel card = new RoundedPanel(22, Color.WHITE);
        card.setLayout(new BorderLayout(10, 10));
        card.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JTabbedPane tabs = new JTabbedPane();

        /* ===================== MY BOOKS ===================== */
        JTable myBooksTable = createBooksTable();
        JScrollPane myBooksScroll = new JScrollPane(myBooksTable);

        JButton refreshMy = new JButton("Refresh");
        styleSoftButton(refreshMy);

        JButton addToMe = new JButton("Add book to me");
        stylePrimaryButton(addToMe);

        JPanel myTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        myTop.setOpaque(false);
        myTop.add(refreshMy);
        myTop.add(addToMe);

        JPanel myTab = new JPanel(new BorderLayout(10, 10));
        myTab.setOpaque(false);
        myTab.add(myTop, BorderLayout.NORTH);
        myTab.add(myBooksScroll, BorderLayout.CENTER);

        tabs.addTab("My Books", myTab);

        /* ===================== BROWSE BOOKS ===================== */
        JTable browseBooksTable = createBrowseBooksTable();
        JScrollPane browseScroll = new JScrollPane(browseBooksTable);

        JButton refreshAll = new JButton("Refresh");
        styleSoftButton(refreshAll);

        JButton requestSwap = new JButton("Request Swap");
        stylePrimaryButton(requestSwap);

        JPanel browseTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        browseTop.setOpaque(false);
        browseTop.add(refreshAll);
        browseTop.add(requestSwap);

        JPanel browseTab = new JPanel(new BorderLayout(10, 10));
        browseTab.setOpaque(false);
        browseTab.add(browseTop, BorderLayout.NORTH);
        browseTab.add(browseScroll, BorderLayout.CENTER);

        tabs.addTab("Browse Books", browseTab);

        /* ===================== INBOX ===================== */
        JTable inboxTable = createInboxTable();
        JScrollPane inboxScroll = new JScrollPane(inboxTable);

        JButton refreshInbox = new JButton("Refresh");
        styleSoftButton(refreshInbox);

        JButton acceptBtn = new JButton("Accept");
        stylePrimaryButton(acceptBtn);

        JButton rejectBtn = new JButton("Reject");

        acceptBtn.addActionListener(e ->
                handleInboxAction(inboxTable, "ACCEPTED", user.id)
        );

        rejectBtn.addActionListener(e ->
                handleInboxAction(inboxTable, "REJECTED", user.id)
        );


        stylePrimaryButton(rejectBtn);

        JPanel inboxTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        inboxTop.setOpaque(false);
        inboxTop.add(refreshInbox);
        inboxTop.add(acceptBtn);
        inboxTop.add(rejectBtn);

        JPanel inboxTab = new JPanel(new BorderLayout(10, 10));
        inboxTab.setOpaque(false);
        inboxTab.add(inboxTop, BorderLayout.NORTH);
        inboxTab.add(inboxScroll, BorderLayout.CENTER);

        tabs.addTab("Inbox Requests", inboxTab);

        /* ===================== FINAL UI ===================== */
        card.add(tabs, BorderLayout.CENTER);
        root.add(header, BorderLayout.NORTH);
        root.add(card, BorderLayout.CENTER);

        frame.setContentPane(root);
        frame.setVisible(true);

        /* ===================== LOAD DATA ===================== */
        loadMyBooksIntoTable(myBooksTable, user.id);
        loadBrowseBooks(browseBooksTable);
        loadInboxRequests(inboxTable, user.id);

        /* ===================== ACTIONS ===================== */
        refreshMy.addActionListener(e -> loadMyBooksIntoTable(myBooksTable, user.id));
        refreshAll.addActionListener(e -> loadBrowseBooks(browseBooksTable));
        refreshInbox.addActionListener(e -> loadInboxRequests(inboxTable, user.id));

        addToMe.addActionListener(e -> {
            try {
                BookChoice chosen = chooseBookDialog(frame);
                if (chosen != null) {
                    addBookToUser(user.id, chosen.id());
                    loadMyBooksIntoTable(myBooksTable, user.id);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        requestSwap.addActionListener(e -> {
            int row = browseBooksTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(frame, "Selectează o carte.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            long ownerId = (long) browseBooksTable.getModel().getValueAt(row, 0);
            long bookId  = (long) browseBooksTable.getModel().getValueAt(row, 2);
            String owner = String.valueOf(browseBooksTable.getModel().getValueAt(row, 1));
            String titlu = String.valueOf(browseBooksTable.getModel().getValueAt(row, 3));
            String autor = String.valueOf(browseBooksTable.getModel().getValueAt(row, 4));

            if (ownerId == user.id) {
                JOptionPane.showMessageDialog(frame, "Nu poți cere propria carte.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            try {
                createSwapRequest(user.id, ownerId, bookId);
                JOptionPane.showMessageDialog(frame,
                        "Request trimis către " + owner + ":\n" + titlu + " — " + autor,
                        "Swap Request", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private record User(long id, String username, String email) {}

    private static void styleField(JTextField field) {
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xE6E6E6), 1, true),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        field.setBackground(Color.WHITE);
    }

    private static void stylePrimaryButton(JButton btn) {
        btn.setFont(new Font("SansSerif", Font.BOLD, 16));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(0xC85A77)); // roz
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
    }

    private static void styleSoftButton(JButton btn) {
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setForeground(new Color(0x8A2B4A));
        btn.setBackground(new Color(0xF2D3DE));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
    }

    private static long findBookIdByTitleAuthor(String titlu, String autor) throws SQLException {
        String sql = "SELECT id FROM books WHERE titlu = ? AND autor = ? LIMIT 1";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, titlu);
            ps.setString(2, autor);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong("id");
                throw new SQLException("Book not found in DB for: " + titlu + " / " + autor);
            }
        }
    }

    private static void createSwapRequest(long requesterId, long ownerId, long bookId) throws SQLException {
        String sql = """
        INSERT INTO swap_requests(requester_user_id, owner_user_id, requested_book_id)
        VALUES (?, ?, ?)
    """;

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, requesterId);
            ps.setLong(2, ownerId);
            ps.setLong(3, bookId);
            ps.executeUpdate();
        }
    }

    private static JTable createInboxTable() {
        String[] cols = {"ID", "From", "Titlu", "Autor", "Status", "Date"};
        var model = new javax.swing.table.DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        table.setRowHeight(26);

        // ascunde ID
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);

        return table;
    }

    private static void loadInboxRequests(JTable table, long userId) {
        System.out.println("LOADING INBOX FOR USER ID = " + userId);

        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);

        String sql = """
        SELECT
          sr.id,
          u.username AS from_user,
          b.titlu,
          b.autor,
          sr.status,
          sr.created_at
        FROM swap_requests sr
        JOIN users u ON u.id = sr.requester_user_id
        JOIN books b ON b.id = sr.requested_book_id
        WHERE sr.owner_user_id = ?
        ORDER BY sr.created_at DESC
    """;

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    System.out.println(
                            "INBOX ROW: requestId=" + rs.getLong("id") +
                                    " from=" + rs.getString("from_user")
                    );

                    model.addRow(new Object[]{
                            rs.getLong("id"),
                            rs.getString("from_user"),
                            rs.getString("titlu"),
                            rs.getString("autor"),
                            rs.getString("status"),
                            rs.getTimestamp("created_at")
                    });
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            model.addRow(new Object[]{"ERR", ex.getMessage(), "", "", "", ""});
        }
    }


    private static void updateRequestStatus(long requestId, String status) throws SQLException {
        String sql = "UPDATE swap_requests SET status = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setLong(2, requestId);
            ps.executeUpdate();
        }
    }

    private static void handleInboxAction(JTable inboxTable, String status, long userId) {
        int row = inboxTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(null, "Selectează o cerere.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        long requestId = (long) inboxTable.getModel().getValueAt(row, 0);

        try {
            updateRequestStatus(requestId, status);
            loadInboxRequests(inboxTable, userId);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }


    private static JTable createBrowseBooksTable() {
        String[] cols = {"OwnerId", "Owner", "BookId", "Titlu", "Autor"};

        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(26);
        table.getTableHeader().setReorderingAllowed(false);

        // ascunde OwnerId
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);

        // ascunde BookId
        table.getColumnModel().getColumn(2).setMinWidth(0);
        table.getColumnModel().getColumn(2).setMaxWidth(0);
        table.getColumnModel().getColumn(2).setWidth(0);

        return table;
    }



    private static void loadBrowseBooks(JTable table) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);

        String sql = """
        SELECT
          u.id AS owner_id,
          u.username AS owner,
          b.id AS book_id,
          b.titlu,
          b.autor
        FROM user_books ub
        JOIN users u ON u.id = ub.user_id
        JOIN books b ON b.id = ub.book_id
        ORDER BY b.titlu
    """;

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getLong("owner_id"),
                        rs.getString("owner"),
                        rs.getLong("book_id"),
                        rs.getString("titlu"),
                        rs.getString("autor")
                });
            }
        } catch (SQLException ex) {
            model.addRow(new Object[]{"ERR", ex.getMessage(), "", "", ""});
        }
    }



}
class GradientPanel extends JPanel {
    private final Color top;
    private final Color bottom;

    GradientPanel(Color top, Color bottom) {
        this.top = top;
        this.bottom = bottom;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        int w = getWidth(), h = getHeight();
        GradientPaint gp = new GradientPaint(0, 0, top, 0, h, bottom);
        g2.setPaint(gp);
        g2.fillRect(0, 0, w, h);
        g2.dispose();
    }
}

class RoundedPanel extends JPanel {
    private final int radius;
    private final Color bg;

    RoundedPanel(int radius, Color bg) {
        this.radius = radius;
        this.bg = bg;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // shadow
        g2.setColor(new Color(0, 0, 0, 25));
        g2.fillRoundRect(6, 8, w - 12, h - 12, radius, radius);

        // card
        g2.setColor(bg);
        g2.fillRoundRect(0, 0, w - 12, h - 12, radius, radius);

        g2.dispose();
        super.paintComponent(g);
    }

    @Override
    public Insets getInsets() {
        return new Insets(0, 0, 12, 12);
    }
}
