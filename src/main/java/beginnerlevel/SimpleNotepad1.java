package beginnerlevel; // CRITICAL: This must match your folder name

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class SimpleNotepad1 extends JFrame {

    private JTextArea textArea;
    private JMenuBar menuBar;
    private JMenu fileMenu, editMenu;
    private JMenuItem newItem, openItem, saveItem, exitItem;
    private JMenuItem cutItem, copyItem, pasteItem, selectAllItem;
    private JCheckBoxMenuItem wordWrapItem;
    
    private File currentFile = null;
    private boolean isModified = false;

    public SimpleNotepad1() {
        // Setup Window
        setTitle("Untitled - Notepad");
        setSize(800, 600);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);

        // Text Area
        textArea = new JTextArea();
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 15));
        
        // Track modifications in real-time
        textArea.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { setModified(true); }
            public void removeUpdate(DocumentEvent e) { setModified(true); }
            public void changedUpdate(DocumentEvent e) { setModified(true); }
        });
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane);

        // Build Menu
        createMenus();

        // Window close handler (Handles the 'X' button)
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitApplication();
            }
        });
        
        setVisible(true);
    }

    private void createMenus() {
        menuBar = new JMenuBar();
        
        // File Menu
        fileMenu = new JMenu("File");
        newItem = new JMenuItem("New");
        openItem = new JMenuItem("Open...");
        saveItem = new JMenuItem("Save");
        exitItem = new JMenuItem("Exit");
        
        // Shortcuts (CTRL + N, etc.)
        newItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
        openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        
        fileMenu.add(newItem);
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        
        // Edit Menu
        editMenu = new JMenu("Edit");
        cutItem = new JMenuItem("Cut");
        copyItem = new JMenuItem("Copy");
        pasteItem = new JMenuItem("Paste");
        selectAllItem = new JMenuItem("Select All");
        wordWrapItem = new JCheckBoxMenuItem("Word Wrap");
        
        editMenu.add(cutItem);
        editMenu.add(copyItem);
        editMenu.add(pasteItem);
        editMenu.addSeparator();
        editMenu.add(selectAllItem);
        editMenu.addSeparator();
        editMenu.add(wordWrapItem);
        
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        setJMenuBar(menuBar);

        // Actions
        newItem.addActionListener(e -> newFile());
        openItem.addActionListener(e -> openFile());
        saveItem.addActionListener(e -> saveFile());
        exitItem.addActionListener(e -> exitApplication());
        
        cutItem.addActionListener(e -> textArea.cut());
        copyItem.addActionListener(e -> textArea.copy());
        pasteItem.addActionListener(e -> textArea.paste());
        selectAllItem.addActionListener(e -> textArea.selectAll());
        wordWrapItem.addActionListener(e -> {
            textArea.setLineWrap(wordWrapItem.isSelected());
            textArea.setWrapStyleWord(true);
        });
    }
    
    private void setModified(boolean modified) {
        isModified = modified;
        String fileName = (currentFile == null) ? "Untitled" : currentFile.getName();
        setTitle(fileName + " - Notepad" + (isModified ? "*" : ""));
    }
    
    private void newFile() {
        if (checkSave()) {
            textArea.setText("");
            currentFile = null;
            setModified(false);
        }
    }
    
    private void openFile() {
        if (!checkSave()) return;
        
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                textArea.read(br, null);
                currentFile = file;
                setModified(false);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Could not open file", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void saveFile() {
        if (currentFile == null) {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                currentFile = fileChooser.getSelectedFile();
            } else {
                return; // User cancelled
            }
        }
        
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(currentFile))) {
            textArea.write(bw);
            setModified(false);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Could not save file", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private boolean checkSave() {
        if (!isModified) return true;

        int response = JOptionPane.showConfirmDialog(this, 
            "Do you want to save changes?", "Notepad", 
            JOptionPane.YES_NO_CANCEL_OPTION);
        
        if (response == JOptionPane.YES_OPTION) {
            saveFile();
            return !isModified; // Return true if save was successful
        } else if (response == JOptionPane.NO_OPTION) {
            return true; // Exit without saving
        }
        return false; // Cancel
    }
    
    private void exitApplication() {
        if (checkSave()) {
            dispose();
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {}
        
        // Start on the Event Dispatch Thread
        SwingUtilities.invokeLater(SimpleNotepad1::new);
    }
}