package earlyintermediate;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.text.*;
import java.util.*;
import javax.swing.Timer;

public class ComboClock extends JFrame {
    
    private ClockPanel clockPanel;
    private JLabel timeLabel;
    private JLabel dateLabel;
    private JLabel dayLabel;
    private Timer timer;
    
    public ComboClock() {
        setTitle("🕐 Premium Clock");
        setSize(700, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        initializeComponents();
        setupLayout();
        startClock();
        
        setVisible(true);
    }
    
    private void initializeComponents() {
        clockPanel = new ClockPanel();
        clockPanel.setPreferredSize(new Dimension(550, 550));
        
        timeLabel = new JLabel("00:00:00");
        timeLabel.setFont(new Font("DS-Digital", Font.BOLD, 48));
        timeLabel.setForeground(new Color(0, 255, 255));
        timeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        dateLabel = new JLabel("January 1, 2024");
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        dateLabel.setForeground(Color.WHITE);
        dateLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        dayLabel = new JLabel("Monday");
        dayLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        dayLabel.setForeground(new Color(255, 200, 100));
        dayLabel.setHorizontalAlignment(SwingConstants.CENTER);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(15, 25, 45));
        
        // Digital Display Panel
        JPanel digitalPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        digitalPanel.setOpaque(false);
        digitalPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        digitalPanel.add(timeLabel);
        digitalPanel.add(dateLabel);
        digitalPanel.add(dayLabel);
        
        add(digitalPanel, BorderLayout.NORTH);
        add(clockPanel, BorderLayout.CENTER);
        
        // Footer
        JPanel footerPanel = new JPanel();
        footerPanel.setOpaque(false);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        
        String[] info = {"🎨 Live Analog Clock", "⏰ Real-time Digital Display", "✨ Smooth Animation"};
        for (String text : info) {
            JLabel label = new JLabel(text);
            label.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            label.setForeground(new Color(150, 150, 150));
            footerPanel.add(label);
            footerPanel.add(Box.createHorizontalStrut(15));
        }
        
        add(footerPanel, BorderLayout.SOUTH);
    }
    
    private void startClock() {
        timer = new Timer(100, e -> {
            clockPanel.repaint();
            updateDigitalDisplay();
        });
        timer.start();
        updateDigitalDisplay();
    }
    
    private void updateDigitalDisplay() {
        Calendar calendar = Calendar.getInstance();
        
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        timeLabel.setText(timeFormat.format(calendar.getTime()));
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy");
        dateLabel.setText(dateFormat.format(calendar.getTime()));
        
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");
        dayLabel.setText(dayFormat.format(calendar.getTime()));
    }
    
    class ClockPanel extends JPanel {
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int width = getWidth();
            int height = getHeight();
            int centerX = width / 2;
            int centerY = height / 2;
            int radius = Math.min(width, height) / 2 - 30;
            
            // Draw decorative background
            drawBackground(g2d, centerX, centerY, radius);
            
            // Draw clock face
            drawClockFace(g2d, centerX, centerY, radius);
            
            // Get current time
            Calendar calendar = Calendar.getInstance();
            int hours = calendar.get(Calendar.HOUR);
            int minutes = calendar.get(Calendar.MINUTE);
            int seconds = calendar.get(Calendar.SECOND);
            
            // Draw clock hands
            drawHourHand(g2d, centerX, centerY, radius, hours, minutes);
            drawMinuteHand(g2d, centerX, centerY, radius, minutes);
            drawSecondHand(g2d, centerX, centerY, radius, seconds);
            
            // Draw center dot
            g2d.setColor(Color.WHITE);
            g2d.fillOval(centerX - 8, centerY - 8, 16, 16);
            g2d.setColor(new Color(255, 100, 100));
            g2d.fillOval(centerX - 4, centerY - 4, 8, 8);
        }
        
        private void drawBackground(Graphics2D g2d, int cx, int cy, int radius) {
            // Radial gradient background
            RadialGradientPaint rgp = new RadialGradientPaint(
                cx, cy, radius,
                new float[]{0f, 0.5f, 1f},
                new Color[]{new Color(30, 40, 60), new Color(25, 35, 55), new Color(20, 30, 50)}
            );
            g2d.setPaint(rgp);
            g2d.fillOval(cx - radius - 10, cy - radius - 10, (radius + 10) * 2, (radius + 10) * 2);
        }
        
        private void drawClockFace(Graphics2D g2d, int cx, int cy, int radius) {
            // Outer circle with glow effect
            g2d.setColor(new Color(100, 100, 150, 100));
            g2d.setStroke(new BasicStroke(3));
            g2d.drawOval(cx - radius, cy - radius, radius * 2, radius * 2);
            
            // Main clock face
            g2d.setColor(new Color(40, 50, 70));
            g2d.fillOval(cx - radius + 2, cy - radius + 2, (radius - 2) * 2, (radius - 2) * 2);
            
            // Inner circle
            g2d.setColor(new Color(30, 40, 60));
            g2d.fillOval(cx - radius + 8, cy - radius + 8, (radius - 8) * 2, (radius - 8) * 2);
            
            // Draw hour numbers with Roman numerals for elegance
            String[] romanNumerals = {"XII", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI"};
            for (int i = 1; i <= 12; i++) {
                double angle = Math.toRadians(90 - i * 30);
                int xNum = cx + (int) (radius * 0.78 * Math.cos(angle)) - 10;
                int yNum = cy - (int) (radius * 0.78 * Math.sin(angle)) - 8;
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 14));
                g2d.setColor(new Color(200, 200, 255));
                g2d.drawString(romanNumerals[i - 1], xNum, yNum + 8);
            }
            
            // Draw minute markers
            for (int i = 1; i <= 60; i++) {
                double angle = Math.toRadians(90 - i * 6);
                int x1 = cx + (int) (radius * 0.88 * Math.cos(angle));
                int y1 = cy - (int) (radius * 0.88 * Math.sin(angle));
                int x2 = cx + (int) (radius * 0.95 * Math.cos(angle));
                int y2 = cy - (int) (radius * 0.95 * Math.sin(angle));
                
                if (i % 5 == 0) {
                    g2d.setColor(new Color(255, 200, 100));
                    g2d.setStroke(new BasicStroke(2));
                } else {
                    g2d.setColor(new Color(150, 150, 180));
                    g2d.setStroke(new BasicStroke(1));
                }
                g2d.drawLine(x1, y1, x2, y2);
            }
        }
        
        private void drawHourHand(Graphics2D g2d, int cx, int cy, int radius, int hours, int minutes) {
            double angle = Math.toRadians(90 - (hours * 30 + minutes * 0.5));
            int length = radius / 2;
            int x = cx + (int) (length * Math.cos(angle));
            int y = cy - (int) (length * Math.sin(angle));
            
            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(6, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2d.drawLine(cx, cy, x, y);
        }
        
        private void drawMinuteHand(Graphics2D g2d, int cx, int cy, int radius, int minutes) {
            double angle = Math.toRadians(90 - minutes * 6);
            int length = (int) (radius * 0.68);
            int x = cx + (int) (length * Math.cos(angle));
            int y = cy - (int) (length * Math.sin(angle));
            
            g2d.setColor(new Color(200, 200, 255));
            g2d.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2d.drawLine(cx, cy, x, y);
        }
        
        private void drawSecondHand(Graphics2D g2d, int cx, int cy, int radius, int seconds) {
            double angle = Math.toRadians(90 - seconds * 6);
            int length = (int) (radius * 0.82);
            int x = cx + (int) (length * Math.cos(angle));
            int y = cy - (int) (length * Math.sin(angle));
            
            g2d.setColor(new Color(255, 100, 100));
            g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2d.drawLine(cx, cy, x, y);
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ComboClock());
    }
}