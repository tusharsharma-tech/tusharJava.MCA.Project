package beginnerlevel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import javax.swing.Timer;

public class ToDoList extends JFrame {
    
    private JList<Task> taskList;
    private DefaultListModel<Task> listModel;
    private JTextField taskInput;
    private JButton addButton, deleteButton, completeButton, clearButton;
    private JRadioButton allTasks, pendingTasks, completedTasks;
    private ButtonGroup filterGroup;
    private JLabel taskCountLabel, completedCountLabel;
    private JProgressBar progressBar;
    private JMenuBar menuBar;
    private JMenu fileMenu, viewMenu, helpMenu;
    private JMenuItem saveItem, loadItem, exportItem, exitItem;
    private JCheckBoxMenuItem showTimeStampItem;
    private JLabel statusBar;
    private Color primaryColor = new Color(33, 150, 243);
    private Color successColor = new Color(76, 175, 80);
    private Color dangerColor = new Color(244, 67, 54);
    private Color warningColor = new Color(255, 152, 0);

    // --- TASK CLASS FIXED ---
    static class Task implements Serializable {
        private static final long serialVersionUID = 1L;
        
        private String text;
        private boolean completed;
        private LocalDateTime createdTime;
        private LocalDateTime completedTime;
        private Priority priority;

        enum Priority {
            HIGH("🔴 High", new Color(255, 235, 238)),
            MEDIUM("🟡 Medium", new Color(255, 248, 225)),
            LOW("🟢 Low", new Color(232, 245, 233));
            
            private String display;
            private Color color;
            
            Priority(String display, Color color) {
                this.display = display;
                this.color = color;
            }
            public String getDisplay() { return display; }
            public Color getColor() { return color; }
        }

        public Task(String text) {
            this.text = text;
            this.completed = false;
            this.createdTime = LocalDateTime.now();
            this.priority = Priority.MEDIUM; // Default priority
        }

        // Getters and Setters
        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
        public boolean isCompleted() { return completed; }
        public void setCompleted(boolean completed) { 
            this.completed = completed;
            this.completedTime = completed ? LocalDateTime.now() : null;
        }
        public Priority getPriority() { return priority; }
        public void setPriority(Priority priority) { this.priority = priority; }
        public LocalDateTime getCreatedTime() { return createdTime; }
    }

    public ToDoList() {
        setTitle("Advanced To-Do List Manager");
        setSize(700, 750);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initializeComponents();
        setupLayout();
        setupMenuBar();
        setupListeners();
        updateStatistics();
        
        setVisible(true);
    }

    private void initializeComponents() {
        listModel = new DefaultListModel<>();
        taskList = new JList<>(listModel);
        taskList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        taskList.setCellRenderer(new TaskCellRenderer());
        taskList.setFixedCellHeight(55);
        
        taskInput = new JTextField();
        taskInput.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        addButton = createStyledButton("➕ Add Task", primaryColor);
        deleteButton = createStyledButton("🗑️ Delete", dangerColor);
        completeButton = createStyledButton("✓ Complete", successColor);
        clearButton = createStyledButton("🗑️ Clear All", warningColor);
        
        allTasks = new JRadioButton("All", true);
        pendingTasks = new JRadioButton("Pending");
        completedTasks = new JRadioButton("Done");
        filterGroup = new ButtonGroup();
        filterGroup.add(allTasks); filterGroup.add(pendingTasks); filterGroup.add(completedTasks);
        
        taskCountLabel = new JLabel("Total: 0");
        completedCountLabel = new JLabel("Done: 0");
        progressBar = new JProgressBar(0, 100);
        
        statusBar = new JLabel(" Ready");
        statusBar.setBorder(BorderFactory.createEtchedBorder());
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        
        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(primaryColor);
        header.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        JLabel title = new JLabel("📝 MCA Task Manager");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.WEST);
        
        JPanel stats = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        stats.setOpaque(false);
        stats.add(taskCountLabel); stats.add(completedCountLabel); stats.add(progressBar);
        header.add(stats, BorderLayout.EAST);

        // Input Area
        JPanel inputPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        JPanel row1 = new JPanel(new BorderLayout(10, 0));
        row1.add(new JLabel("Task: "), BorderLayout.WEST);
        row1.add(taskInput, BorderLayout.CENTER);
        
        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JComboBox<Task.Priority> pCombo = new JComboBox<>(Task.Priority.values());
        row2.add(new JLabel("Priority: ")); row2.add(pCombo);
        row2.add(addButton);
        addButton.putClientProperty("pCombo", pCombo);

        inputPanel.add(row1); inputPanel.add(row2);

        // Filters
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel(" Filter: "));
        filterPanel.add(allTasks); filterPanel.add(pendingTasks); filterPanel.add(completedTasks);

        // Main Center
        JPanel center = new JPanel(new BorderLayout());
        center.add(filterPanel, BorderLayout.NORTH);
        center.add(new JScrollPane(taskList), BorderLayout.CENTER);
        center.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));

        // Bottom
        JPanel bottom = new JPanel(new BorderLayout());
        JPanel actions = new JPanel(new FlowLayout());
        actions.add(completeButton); actions.add(deleteButton); actions.add(clearButton);
        
        bottom.add(actions, BorderLayout.NORTH);
        bottom.add(statusBar, BorderLayout.SOUTH);

        add(header, BorderLayout.NORTH);
        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.add(inputPanel, BorderLayout.CENTER);
        add(topContainer, BorderLayout.CENTER); // Using center for input + list
        
        // Final structure adjustment
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.add(inputPanel, BorderLayout.NORTH);
        mainContent.add(center, BorderLayout.CENTER);
        
        add(mainContent, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }

    private void setupListeners() {
        addButton.addActionListener(e -> {
            JComboBox<Task.Priority> cb = (JComboBox<Task.Priority>) addButton.getClientProperty("pCombo");
            addTask(cb);
        });
        taskInput.addActionListener(e -> addButton.doClick());
        completeButton.addActionListener(e -> completeTask());
        deleteButton.addActionListener(e -> deleteTask());
        clearButton.addActionListener(e -> clearAllTasks());
        
        allTasks.addActionListener(e -> filterTasks());
        pendingTasks.addActionListener(e -> filterTasks());
        completedTasks.addActionListener(e -> filterTasks());

        saveItem.addActionListener(e -> saveTasks());
        loadItem.addActionListener(e -> loadTasks());
        exitItem.addActionListener(e -> exitApplication());
    }

    private void addTask(JComboBox<Task.Priority> cb) {
        String txt = taskInput.getText().trim();
        if(!txt.isEmpty()) {
            Task t = new Task(txt);
            t.setPriority((Task.Priority) cb.getSelectedItem());
            listModel.addElement(t);
            taskInput.setText("");
            filterTasks();
            updateStatistics();
        }
    }

    private void completeTask() {
        Task t = taskList.getSelectedValue();
        if(t != null) { t.setCompleted(true); taskList.repaint(); updateStatistics(); filterTasks(); }
    }

    private void deleteTask() {
        Task t = taskList.getSelectedValue();
        if(t != null) { listModel.removeElement(t); updateStatistics(); filterTasks(); }
    }

    private void clearAllTasks() {
        listModel.clear(); filterTasks(); updateStatistics();
    }

    private void filterTasks() {
        DefaultListModel<Task> filtered = new DefaultListModel<>();
        for(int i=0; i<listModel.size(); i++) {
            Task t = listModel.get(i);
            if(allTasks.isSelected()) filtered.addElement(t);
            else if(pendingTasks.isSelected() && !t.isCompleted()) filtered.addElement(t);
            else if(completedTasks.isSelected() && t.isCompleted()) filtered.addElement(t);
        }
        taskList.setModel(filtered);
    }

    private void updateStatistics() {
        int total = listModel.size();
        int done = 0;
        for(int i=0; i<total; i++) if(listModel.get(i).isCompleted()) done++;
        taskCountLabel.setText("Total: " + total);
        completedCountLabel.setText("Done: " + done);
        progressBar.setValue(total > 0 ? (done * 100 / total) : 0);
    }

    private void saveTasks() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("tasks.dat"))) {
            ArrayList<Task> list = new ArrayList<>();
            for(int i=0; i<listModel.size(); i++) list.add(listModel.get(i));
            oos.writeObject(list);
            statusBar.setText(" Tasks Saved!");
        } catch (Exception e) { statusBar.setText(" Save Failed!"); }
    }

    private void loadTasks() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("tasks.dat"))) {
            ArrayList<Task> list = (ArrayList<Task>) ois.readObject();
            listModel.clear();
            for(Task t : list) listModel.addElement(t);
            filterTasks(); updateStatistics();
            statusBar.setText(" Tasks Loaded!");
        } catch (Exception e) { statusBar.setText(" No saved data found."); }
    }

    private void setupMenuBar() {
        menuBar = new JMenuBar();
        fileMenu = new JMenu("File");
        saveItem = new JMenuItem("Save"); loadItem = new JMenuItem("Load"); exitItem = new JMenuItem("Exit");
        fileMenu.add(saveItem); fileMenu.add(loadItem); fileMenu.add(exitItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);
    }

    private void exitApplication() { System.exit(0); }

    class TaskCellRenderer extends JPanel implements ListCellRenderer<Task> {
        private JLabel txt = new JLabel();
        private JLabel info = new JLabel();
        public TaskCellRenderer() {
            setLayout(new BorderLayout());
            setBorder(new EmptyBorder(5,10,5,10));
            add(txt, BorderLayout.CENTER); add(info, BorderLayout.EAST);
        }
        public Component getListCellRendererComponent(JList<? extends Task> l, Task t, int i, boolean s, boolean f) {
            txt.setText(t.getText());
            txt.setFont(new Font("Segoe UI", t.isCompleted() ? Font.ITALIC : Font.BOLD, 14));
            info.setText(t.getPriority().getDisplay() + (t.isCompleted() ? " ✅" : ""));
            setBackground(s ? new Color(200, 230, 255) : t.getPriority().getColor());
            return this;
        }
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch(Exception e){}
        SwingUtilities.invokeLater(ToDoList::new);
    }
}