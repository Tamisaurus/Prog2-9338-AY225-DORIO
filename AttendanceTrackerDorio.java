import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class AttendanceTrackerDorio {

    // --- Theme Colors ---
    private static final Color COLOR_BG = new Color(30, 30, 30);       // Main Background
    private static final Color COLOR_FIELD_BG = new Color(50, 50, 50); // Text Field Background
    private static final Color COLOR_ACCENT = new Color(180, 40, 40);  // Red Accent
    private static final Color COLOR_TEXT = new Color(240, 240, 240);  // White Text
    
    // --- File Path ---
    // Using double backslashes for Windows path safety
    private static final String FILE_NAME = "Prog2-9338-AY225-DORIO\\Attendance Records.txt";

    // --- Global Component ---
    private static JTextArea logArea; 

    public static void main(String[] args) {
        // 1. Frame Setup
        JFrame frame = new JFrame("Attendance Tracker Pro");
        frame.setSize(500, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setBackground(COLOR_BG);
        frame.setLayout(new BorderLayout(15, 15)); // Spacing between sections

        // --- TOP: INPUT FORM ---
        JPanel inputPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        inputPanel.setBackground(COLOR_BG);
        inputPanel.setBorder(new EmptyBorder(20, 20, 10, 20));

        // Fields
        JLabel nameLabel = createStyledLabel("Name:");
        JTextField nameField = createStyledTextField();

        JLabel courseLabel = createStyledLabel("Course:");
        JTextField courseField = createStyledTextField();

        JLabel yearLabel = createStyledLabel("Year Level:");
        JTextField yearField = createStyledTextField();
        
        // Radio Buttons (Time In / Time Out)
        JLabel typeLabel = createStyledLabel("Attendance Type:");
        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        radioPanel.setBackground(COLOR_BG);
        
        JRadioButton rbIn = new JRadioButton("Time IN", true);
        JRadioButton rbOut = new JRadioButton("Time OUT");
        styleRadioButton(rbIn);
        styleRadioButton(rbOut);
        
        ButtonGroup typeGroup = new ButtonGroup();
        typeGroup.add(rbIn);
        typeGroup.add(rbOut);
        radioPanel.add(rbIn);
        radioPanel.add(rbOut);

        // Auto-fields
        JLabel timeLabel = createStyledLabel("Timestamp:");
        JTextField timeInField = createStyledTextField();
        timeInField.setEditable(false);
        timeInField.setFocusable(false);
        
        JLabel signatureLabel = createStyledLabel("E-Signature:");
        JTextField eSignatureField = createStyledTextField();
        eSignatureField.setEditable(false);
        eSignatureField.setFocusable(false);

        // Add to Input Panel
        inputPanel.add(nameLabel);      inputPanel.add(nameField);
        inputPanel.add(courseLabel);    inputPanel.add(courseField);
        inputPanel.add(yearLabel);      inputPanel.add(yearField);
        inputPanel.add(typeLabel);      inputPanel.add(radioPanel);
        inputPanel.add(timeLabel);      inputPanel.add(timeInField);
        inputPanel.add(signatureLabel); inputPanel.add(eSignatureField);

        // --- CENTER: LOG DISPLAY ---
        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBackground(COLOR_BG);
        logPanel.setBorder(new EmptyBorder(0, 20, 0, 20));
        
        JLabel logTitle = createStyledLabel("Attendance Log History:");
        logTitle.setBorder(new EmptyBorder(0, 0, 5, 0));
        
        logArea = new JTextArea();
        logArea.setBackground(COLOR_FIELD_BG); // Matches input fields
        logArea.setForeground(COLOR_TEXT);     // Matches input text
        logArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        logArea.setEditable(false);
        // Add padding inside the log area
        logArea.setBorder(new EmptyBorder(10, 10, 10, 10)); 
        
        JScrollPane scrollPane = new JScrollPane(logArea);
        // Remove default ugly borders and use our Red Accent
        scrollPane.setBorder(new LineBorder(COLOR_ACCENT, 1));
        scrollPane.getVerticalScrollBar().setBackground(COLOR_BG);
        
        logPanel.add(logTitle, BorderLayout.NORTH);
        logPanel.add(scrollPane, BorderLayout.CENTER);

        // --- BOTTOM: SUBMIT BUTTON ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(COLOR_BG);
        buttonPanel.setBorder(new EmptyBorder(10, 20, 20, 20));
        
        JButton submitButton = new JButton("GENERATE & SAVE RECORD");
        submitButton.setPreferredSize(new Dimension(250, 45));
        styleButton(submitButton);
        buttonPanel.add(submitButton);

        // --- LOGIC ---
        
        // Load logs on startup
        refreshLogDisplay();

        submitButton.addActionListener((ActionEvent e) -> {
            String name = nameField.getText().trim();
            String course = courseField.getText().trim();
            String year = yearField.getText().trim();
            String type = rbIn.isSelected() ? "TIME-IN" : "TIME-OUT";

            // Validation
            if (name.isEmpty() || course.isEmpty() || year.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please fill in all fields.");
                return;
            }
            
            // Generate Data
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String timestamp = now.format(formatter);
            String eSignature = UUID.randomUUID().toString().substring(0, 8); 
            
            // Update UI
            timeInField.setText(timestamp);
            eSignatureField.setText(eSignature);
            
            // Save & Refresh
            saveToFile(name, course, year, type, timestamp, eSignature, frame);
            refreshLogDisplay();
        });

        // Add to Frame
        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(logPanel, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);
        
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // --- HELPER: Save Logic ---
    private static void saveToFile(String name, String course, String year, String type, String time, String sign, JFrame frame) {
        File file = new File(FILE_NAME);
        
        // Ensure folder exists
        if (file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }

        // Try-with-resources ensures the writer closes automatically (fixes yellow underlines)
        try (FileWriter fw = new FileWriter(file, true);
             BufferedWriter writer = new BufferedWriter(fw)) {
                
            String record = String.format("[%s] %s | %s | %s | %s | ID:%s", 
                type, name, course, year, time, sign);
            
            writer.write(record);
            writer.newLine();
            
            JOptionPane.showMessageDialog(frame, "Success! " + type + " Recorded.");
            
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(frame, "Error saving file: " + ex.getMessage());
        }
    }

    // --- HELPER: Read Log Logic ---
    private static void refreshLogDisplay() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            logArea.setText("No records found yet.");
            return;
        }

        // Try-with-resources ensures the reader closes automatically
        try (FileReader fr = new FileReader(file);
             BufferedReader reader = new BufferedReader(fr)) {
            
            logArea.read(reader, null);
            
        } catch (IOException ex) {
            logArea.setText("Error reading logs: " + ex.getMessage());
        }
    }

    // --- HELPER: UI Styling ---
    private static JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(COLOR_TEXT);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        return label;
    }

    private static JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setBackground(COLOR_FIELD_BG);
        field.setForeground(COLOR_TEXT);
        field.setCaretColor(COLOR_ACCENT);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        // Red border with padding inside
        field.setBorder(new CompoundBorder(new LineBorder(COLOR_ACCENT, 1), new EmptyBorder(5, 5, 5, 5)));
        return field;
    }
    
    private static void styleRadioButton(JRadioButton rb) {
        rb.setBackground(COLOR_BG);
        rb.setForeground(COLOR_TEXT);
        rb.setFont(new Font("Segoe UI", Font.BOLD, 14));
        rb.setFocusPainted(false);
        rb.setOpaque(false); // Fixes background artifacts
    }

    private static void styleButton(JButton btn) {
        btn.setBackground(COLOR_ACCENT);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false); 
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBorder(new LineBorder(COLOR_ACCENT.darker(), 2));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}