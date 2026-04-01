package earlyintermediate; 

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;

public class Quiz extends JFrame {
    private JPanel cardPanel;
    private CardLayout cardLayout;
    
    private int currentQuestion = 0;
    private int score = 0;
    
    // Array to track if answer was already counted (to prevent score hacking via Prev/Next)
    private boolean[] answeredCorrectly; 

    private String[][] questions = {
        {"What is the size of int in Java?", "1 byte", "2 bytes", "4 bytes", "8 bytes", "4 bytes"},
        {"Which keyword is used for inheritance?", "extends", "implements", "imports", "super", "extends"},
        {"Which IDE are you currently using?", "Eclipse", "VS Code", "NetBeans", "IntelliJ", "NetBeans"},
        {"What is JVM?", "Java Virtual Machine", "Java Variable Method", "Java Visual Machine", "Java Verified Memory", "Java Virtual Machine"},
        {"Which of these is not a Java feature?", "Object-oriented", "Platform independent", "Multiple inheritance", "Automatic memory management", "Multiple inheritance"},
        {"What is the default value of a boolean in Java?", "true", "false", "null", "0", "false"},
        {"Which collection maintains insertion order?", "HashSet", "HashMap", "ArrayList", "TreeSet", "ArrayList"},
        {"What is the superclass of all classes in Java?", "Object", "Class", "Main", "System", "Object"},
        {"Which keyword is used to create a constant variable?", "static", "final", "const", "constant", "final"},
        {"What is the size of double in Java?", "4 bytes", "8 bytes", "16 bytes", "32 bytes", "8 bytes"}
    };

    private JLabel qLabel, questionNumberLabel, scoreLabel;
    private JRadioButton[] options = new JRadioButton[4];
    private ButtonGroup group;
    private JProgressBar progressBar;
    
    // Result screen components (Made global to avoid getClientProperty issues)
    private JLabel finalScoreDisplay, finalMessageLabel;
    
    private Color primaryColor = new Color(67, 97, 238);
    private Color successColor = new Color(76, 175, 80);
    private Color dangerColor = new Color(244, 67, 54);
    private Color darkBg = new Color(30, 36, 48);
    private Color lightBg = new Color(248, 249, 250);

    public Quiz() {
        answeredCorrectly = new boolean[questions.length];
        setTitle("📚 Java Quiz Master");
        setSize(700, 550);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        
        cardPanel.add(createWelcomePanel(), "welcome");
        cardPanel.add(createQuizPanel(), "quiz");
        cardPanel.add(createResultPanel(), "result");
        
        add(cardPanel);
        cardLayout.show(cardPanel, "welcome");
    }

    private JPanel createWelcomePanel() {
        // ... (Aapka Welcome Panel code sahi tha, bas Gradient wala part maintain rakhein)
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(darkBg);
        
        JPanel gradientPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, new Color(67, 97, 238), 0, getHeight(), new Color(156, 39, 176));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        gradientPanel.setLayout(new GridBagLayout());
        
        JLabel title = new JLabel("Java Quiz Master");
        title.setFont(new Font("Segoe UI", Font.BOLD, 36));
        title.setForeground(Color.WHITE);
        
        JButton startBtn = new JButton("START QUIZ");
        startBtn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        startBtn.addActionListener(e -> cardLayout.show(cardPanel, "quiz"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; gbc.insets = new Insets(10,10,10,10);
        gradientPanel.add(title, gbc);
        gbc.gridy = 1;
        gradientPanel.add(startBtn, gbc);
        
        panel.add(gradientPanel);
        return panel;
    }

    private JPanel createQuizPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(lightBg);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(primaryColor);
        headerPanel.setBorder(new EmptyBorder(20, 30, 20, 30));

        questionNumberLabel = new JLabel("Question 1/10");
        questionNumberLabel.setForeground(Color.WHITE);
        
        scoreLabel = new JLabel("Score: 0");
        scoreLabel.setForeground(Color.YELLOW);
        
        progressBar = new JProgressBar(0, questions.length);
        
        headerPanel.add(questionNumberLabel, BorderLayout.WEST);
        headerPanel.add(scoreLabel, BorderLayout.CENTER); // Added score to center
        headerPanel.add(progressBar, BorderLayout.EAST);

        // Question Area
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(new EmptyBorder(30,30,30,30));
        
        qLabel = new JLabel();
        qLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        
        JPanel optionsPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        optionsPanel.setOpaque(false);
        group = new ButtonGroup();
        for (int i = 0; i < 4; i++) {
            options[i] = new JRadioButton();
            group.add(options[i]);
            optionsPanel.add(options[i]);
        }

        centerPanel.add(qLabel, BorderLayout.NORTH);
        centerPanel.add(optionsPanel, BorderLayout.CENTER);

        // Navigation
        JPanel footer = new JPanel();
        JButton prevBtn = new JButton("Previous");
        JButton nextBtn = new JButton("Next Question");
        
        prevBtn.addActionListener(e -> previousQuestion());
        nextBtn.addActionListener(e -> handleNext());
        
        footer.add(prevBtn);
        footer.add(nextBtn);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(footer, BorderLayout.SOUTH);

        loadQuestion();
        return mainPanel;
    }

    private JPanel createResultPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(darkBg);
        
        JPanel resultCard = new JPanel();
        resultCard.setLayout(new BoxLayout(resultCard, BoxLayout.Y_AXIS));
        resultCard.setBorder(new EmptyBorder(40, 50, 40, 50));

        finalScoreDisplay = new JLabel(); // Made class member
        finalScoreDisplay.setFont(new Font("Segoe UI", Font.BOLD, 48));
        
        finalMessageLabel = new JLabel(); // Made class member
        finalMessageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));

        JButton restartBtn = new JButton("Restart");
        restartBtn.addActionListener(e -> {
            resetQuiz();
            cardLayout.show(cardPanel, "quiz");
        });

        resultCard.add(finalScoreDisplay);
        resultCard.add(finalMessageLabel);
        resultCard.add(restartBtn);
        
        panel.add(resultCard);
        return panel;
    }

    private void loadQuestion() {
        qLabel.setText("Q" + (currentQuestion + 1) + ". " + questions[currentQuestion][0]);
        for (int i = 0; i < 4; i++) {
            options[i].setText(questions[currentQuestion][i + 1]);
        }
        group.clearSelection();
        questionNumberLabel.setText("Question " + (currentQuestion + 1) + "/" + questions.length);
        progressBar.setValue(currentQuestion);
        scoreLabel.setText("Score: " + score);
    }

    private void handleNext() {
        String selected = "";
        for (JRadioButton rb : options) {
            if (rb.isSelected()) {
                selected = rb.getText();
                break;
            }
        }

        if (selected.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select an answer!");
            return;
        }

        // Logical Fix: Check and update score
        if (selected.equals(questions[currentQuestion][5])) {
            if (!answeredCorrectly[currentQuestion]) {
                score++;
                answeredCorrectly[currentQuestion] = true;
            }
        }

        currentQuestion++;
        if (currentQuestion < questions.length) {
            loadQuestion();
        } else {
            showResult();
        }
    }

    private void previousQuestion() {
        if (currentQuestion > 0) {
            currentQuestion--;
            loadQuestion();
        }
    }

    private void showResult() {
        finalScoreDisplay.setText(score + " / " + questions.length);
        int percent = (score * 100) / questions.length;
        finalMessageLabel.setText(percent >= 60 ? "Excellent Job!" : "Keep Practicing!");
        cardLayout.show(cardPanel, "result");
    }

    private void resetQuiz() {
        currentQuestion = 0;
        score = 0;
        answeredCorrectly = new boolean[questions.length];
        loadQuestion();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Quiz().setVisible(true));
    }
}