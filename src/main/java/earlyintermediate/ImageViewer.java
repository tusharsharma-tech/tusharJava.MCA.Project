package earlyintermediate;
import javax.swing.Timer;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.util.ArrayList;

public class ImageViewer extends JFrame {
    
    // Components
    private JLabel imageLabel;
    private JButton loadBtn, prevBtn, nextBtn, zoomInBtn, zoomOutBtn, fitBtn, slideshowBtn;
    private JLabel fileNameLabel, dimensionsLabel, zoomLabel, countLabel;
    private JProgressBar progressBar;
    private Timer slideshowTimer;
    private boolean slideshowRunning = false;
    
    // Image Data
    private ArrayList<File> imageList;
    private int currentIndex;
    private BufferedImage currentImage;
    private double zoom;
    
    // Colors
    private Color darkBg = new Color(18, 18, 24);
    private Color glassBg = new Color(255, 255, 255, 15);
    private Color glassBorder = new Color(255, 255, 255, 50);
    private Color glassText = new Color(255, 255, 255);
    private Color accentCyan = new Color(0, 200, 255);
    private Color accentPink = new Color(255, 80, 150);
    private Color accentGreen = new Color(0, 255, 100);
    private Color accentPurple = new Color(150, 100, 255);
    private Color accentOrange = new Color(255, 100, 50);
    
    public ImageViewer() {
        setTitle("✨ AURORA IMAGE VIEWER ✨");
        setSize(1100, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        imageList = new ArrayList<>();
        currentIndex = -1;
        zoom = 1.0;
        
        setupUI();
        setupSlideshow();
        
        setVisible(true);
    }
    
    private void setupUI() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(darkBg);
        
        // Main Panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Header
        mainPanel.add(createHeader(), BorderLayout.NORTH);
        
        // Center Image Panel
        mainPanel.add(createImagePanel(), BorderLayout.CENTER);
        
        // Bottom Panel
        mainPanel.add(createBottomPanel(), BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        // Left - Title
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        titlePanel.setOpaque(false);
        
        JLabel titleIcon = new JLabel("✨");
        titleIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        
        JLabel title = new JLabel("AURORA VIEWER");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(glassText);
        
        JLabel subtitle = new JLabel("|  Image Viewer Pro");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        subtitle.setForeground(new Color(200, 200, 255));
        
        titlePanel.add(titleIcon);
        titlePanel.add(title);
        titlePanel.add(subtitle);
        
        // Right - Stats
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);
        
        countLabel = new JLabel("📁 0 images");
        countLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        countLabel.setForeground(accentCyan);
        countLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        rightPanel.add(countLabel);
        
        header.add(titlePanel, BorderLayout.WEST);
        header.add(rightPanel, BorderLayout.EAST);
        
        return header;
    }
    
    private JPanel createImagePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        
        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        imageLabel.setVerticalAlignment(JLabel.CENTER);
        imageLabel.setBackground(new Color(30, 30, 40));
        imageLabel.setOpaque(true);
        imageLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 100, 150), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        // Placeholder
        imageLabel.setText("✨ NO IMAGE LOADED ✨\n\nClick 'Load Image' to begin\n\nSupported: JPG, PNG, GIF, BMP");
        imageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        imageLabel.setForeground(Color.LIGHT_GRAY);
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        
        JScrollPane scrollPane = new JScrollPane(imageLabel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(new Color(30, 30, 40));
        scrollPane.getHorizontalScrollBar().setUnitIncrement(20);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createBottomPanel() {
        JPanel bottom = new JPanel(new BorderLayout(0, 8));
        bottom.setOpaque(false);
        bottom.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        // Control Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        buttonPanel.setOpaque(false);
        
        loadBtn = createStyledButton("📂 Load Image", new Color(76, 175, 80));
        prevBtn = createStyledButton("◀ Previous", new Color(33, 150, 243));
        nextBtn = createStyledButton("Next ▶", new Color(33, 150, 243));
        zoomInBtn = createStyledButton("🔍 Zoom In", new Color(255, 152, 0));
        zoomOutBtn = createStyledButton("🔍 Zoom Out", new Color(255, 152, 0));
        fitBtn = createStyledButton("📐 Fit to Window", new Color(156, 39, 176));
        slideshowBtn = createStyledButton("▶ Slideshow", new Color(244, 67, 54));
        
        // Disable initially
        prevBtn.setEnabled(false);
        nextBtn.setEnabled(false);
        zoomInBtn.setEnabled(false);
        zoomOutBtn.setEnabled(false);
        fitBtn.setEnabled(false);
        slideshowBtn.setEnabled(false);
        
        buttonPanel.add(loadBtn);
        buttonPanel.add(prevBtn);
        buttonPanel.add(nextBtn);
        buttonPanel.add(zoomInBtn);
        buttonPanel.add(zoomOutBtn);
        buttonPanel.add(fitBtn);
        buttonPanel.add(slideshowBtn);
        
        // Info Panel
        JPanel infoPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        infoPanel.setOpaque(false);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 5));
        
        fileNameLabel = new JLabel("📄 No file selected");
        fileNameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        fileNameLabel.setForeground(Color.WHITE);
        fileNameLabel.setBackground(new Color(0, 0, 0, 50));
        fileNameLabel.setOpaque(true);
        fileNameLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        dimensionsLabel = new JLabel("📏 -- x --");
        dimensionsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        dimensionsLabel.setForeground(Color.WHITE);
        dimensionsLabel.setBackground(new Color(0, 0, 0, 50));
        dimensionsLabel.setOpaque(true);
        dimensionsLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        zoomLabel = new JLabel("🔍 100%");
        zoomLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        zoomLabel.setForeground(Color.WHITE);
        zoomLabel.setBackground(new Color(0, 0, 0, 50));
        zoomLabel.setOpaque(true);
        zoomLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        infoPanel.add(fileNameLabel);
        infoPanel.add(dimensionsLabel);
        infoPanel.add(zoomLabel);
        
        // Progress Bar
        progressBar = new JProgressBar();
        progressBar.setVisible(false);
        progressBar.setForeground(new Color(76, 175, 80));
        progressBar.setBackground(new Color(50, 50, 60));
        progressBar.setPreferredSize(new Dimension(0, 4));
        
        bottom.add(buttonPanel, BorderLayout.NORTH);
        bottom.add(infoPanel, BorderLayout.CENTER);
        bottom.add(progressBar, BorderLayout.SOUTH);
        
        return bottom;
    }
    
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(bgColor.darker(), 1),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.darker());
                button.setBorder(BorderFactory.createLineBorder(bgColor, 1));
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
                button.setBorder(BorderFactory.createLineBorder(bgColor.darker(), 1));
            }
        });
        
        button.addActionListener(e -> {
            String btnText = button.getText();
            if (btnText.contains("Load")) loadImage();
            else if (btnText.contains("Previous")) showPrevious();
            else if (btnText.contains("Next")) showNext();
            else if (btnText.contains("Zoom In")) zoomIn();
            else if (btnText.contains("Zoom Out")) zoomOut();
            else if (btnText.contains("Fit")) fitToWindow();
            else if (btnText.contains("Slideshow")) toggleSlideshow();
        });
        
        return button;
    }
    
    private void setupSlideshow() {
        slideshowTimer = new Timer(3000, e -> {
            if (currentIndex < imageList.size() - 1) {
                loadImageAtIndex(currentIndex + 1);
            } else if (currentIndex == imageList.size() - 1 && imageList.size() > 0) {
                loadImageAtIndex(0);
            }
        });
    }
    
    private void loadImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File f) {
                if (f.isDirectory()) return true;
                String name = f.getName().toLowerCase();
                return name.endsWith(".jpg") || name.endsWith(".jpeg") || 
                       name.endsWith(".png") || name.endsWith(".gif") || 
                       name.endsWith(".bmp");
            }
            public String getDescription() {
                return "Image Files (*.jpg, *.png, *.gif, *.bmp)";
            }
        });
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            File folder = selectedFile.getParentFile();
            loadImagesFromFolder(folder);
            
            for (int i = 0; i < imageList.size(); i++) {
                if (imageList.get(i).equals(selectedFile)) {
                    loadImageAtIndex(i);
                    break;
                }
            }
        }
    }
    
    private void loadImagesFromFolder(File folder) {
        imageList.clear();
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                String name = file.getName().toLowerCase();
                if (file.isFile() && (name.endsWith(".jpg") || name.endsWith(".jpeg") || 
                    name.endsWith(".png") || name.endsWith(".gif") || name.endsWith(".bmp"))) {
                    imageList.add(file);
                }
            }
        }
        
        int count = imageList.size();
        countLabel.setText("📁 " + count + " image" + (count != 1 ? "s" : ""));
        
        if (count > 1) {
            slideshowBtn.setEnabled(true);
        }
    }
    
    private void loadImageAtIndex(int index) {
        if (index < 0 || index >= imageList.size()) return;
        
        currentIndex = index;
        File imageFile = imageList.get(currentIndex);
        
        progressBar.setVisible(true);
        progressBar.setIndeterminate(true);
        imageLabel.setText("✨ Loading... ✨");
        imageLabel.setIcon(null);
        
        SwingWorker<BufferedImage, Void> worker = new SwingWorker<BufferedImage, Void>() {
            @Override
            protected BufferedImage doInBackground() throws Exception {
                return ImageIO.read(imageFile);
            }
            
            @Override
            protected void done() {
                try {
                    currentImage = get();
                    if (currentImage != null) {
                        zoom = 1.0;
                        displayImage();
                        updateInfo(imageFile);
                        
                        zoomInBtn.setEnabled(true);
                        zoomOutBtn.setEnabled(true);
                        fitBtn.setEnabled(true);
                        prevBtn.setEnabled(currentIndex > 0);
                        nextBtn.setEnabled(currentIndex < imageList.size() - 1);
                        countLabel.setText("📁 " + imageList.size() + " | 📍 " + (currentIndex + 1) + "/" + imageList.size());
                    } else {
                        imageLabel.setText("❌ Failed to load image\n\nFile may be corrupted");
                    }
                } catch (Exception e) {
                    imageLabel.setText("❌ Error: " + e.getMessage());
                } finally {
                    progressBar.setVisible(false);
                }
            }
        };
        
        worker.execute();
    }
    
    private void displayImage() {
        if (currentImage != null) {
            int newWidth = (int)(currentImage.getWidth() * zoom);
            int newHeight = (int)(currentImage.getHeight() * zoom);
            Image scaledImage = currentImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(scaledImage));
            imageLabel.setText(null);
            zoomLabel.setText("🔍 " + (int)(zoom * 100) + "%");
        }
    }
    
    private void updateInfo(File imageFile) {
        fileNameLabel.setText("📄 " + imageFile.getName());
        dimensionsLabel.setText("📏 " + currentImage.getWidth() + " x " + currentImage.getHeight());
        setTitle("✨ AURORA VIEWER - " + imageFile.getName());
    }
    
    private void showPrevious() {
        if (slideshowRunning) stopSlideshow();
        if (currentIndex > 0) loadImageAtIndex(currentIndex - 1);
    }
    
    private void showNext() {
        if (slideshowRunning) stopSlideshow();
        if (currentIndex < imageList.size() - 1) loadImageAtIndex(currentIndex + 1);
    }
    
    private void zoomIn() {
        if (currentImage != null && zoom < 3.0) {
            zoom += 0.25;
            displayImage();
        }
    }
    
    private void zoomOut() {
        if (currentImage != null && zoom > 0.25) {
            zoom -= 0.25;
            displayImage();
        }
    }
    
    private void fitToWindow() {
        if (currentImage != null) {
            Dimension viewportSize = imageLabel.getParent().getSize();
            if (viewportSize.width > 0 && viewportSize.height > 0) {
                double widthRatio = (double) viewportSize.width / currentImage.getWidth();
                double heightRatio = (double) viewportSize.height / currentImage.getHeight();
                zoom = Math.min(widthRatio, heightRatio) * 0.95;
                displayImage();
            }
        }
    }
    
    private void toggleSlideshow() {
        if (slideshowRunning) {
            stopSlideshow();
        } else {
            startSlideshow();
        }
    }
    
    private void startSlideshow() {
        if (imageList.size() > 1) {
            slideshowRunning = true;
            slideshowTimer.start();
            slideshowBtn.setText("⏸ Stop");
            slideshowBtn.setBackground(new Color(220, 60, 60));
        }
    }
    
    private void stopSlideshow() {
        slideshowRunning = false;
        slideshowTimer.stop();
        slideshowBtn.setText("▶ Slideshow");
        slideshowBtn.setBackground(new Color(244, 67, 54));
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {}
        
        SwingUtilities.invokeLater(() -> new ImageViewer());
    }
}