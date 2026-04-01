package earlyintermediate;
import javax.swing.Timer;  // Make sure this is imported, not java.util.Timer
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;
import java.util.*;

public class AddressBook extends JFrame {
    
    // Table columns
    private String[] columns = {"ID", "Name", "Phone", "Email", "Address", "Category"};
    private DefaultTableModel tableModel;
    private JTable contactTable;
    private JTextField idField, nameField, phoneField, emailField, addressField;
    private JComboBox<String> categoryCombo;
    private JButton addButton, editButton, deleteButton, clearButton, searchButton;
    private JTextField searchField;
    private JLabel statusLabel, countLabel;
    private int nextId = 1;
    
    // Colors
    private Color primaryColor = new Color(33, 150, 243);
    private Color successColor = new Color(76, 175, 80);
    private Color dangerColor = new Color(244, 67, 54);
    private Color warningColor = new Color(255, 152, 0);
    private Color bgColor = new Color(248, 249, 250);
    
    public AddressBook() {
        setTitle("📇 Address Book Manager");
        setSize(1100, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initializeComponents();
        setupLayout();
        setupListeners();
        loadSampleData();
        
        setVisible(true);
    }
    
    private void initializeComponents() {
        // Table Model (non-editable)
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        
        contactTable = new JTable(tableModel);
        contactTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        contactTable.setRowHeight(30);
        contactTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        contactTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        contactTable.getTableHeader().setBackground(primaryColor);
        contactTable.getTableHeader().setForeground(Color.WHITE);
        
        // Set column widths
        contactTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        contactTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        contactTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        contactTable.getColumnModel().getColumn(3).setPreferredWidth(180);
        contactTable.getColumnModel().getColumn(4).setPreferredWidth(200);
        contactTable.getColumnModel().getColumn(5).setPreferredWidth(100);
        
        // Form Fields
        idField = new JTextField();
        idField.setEditable(false);
        idField.setBackground(new Color(240, 240, 240));
        
        nameField = new JTextField();
        phoneField = new JTextField();
        emailField = new JTextField();
        addressField = new JTextField();
        
        // Category Combo
        String[] categories = {"Personal", "Business", "Family", "Friend", "Work", "Other"};
        categoryCombo = new JComboBox<>(categories);
        
        // Buttons
        addButton = createStyledButton("➕ Add Contact", successColor);
        editButton = createStyledButton("✏️ Edit Contact", warningColor);
        deleteButton = createStyledButton("🗑️ Delete Contact", dangerColor);
        clearButton = createStyledButton("🗑️ Clear Form", new Color(108, 117, 125));
        searchButton = createStyledButton("🔍 Search", primaryColor);
        
        // Initially disable edit and delete buttons
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
        
        // Search Field
        searchField = new JTextField(15);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        // Status and Count Labels
        statusLabel = new JLabel("● Ready");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusLabel.setForeground(new Color(100, 100, 100));
        
        countLabel = new JLabel("Total Contacts: 0");
        countLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        countLabel.setForeground(primaryColor);
    }
    
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.darker());
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(bgColor);
        
        // Header Panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Main Content Split Pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(450);
        splitPane.setResizeWeight(0.35);
        
        // Left Panel - Form
        JPanel leftPanel = createFormPanel();
        
        // Right Panel - Table
        JPanel rightPanel = createTablePanel();
        
        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);
        
        add(splitPane, BorderLayout.CENTER);
        
        // Footer Panel
        JPanel footerPanel = createFooterPanel();
        add(footerPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(primaryColor);
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel title = new JLabel("📇 ADDRESS BOOK MANAGER");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        
        JLabel subtitle = new JLabel("Manage your contacts efficiently");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setForeground(new Color(255, 255, 255, 200));
        
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(title, BorderLayout.NORTH);
        titlePanel.add(subtitle, BorderLayout.SOUTH);
        
        header.add(titlePanel, BorderLayout.WEST);
        
        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        searchPanel.setOpaque(false);
        
        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setForeground(Color.WHITE);
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        searchField.setPreferredSize(new Dimension(200, 30));
        
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        
        header.add(searchPanel, BorderLayout.EAST);
        
        return header;
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel formTitle = new JLabel("CONTACT DETAILS");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        formTitle.setForeground(primaryColor);
        formTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(formTitle);
        panel.add(Box.createVerticalStrut(20));
        
        // Form Fields
        panel.add(createFormField("ID:", idField));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createFormField("Name *:", nameField));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createFormField("Phone *:", phoneField));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createFormField("Email:", emailField));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createFormField("Address:", addressField));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createFormField("Category:", categoryCombo));
        panel.add(Box.createVerticalStrut(20));
        
        // Buttons Panel
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        
        panel.add(buttonPanel);
        
        // Info Panel
        panel.add(Box.createVerticalStrut(20));
        JPanel infoPanel = new JPanel();
        infoPanel.setBackground(new Color(255, 248, 225));
        infoPanel.setBorder(BorderFactory.createLineBorder(new Color(255, 193, 7)));
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        
        JLabel infoTitle = new JLabel("ℹ️ Instructions:");
        infoTitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
        infoTitle.setForeground(warningColor);
        infoPanel.add(infoTitle);
        
        String[] instructions = {
            "• Fields with * are required",
            "• Click on table row to edit/delete",
            "• Use search to find contacts",
            "• Double-click table row to load contact"
        };
        
        for (String instruction : instructions) {
            JLabel ins = new JLabel(instruction);
            ins.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            infoPanel.add(ins);
        }
        
        panel.add(Box.createVerticalStrut(10));
        panel.add(infoPanel);
        
        return panel;
    }
    
    private JPanel createFormField(String label, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(Color.WHITE);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setPreferredSize(new Dimension(80, 25));
        
        field.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        panel.add(lbl, BorderLayout.WEST);
        panel.add(field, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel tableTitle = new JLabel("CONTACT LIST");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tableTitle.setForeground(primaryColor);
        
        JScrollPane scrollPane = new JScrollPane(contactTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        
        panel.add(tableTitle, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createFooterPanel() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(new Color(240, 240, 240));
        footer.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        
        footer.add(statusLabel, BorderLayout.WEST);
        footer.add(countLabel, BorderLayout.EAST);
        
        return footer;
    }
    
    private void setupListeners() {
        // Add Contact
        addButton.addActionListener(e -> addContact());
        
        // Edit Contact
        editButton.addActionListener(e -> editContact());
        
        // Delete Contact
        deleteButton.addActionListener(e -> deleteContact());
        
        // Clear Form
        clearButton.addActionListener(e -> clearForm());
        
        // Search
        searchButton.addActionListener(e -> searchContacts());
        searchField.addActionListener(e -> searchContacts());
        
        // Table Selection Listener
        contactTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = contactTable.getSelectedRow();
                if (selectedRow >= 0) {
                    loadContactToForm(selectedRow);
                    editButton.setEnabled(true);
                    deleteButton.setEnabled(true);
                } else {
                    editButton.setEnabled(false);
                    deleteButton.setEnabled(false);
                }
            }
        });
        
        // Double-click to load contact
        contactTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedRow = contactTable.getSelectedRow();
                    if (selectedRow >= 0) {
                        loadContactToForm(selectedRow);
                    }
                }
            }
        });
        
        // Enter key to add contact
        nameField.addActionListener(e -> addContact());
        phoneField.addActionListener(e -> addContact());
    }
    
    private void addContact() {
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();
        String address = addressField.getText().trim();
        String category = (String) categoryCombo.getSelectedItem();
        
        // Validation
        if (name.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Name and Phone are required fields!",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Check if phone number already exists
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if (tableModel.getValueAt(i, 2).equals(phone)) {
                JOptionPane.showMessageDialog(this,
                    "Phone number already exists!",
                    "Duplicate Entry",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        
        // Add to table
        Object[] row = {nextId++, name, phone, email, address, category};
        tableModel.addRow(row);
        
        clearForm();
        updateCount();
        updateStatus("Contact added successfully!");
        
        // Scroll to new row
        int lastRow = tableModel.getRowCount() - 1;
        contactTable.scrollRectToVisible(contactTable.getCellRect(lastRow, 0, true));
    }
    
    private void editContact() {
        int selectedRow = contactTable.getSelectedRow();
        if (selectedRow >= 0) {
            String name = nameField.getText().trim();
            String phone = phoneField.getText().trim();
            String email = emailField.getText().trim();
            String address = addressField.getText().trim();
            String category = (String) categoryCombo.getSelectedItem();
            
            // Validation
            if (name.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Name and Phone are required fields!",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Check if phone number already exists (excluding current contact)
            int currentId = (Integer) tableModel.getValueAt(selectedRow, 0);
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                if (i != selectedRow && tableModel.getValueAt(i, 2).equals(phone)) {
                    JOptionPane.showMessageDialog(this,
                        "Phone number already exists for another contact!",
                        "Duplicate Entry",
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }
            
            int confirm = JOptionPane.showConfirmDialog(this,
                "Update this contact?",
                "Confirm Edit",
                JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                // Update table
                tableModel.setValueAt(name, selectedRow, 1);
                tableModel.setValueAt(phone, selectedRow, 2);
                tableModel.setValueAt(email, selectedRow, 3);
                tableModel.setValueAt(address, selectedRow, 4);
                tableModel.setValueAt(category, selectedRow, 5);
                
                clearForm();
                updateStatus("Contact updated successfully!");
            }
        }
    }
    
    private void deleteContact() {
        int selectedRow = contactTable.getSelectedRow();
        if (selectedRow >= 0) {
            String name = (String) tableModel.getValueAt(selectedRow, 1);
            int confirm = JOptionPane.showConfirmDialog(this,
                "Delete contact: " + name + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            
            if (confirm == JOptionPane.YES_OPTION) {
                tableModel.removeRow(selectedRow);
                clearForm();
                updateCount();
                updateStatus("Contact deleted successfully!");
                
                if (tableModel.getRowCount() == 0) {
                    editButton.setEnabled(false);
                    deleteButton.setEnabled(false);
                }
            }
        }
    }
    
    private void searchContacts() {
        String searchTerm = searchField.getText().trim().toLowerCase();
        
        if (searchTerm.isEmpty()) {
            // Clear filter
            tableModel.setRowCount(0);
            loadSampleData();
            updateCount();
            updateStatus("Showing all contacts");
            return;
        }
        
        // Filter table
        DefaultTableModel filteredModel = new DefaultTableModel(columns, 0);
        
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String id = tableModel.getValueAt(i, 0).toString();
            String name = tableModel.getValueAt(i, 1).toString().toLowerCase();
            String phone = tableModel.getValueAt(i, 2).toString().toLowerCase();
            String email = tableModel.getValueAt(i, 3).toString().toLowerCase();
            String address = tableModel.getValueAt(i, 4).toString().toLowerCase();
            String category = tableModel.getValueAt(i, 5).toString().toLowerCase();
            
            if (name.contains(searchTerm) || phone.contains(searchTerm) || 
                email.contains(searchTerm) || address.contains(searchTerm) || 
                category.contains(searchTerm)) {
                Object[] row = {
                    tableModel.getValueAt(i, 0),
                    tableModel.getValueAt(i, 1),
                    tableModel.getValueAt(i, 2),
                    tableModel.getValueAt(i, 3),
                    tableModel.getValueAt(i, 4),
                    tableModel.getValueAt(i, 5)
                };
                filteredModel.addRow(row);
            }
        }
        
        contactTable.setModel(filteredModel);
        updateCount(filteredModel.getRowCount());
        updateStatus("Found " + filteredModel.getRowCount() + " contact(s)");
        
        // Reset button to show all
        JButton resetButton = new JButton("Show All");
        resetButton.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        resetButton.addActionListener(e -> {
            contactTable.setModel(tableModel);
            searchField.setText("");
            updateCount();
            updateStatus("Showing all contacts");
        });
        
        // Add reset button to status bar temporarily
        JPanel statusPanel = (JPanel) statusLabel.getParent();
        if (statusPanel.getComponentCount() > 2) {
            statusPanel.remove(2);
        }
        statusPanel.add(resetButton, BorderLayout.CENTER);
        statusPanel.revalidate();
    }
    
    private void loadContactToForm(int row) {
        idField.setText(tableModel.getValueAt(row, 0).toString());
        nameField.setText(tableModel.getValueAt(row, 1).toString());
        phoneField.setText(tableModel.getValueAt(row, 2).toString());
        emailField.setText(tableModel.getValueAt(row, 3).toString());
        addressField.setText(tableModel.getValueAt(row, 4).toString());
        categoryCombo.setSelectedItem(tableModel.getValueAt(row, 5).toString());
    }
    
    private void clearForm() {
        idField.setText("");
        nameField.setText("");
        phoneField.setText("");
        emailField.setText("");
        addressField.setText("");
        categoryCombo.setSelectedIndex(0);
        
        contactTable.clearSelection();
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
        
        nameField.requestFocus();
    }
    
    private void updateCount() {
        int count = tableModel.getRowCount();
        countLabel.setText("Total Contacts: " + count);
    }
    
    private void updateCount(int count) {
        countLabel.setText("Total Contacts: " + count);
    }
    
    private void updateStatus(String message) {
        String timestamp = new java.text.SimpleDateFormat("HH:mm:ss").format(new Date());
        statusLabel.setText("● " + timestamp + " - " + message);
        
        
        Timer timer = new Timer(3000, e -> {
            statusLabel.setText("● Ready");
        });
        timer.setRepeats(false);
        timer.start();
    }
    
    private void loadSampleData() {
        // Sample contacts
        Object[][] sampleContacts = {
            {nextId++, "Rahul Sharma", "9876543210", "rahul@email.com", "Mumbai, Maharashtra", "Personal"},
            {nextId++, "Priya Patel", "9876543211", "priya@email.com", "Delhi, NCR", "Friend"},
            {nextId++, "Amit Kumar", "9876543212", "amit@email.com", "Bangalore, Karnataka", "Business"},
            {nextId++, "Neha Singh", "9876543213", "neha@email.com", "Pune, Maharashtra", "Family"},
            {nextId++, "Rajesh Gupta", "9876543214", "rajesh@email.com", "Chennai, Tamil Nadu", "Work"},
            {nextId++, "Sunita Verma", "9876543215", "sunita@email.com", "Kolkata, West Bengal", "Personal"},
            {nextId++, "Vikram Mehta", "9876543216", "vikram@email.com", "Ahmedabad, Gujarat", "Business"},
            {nextId++, "Anjali Desai", "9876543217", "anjali@email.com", "Hyderabad, Telangana", "Friend"},
            {nextId++, "Sanjay Gupta", "9876543218", "sanjay@email.com", "Jaipur, Rajasthan", "Work"},
            {nextId++, "Kavita Reddy", "9876543219", "kavita@email.com", "Mumbai, Maharashtra", "Family"}
        };
        
        for (Object[] contact : sampleContacts) {
            tableModel.addRow(contact);
        }
        
        updateCount();
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> new AddressBook());
    }
}