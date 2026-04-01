package earlyintermediate;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;
import java.util.*;

public class PizzaOrderSystem extends JFrame {
    
    // Menu Items with prices
    private MenuItem[] pizzas = {
        new MenuItem("Margherita Pizza", 299, "Classic tomato sauce, mozzarella, basil"),
        new MenuItem("Pepperoni Pizza", 399, "Pepperoni, mozzarella, tomato sauce"),
        new MenuItem("Farmhouse Pizza", 449, "Corn, capsicum, onion, mushroom"),
        new MenuItem("Veg Extravaganza", 499, "Onion, capsicum, mushroom, corn, olives"),
        new MenuItem("Chicken Dominator", 549, "Chicken tikka, grilled chicken, chicken sausage")
    };
    
    private MenuItem[] beverages = {
        new MenuItem("Coca Cola", 50, "330ml"),
        new MenuItem("Pepsi", 50, "330ml"),
        new MenuItem("Fresh Lime Soda", 80, "Regular"),
        new MenuItem("Cold Coffee", 120, "With ice cream"),
        new MenuItem("Mineral Water", 30, "1 liter")
    };
    
    private MenuItem[] sides = {
        new MenuItem("Garlic Bread", 120, "4 pieces with cheese"),
        new MenuItem("Cheese Sticks", 150, "6 pieces with dip"),
        new MenuItem("French Fries", 99, "Regular"),
        new MenuItem("Chicken Wings", 199, "6 pieces spicy"),
        new MenuItem("Onion Rings", 110, "Crispy onion rings")
    };
    
    // UI Components
    private JCheckBox[] pizzaCheckboxes;
    private JSpinner[] pizzaQuantities;
    private JCheckBox[] beverageCheckboxes;
    private JSpinner[] beverageQuantities;
    private JCheckBox[] sidesCheckboxes;
    private JSpinner[] sidesQuantities;
    
    private JRadioButton dineInRadio, takeawayRadio, deliveryRadio;
    private ButtonGroup orderTypeGroup;
    
    private JComboBox<String> crustCombo;
    private JComboBox<String> sizeCombo;
    private JTextArea billArea;
    private JLabel subtotalLabel, taxLabel, deliveryChargeLabel, totalLabel;
    private JButton generateBillButton, clearButton, printButton;
    private JPanel billPanel;
    
    // Tax and charges
    private final double GST_RATE = 0.05; // 5% GST
    private final double DELIVERY_CHARGE = 40;
    
    // Colors
    private Color primaryColor = new Color(255, 87, 34);
    private Color secondaryColor = new Color(255, 193, 7);
    private Color bgColor = new Color(255, 248, 225);
    
    public PizzaOrderSystem() {
        setTitle("🍕 Pizza Palace - Order Management System");
        setSize(1200, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initializeComponents();
        setupLayout();
        setupListeners();
        
        setVisible(true);
    }
    
    private void initializeComponents() {
        // Initialize arrays
        pizzaCheckboxes = new JCheckBox[pizzas.length];
        pizzaQuantities = new JSpinner[pizzas.length];
        beverageCheckboxes = new JCheckBox[beverages.length];
        beverageQuantities = new JSpinner[beverages.length];
        sidesCheckboxes = new JCheckBox[sides.length];
        sidesQuantities = new JSpinner[sides.length];
        
        // Create checkboxes and quantity spinners for pizzas
        for (int i = 0; i < pizzas.length; i++) {
            pizzaCheckboxes[i] = new JCheckBox(pizzas[i].getName() + " - ₹" + pizzas[i].getPrice());
            pizzaCheckboxes[i].setFont(new Font("Segoe UI", Font.PLAIN, 12));
            pizzaQuantities[i] = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
            pizzaQuantities[i].setEnabled(false);
        }
        
        // Create checkboxes and quantity spinners for beverages
        for (int i = 0; i < beverages.length; i++) {
            beverageCheckboxes[i] = new JCheckBox(beverages[i].getName() + " - ₹" + beverages[i].getPrice());
            beverageCheckboxes[i].setFont(new Font("Segoe UI", Font.PLAIN, 12));
            beverageQuantities[i] = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
            beverageQuantities[i].setEnabled(false);
        }
        
        // Create checkboxes and quantity spinners for sides
        for (int i = 0; i < sides.length; i++) {
            sidesCheckboxes[i] = new JCheckBox(sides[i].getName() + " - ₹" + sides[i].getPrice());
            sidesCheckboxes[i].setFont(new Font("Segoe UI", Font.PLAIN, 12));
            sidesQuantities[i] = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
            sidesQuantities[i].setEnabled(false);
        }
        
        // Order type radio buttons
        dineInRadio = new JRadioButton("🏠 Dine In", true);
        takeawayRadio = new JRadioButton("📦 Take Away");
        deliveryRadio = new JRadioButton("🚚 Delivery");
        orderTypeGroup = new ButtonGroup();
        orderTypeGroup.add(dineInRadio);
        orderTypeGroup.add(takeawayRadio);
        orderTypeGroup.add(deliveryRadio);
        
        // Pizza customization
        crustCombo = new JComboBox<>(new String[]{"Regular Crust", "Thin Crust", "Cheese Burst +₹50", "Pan Crust"});
        sizeCombo = new JComboBox<>(new String[]{"Regular", "Medium +₹50", "Large +₹100"});
        
        // Bill area
        billArea = new JTextArea();
        billArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        billArea.setEditable(false);
        billArea.setBackground(Color.WHITE);
        billArea.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        
        // Labels
        subtotalLabel = new JLabel("₹0.00");
        subtotalLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        taxLabel = new JLabel("₹0.00");
        taxLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        deliveryChargeLabel = new JLabel("₹0.00");
        deliveryChargeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        totalLabel = new JLabel("₹0.00");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        totalLabel.setForeground(primaryColor);
        
        // Buttons
        generateBillButton = createStyledButton("💰 Generate Bill", new Color(76, 175, 80));
        clearButton = createStyledButton("🗑️ Clear Order", new Color(244, 67, 54));
        printButton = createStyledButton("🖨️ Print Bill", new Color(33, 150, 243));
        printButton.setEnabled(false);
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
        
        // Main Content Panel
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(600);
        splitPane.setResizeWeight(0.6);
        
        // Left Panel - Menu Items
        JPanel leftPanel = createMenuPanel();
        
        // Right Panel - Bill
        JPanel rightPanel = createBillPanel();
        
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
        
        JLabel title = new JLabel("🍕 PIZZA PALACE");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(Color.WHITE);
        
        JLabel tagline = new JLabel("Order your favorite pizza online!");
        tagline.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tagline.setForeground(new Color(255, 255, 255, 200));
        
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(title, BorderLayout.NORTH);
        titlePanel.add(tagline, BorderLayout.SOUTH);
        
        header.add(titlePanel, BorderLayout.WEST);
        
        // Order Type Panel
        JPanel orderTypePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        orderTypePanel.setOpaque(false);
        
        Font radioFont = new Font("Segoe UI", Font.BOLD, 12);
        dineInRadio.setFont(radioFont);
        takeawayRadio.setFont(radioFont);
        deliveryRadio.setFont(radioFont);
        
        dineInRadio.setForeground(Color.WHITE);
        takeawayRadio.setForeground(Color.WHITE);
        deliveryRadio.setForeground(Color.WHITE);
        
        dineInRadio.setOpaque(false);
        takeawayRadio.setOpaque(false);
        deliveryRadio.setOpaque(false);
        
        orderTypePanel.add(dineInRadio);
        orderTypePanel.add(takeawayRadio);
        orderTypePanel.add(deliveryRadio);
        
        header.add(orderTypePanel, BorderLayout.EAST);
        
        return header;
    }
    
    private JPanel createMenuPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(bgColor);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Pizzas Section
        panel.add(createMenuSection("🍕 PIZZAS", pizzas, pizzaCheckboxes, pizzaQuantities));
        panel.add(Box.createVerticalStrut(15));
        
        // Beverages Section
        panel.add(createMenuSection("🥤 BEVERAGES", beverages, beverageCheckboxes, beverageQuantities));
        panel.add(Box.createVerticalStrut(15));
        
        // Sides Section
        panel.add(createMenuSection("🍟 SIDES", sides, sidesCheckboxes, sidesQuantities));
        panel.add(Box.createVerticalStrut(15));
        
        // Customization Section
        panel.add(createCustomizationPanel());
        
        return panel;
    }
    
    private JPanel createMenuSection(String title, MenuItem[] items, JCheckBox[] checkboxes, JSpinner[] spinners) {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(Color.WHITE);
        section.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(primaryColor);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        section.add(titleLabel);
        section.add(Box.createVerticalStrut(10));
        
        for (int i = 0; i < items.length; i++) {
            JPanel itemPanel = new JPanel(new BorderLayout(10, 5));
            itemPanel.setBackground(Color.WHITE);
            itemPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
            
            // Tooltip with description
            checkboxes[i].setToolTipText(items[i].getDescription());
            checkboxes[i].setBackground(Color.WHITE);
            
            JPanel quantityPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
            quantityPanel.setBackground(Color.WHITE);
            quantityPanel.add(new JLabel("Qty:"));
            quantityPanel.add(spinners[i]);
            
            itemPanel.add(checkboxes[i], BorderLayout.CENTER);
            itemPanel.add(quantityPanel, BorderLayout.EAST);
            
            section.add(itemPanel);
            
            // Add listener to enable/disable spinner
            final int index = i;
            checkboxes[i].addActionListener(e -> {
                spinners[index].setEnabled(checkboxes[index].isSelected());
                updateBill();
            });
            spinners[i].addChangeListener(e -> updateBill());
        }
        
        return section;
    }
    
    private JPanel createCustomizationPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 2, 10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        JLabel crustLabel = new JLabel("Select Crust:");
        crustLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panel.add(crustLabel);
        panel.add(crustCombo);
        
        JLabel sizeLabel = new JLabel("Select Size:");
        sizeLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panel.add(sizeLabel);
        panel.add(sizeCombo);
        
        crustCombo.addActionListener(e -> updateBill());
        sizeCombo.addActionListener(e -> updateBill());
        
        return panel;
    }
    
    private JPanel createBillPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel billTitle = new JLabel("🧾 ORDER SUMMARY");
        billTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        billTitle.setForeground(primaryColor);
        billTitle.setHorizontalAlignment(SwingConstants.CENTER);
        
        JScrollPane scrollPane = new JScrollPane(billArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        
        JPanel calculationPanel = new JPanel(new GridLayout(4, 2, 10, 5));
        calculationPanel.setBackground(Color.WHITE);
        calculationPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        calculationPanel.add(new JLabel("Subtotal:"));
        calculationPanel.add(subtotalLabel);
        calculationPanel.add(new JLabel("GST (5%):"));
        calculationPanel.add(taxLabel);
        calculationPanel.add(new JLabel("Delivery Charge:"));
        calculationPanel.add(deliveryChargeLabel);
        
        JSeparator separator = new JSeparator();
        
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalPanel.setBackground(Color.WHITE);
        totalPanel.add(new JLabel("TOTAL AMOUNT:"));
        totalPanel.add(totalLabel);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(generateBillButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(printButton);
        
        panel.add(billTitle, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(calculationPanel, BorderLayout.SOUTH);
        panel.add(separator, BorderLayout.SOUTH);
        panel.add(totalPanel, BorderLayout.SOUTH);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createFooterPanel() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setBackground(new Color(240, 240, 240));
        footer.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        
        JLabel footerText = new JLabel("© 2024 Pizza Palace | All rights reserved | Enjoy your meal!");
        footerText.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        footerText.setForeground(new Color(100, 100, 100));
        footer.add(footerText);
        
        return footer;
    }
    
    private void setupListeners() {
        generateBillButton.addActionListener(e -> generateBill());
        clearButton.addActionListener(e -> clearOrder());
        printButton.addActionListener(e -> printBill());
        
        dineInRadio.addActionListener(e -> updateBill());
        takeawayRadio.addActionListener(e -> updateBill());
        deliveryRadio.addActionListener(e -> updateBill());
    }
    
    private void updateBill() {
        double subtotal = calculateSubtotal();
        double tax = subtotal * GST_RATE;
        double deliveryCharge = deliveryRadio.isSelected() ? DELIVERY_CHARGE : 0;
        double total = subtotal + tax + deliveryCharge;
        
        subtotalLabel.setText("₹" + String.format("%.2f", subtotal));
        taxLabel.setText("₹" + String.format("%.2f", tax));
        deliveryChargeLabel.setText(deliveryRadio.isSelected() ? "₹" + DELIVERY_CHARGE : "₹0.00");
        totalLabel.setText("₹" + String.format("%.2f", total));
    }
    
    private double calculateSubtotal() {
        double subtotal = 0;
        
        // Calculate pizzas
        for (int i = 0; i < pizzas.length; i++) {
            if (pizzaCheckboxes[i].isSelected()) {
                int quantity = (Integer) pizzaQuantities[i].getValue();
                double price = pizzas[i].getPrice();
                
                // Add extra charges for size
                int sizeIndex = sizeCombo.getSelectedIndex();
                if (sizeIndex == 1) price += 50;
                if (sizeIndex == 2) price += 100;
                
                // Add extra for cheese burst crust
                if (crustCombo.getSelectedIndex() == 2) price += 50;
                
                subtotal += price * quantity;
            }
        }
        
        // Calculate beverages
        for (int i = 0; i < beverages.length; i++) {
            if (beverageCheckboxes[i].isSelected()) {
                int quantity = (Integer) beverageQuantities[i].getValue();
                subtotal += beverages[i].getPrice() * quantity;
            }
        }
        
        // Calculate sides
        for (int i = 0; i < sides.length; i++) {
            if (sidesCheckboxes[i].isSelected()) {
                int quantity = (Integer) sidesQuantities[i].getValue();
                subtotal += sides[i].getPrice() * quantity;
            }
        }
        
        return subtotal;
    }
    
    private void generateBill() {
        StringBuilder bill = new StringBuilder();
        bill.append("=".repeat(50)).append("\n");
        bill.append(String.format("%40s\n", "PIZZA PALACE"));
        bill.append(String.format("%38s\n", "Order Bill"));
        bill.append("=".repeat(50)).append("\n\n");
        
        // Order Type
        String orderType = dineInRadio.isSelected() ? "Dine In" : 
                          (takeawayRadio.isSelected() ? "Take Away" : "Delivery");
        bill.append("Order Type: ").append(orderType).append("\n");
        bill.append("Date & Time: ").append(new Date()).append("\n\n");
        
        // Items
        boolean hasItems = false;
        
        // Pizzas
        for (int i = 0; i < pizzas.length; i++) {
            if (pizzaCheckboxes[i].isSelected()) {
                hasItems = true;
                int quantity = (Integer) pizzaQuantities[i].getValue();
                double price = pizzas[i].getPrice();
                
                // Add customization
                int sizeIndex = sizeCombo.getSelectedIndex();
                if (sizeIndex == 1) price += 50;
                if (sizeIndex == 2) price += 100;
                if (crustCombo.getSelectedIndex() == 2) price += 50;
                
                double total = price * quantity;
                bill.append(String.format("%-30s x%d @ ₹%.2f = ₹%.2f\n", 
                    pizzas[i].getName(), quantity, price, total));
            }
        }
        
        // Beverages
        for (int i = 0; i < beverages.length; i++) {
            if (beverageCheckboxes[i].isSelected()) {
                hasItems = true;
                int quantity = (Integer) beverageQuantities[i].getValue();
                double total = beverages[i].getPrice() * quantity;
                bill.append(String.format("%-30s x%d @ ₹%.2f = ₹%.2f\n", 
                    beverages[i].getName(), quantity, beverages[i].getPrice(), total));
            }
        }
        
        // Sides
        for (int i = 0; i < sides.length; i++) {
            if (sidesCheckboxes[i].isSelected()) {
                hasItems = true;
                int quantity = (Integer) sidesQuantities[i].getValue();
                double total = sides[i].getPrice() * quantity;
                bill.append(String.format("%-30s x%d @ ₹%.2f = ₹%.2f\n", 
                    sides[i].getName(), quantity, sides[i].getPrice(), total));
            }
        }
        
        if (!hasItems) {
            bill.append("No items selected!\n");
        }
        
        bill.append("\n").append("-".repeat(50)).append("\n");
        
        double subtotal = calculateSubtotal();
        double tax = subtotal * GST_RATE;
        double deliveryCharge = deliveryRadio.isSelected() ? DELIVERY_CHARGE : 0;
        double total = subtotal + tax + deliveryCharge;
        
        bill.append(String.format("%-30s ₹%.2f\n", "Subtotal:", subtotal));
        bill.append(String.format("%-30s ₹%.2f\n", "GST (5%):", tax));
        if (deliveryRadio.isSelected()) {
            bill.append(String.format("%-30s ₹%.2f\n", "Delivery Charge:", deliveryCharge));
        }
        bill.append("-".repeat(50)).append("\n");
        bill.append(String.format("%-30s ₹%.2f\n", "TOTAL:", total));
        bill.append("=".repeat(50)).append("\n");
        bill.append("\nThank you for ordering! 🍕\n");
        bill.append("Please visit again!\n");
        
        billArea.setText(bill.toString());
        printButton.setEnabled(true);
    }
    
    private void clearOrder() {
        // Clear all checkboxes and reset quantities
        for (JCheckBox cb : pizzaCheckboxes) {
            cb.setSelected(false);
        }
        for (JSpinner spinner : pizzaQuantities) {
            spinner.setValue(1);
            spinner.setEnabled(false);
        }
        
        for (JCheckBox cb : beverageCheckboxes) {
            cb.setSelected(false);
        }
        for (JSpinner spinner : beverageQuantities) {
            spinner.setValue(1);
            spinner.setEnabled(false);
        }
        
        for (JCheckBox cb : sidesCheckboxes) {
            cb.setSelected(false);
        }
        for (JSpinner spinner : sidesQuantities) {
            spinner.setValue(1);
            spinner.setEnabled(false);
        }
        
        // Reset customization
        crustCombo.setSelectedIndex(0);
        sizeCombo.setSelectedIndex(0);
        
        // Clear bill
        billArea.setText("");
        printButton.setEnabled(false);
        
        // Reset calculations
        updateBill();
        
        JOptionPane.showMessageDialog(this, 
            "Order cleared successfully!", 
            "Clear Order", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void printBill() {
        try {
            billArea.print();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error printing bill: " + ex.getMessage(),
                "Print Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Inner class for menu items
    class MenuItem {
        private String name;
        private double price;
        private String description;
        
        public MenuItem(String name, double price, String description) {
            this.name = name;
            this.price = price;
            this.description = description;
        }
        
        public String getName() { return name; }
        public double getPrice() { return price; }
        public String getDescription() { return description; }
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> new PizzaOrderSystem());
    }
}