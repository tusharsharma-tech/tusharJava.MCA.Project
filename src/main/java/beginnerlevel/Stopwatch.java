package beginnerlevel; 

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;
import java.text.*;
import java.util.*;
import java.io.*; // Added for exporting laps
import javax.swing.Timer;

public class Stopwatch extends JFrame {
    
    // Timer variables
    private Timer timer;
    private long startTime;
    private long elapsedTime = 0;
    private boolean isRunning = false;
    
    // UI Components
    private JLabel timeDisplay;
    private JButton startButton, stopButton, resetButton, lapButton;
    private JList<String> lapList;
    private DefaultListModel<String> lapListModel;
    private JScrollPane lapScrollPane;
    private JPanel controlPanel, displayPanel, lapPanel;
    private JLabel statusLabel;
    private JLabel lapCountLabel;
    
    // Formatting
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss.SSS");
    private NumberFormat numberFormat = NumberFormat.getInstance();
    
    // Colors
    private Color backgroundColor = new Color(240, 248, 255);
    private Color displayColor = new Color(33, 33, 33);
    private Color lapColor = new Color(255, 255, 255);
    
    public Stopwatch() {
        setTitle("Professional Stopwatch - MCA Project");
        setSize(500, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        initializeComponents();
        setupLayout();
        setupListeners();
        
        setVisible(true);
    }
    
    private void initializeComponents() {
        // Time Display
        timeDisplay = new JLabel("00:00:00.000");
        // Tip: Agar DS-Digital font nahi hai toh ye Monospaced dikhega
        timeDisplay.setFont(new Font("Monospaced", Font.BOLD, 55));
        timeDisplay.setForeground(Color.WHITE);
        timeDisplay.setBackground(displayColor);
        timeDisplay.setOpaque(true);
        timeDisplay.setHorizontalAlignment(SwingConstants.CENTER);
        timeDisplay.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 100, 100), 2),
            BorderFactory.createEmptyBorder(20, 10, 20, 10)
        ));
        
        // Buttons
        startButton = createStyledButton("▶ Start", new Color(76, 175, 80));
        stopButton = createStyledButton("⏸ Stop", new Color(244, 67, 54));
        resetButton = createStyledButton("⟳ Reset", new Color(255, 152, 0));
        lapButton = createStyledButton("⏱ Lap", new Color(33, 150, 243));
        
        stopButton.setEnabled(false);
        resetButton.setEnabled(false);
        lapButton.setEnabled(false);
        
        // Lap List
        lapListModel = new DefaultListModel<>();
        lapList = new JList<>(lapListModel);
        lapList.setFont(new Font("Monospaced", Font.PLAIN, 13));
        lapList.setBackground(lapColor);
        lapList.setFixedCellHeight(30);
        
        lapList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, 
                    int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (!isSelected) {
                    c.setBackground(index % 2 == 0 ? new Color(245, 245, 245) : Color.WHITE);
                }
                return c;
            }
        });
        
        lapScrollPane = new JScrollPane(lapList);
        lapScrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)), "Lap Times Log"));
        
        statusLabel = new JLabel("● Ready");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lapCountLabel = new JLabel("Laps: 0");
        lapCountLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        timer = new Timer(10, e -> updateDisplay());
    }
    
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { if(button.isEnabled()) button.setBackground(bgColor.darker()); }
            public void mouseExited(MouseEvent e) { button.setBackground(bgColor); }
        });
        return button;
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(backgroundColor);
        
        // Top: Display
        displayPanel = new JPanel(new BorderLayout());
        displayPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 10, 25));
        displayPanel.setBackground(backgroundColor);
        displayPanel.add(timeDisplay, BorderLayout.CENTER);
        
        // Center: Buttons + Laps (Fixed Overlap)
        JPanel centerContainer = new JPanel(new BorderLayout(0, 20));
        centerContainer.setBackground(backgroundColor);
        centerContainer.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));

        controlPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        controlPanel.setBackground(backgroundColor);
        controlPanel.add(startButton); controlPanel.add(stopButton);
        controlPanel.add(resetButton); controlPanel.add(lapButton);
        
        lapPanel = new JPanel(new BorderLayout());
        lapPanel.setBackground(backgroundColor);
        lapScrollPane.setPreferredSize(new Dimension(400, 280));
        lapPanel.add(lapScrollPane, BorderLayout.CENTER);

        centerContainer.add(controlPanel, BorderLayout.NORTH);
        centerContainer.add(lapPanel, BorderLayout.CENTER);
        
        // Bottom: Status
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(backgroundColor);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 25, 15, 25));
        
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        infoPanel.setBackground(backgroundColor);
        infoPanel.add(statusLabel); infoPanel.add(lapCountLabel);
        bottomPanel.add(infoPanel, BorderLayout.WEST);
        
        add(displayPanel, BorderLayout.NORTH);
        add(centerContainer, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void setupListeners() {
        startButton.addActionListener(e -> startStopwatch());
        stopButton.addActionListener(e -> stopStopwatch());
        resetButton.addActionListener(e -> resetStopwatch());
        lapButton.addActionListener(e -> recordLap());
        setupKeyboardShortcuts();
    }

    private void setupKeyboardShortcuts() {
        InputMap im = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getRootPane().getActionMap();
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "toggle");
        am.put("toggle", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { if(isRunning) stopStopwatch(); else startStopwatch(); }
        });
    }

    private void startStopwatch() {
        if (!isRunning) {
            startTime = System.currentTimeMillis() - elapsedTime;
            timer.start();
            isRunning = true;
            updateButtons(false, true, true, true);
            statusLabel.setText("● Running");
            statusLabel.setForeground(new Color(76, 175, 80));
        }
    }

    private void stopStopwatch() {
        if (isRunning) {
            timer.stop();
            isRunning = false;
            elapsedTime = System.currentTimeMillis() - startTime;
            updateButtons(true, false, true, false);
            statusLabel.setText("● Stopped");
            statusLabel.setForeground(new Color(244, 67, 54));
        }
    }

    private void resetStopwatch() {
        timer.stop();
        isRunning = false;
        elapsedTime = 0;
        updateDisplay();
        lapListModel.clear();
        updateLapCount();
        updateButtons(true, false, false, false);
        statusLabel.setText("● Ready");
        statusLabel.setForeground(Color.GRAY);
    }

    private void recordLap() {
        if (isRunning) {
            String entry = String.format("Lap %02d:  %s", (lapListModel.size() + 1), formatTime(elapsedTime));
            lapListModel.addElement(entry);
            lapList.ensureIndexIsVisible(lapListModel.size() - 1);
            updateLapCount();
        }
    }

    private void updateButtons(boolean start, boolean stop, boolean reset, boolean lap) {
        startButton.setEnabled(start);
        stopButton.setEnabled(stop);
        resetButton.setEnabled(reset);
        lapButton.setEnabled(lap);
    }

    private void updateDisplay() {
        if (isRunning) elapsedTime = System.currentTimeMillis() - startTime;
        timeDisplay.setText(formatTime(elapsedTime));
    }

    private String formatTime(long ms) {
        long h = ms / 3600000;
        long m = (ms % 3600000) / 60000;
        long s = (ms % 60000) / 1000;
        long mils = ms % 1000;
        return String.format("%02d:%02d:%02d.%03d", h, m, s, mils);
    }

    private void updateLapCount() { lapCountLabel.setText("Laps Recorded: " + lapListModel.size()); }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
        SwingUtilities.invokeLater(Stopwatch::new);
    }
}