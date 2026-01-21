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
import javax.swing.border.TitledBorder;

public class AttendanceTrackerDorio {

    // --- Theme Colors ---
    private static final Color COLOR_BG = new Color(30, 30, 30);       // Main Background
    private static final Color COLOR_PANEL_BG = new Color(40, 40, 40); // Slightly lighter for panels
    private static final Color COLOR_FIELD_BG = new Color(50, 50, 50); // Text Field Background
    private static final Color COLOR_ACCENT = new Color(180, 40, 40);  // Red Accent
    private static final Color COLOR_TEXT = new Color(240, 240, 240);  // White Text
    
    // --- File Path ---
    private static final String FILE_NAME = "Prog2-9338-AY225-DORIO\\Attendance Records.txt";

    // --- Global Components ---
    private static JTextArea logAreaIn; 
    private static JTextArea logAreaOut; 

    public static void main(String[] args) {
        // 1. Frame Setup
        JFrame frame = new JFrame("Attendance Tracker Pro");
        frame.setSize(900, 700); // Widened to fit two columns
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setBackground(COLOR_BG);
        frame.setLayout(new BorderLayout(15, 15));

        // --- TOP: INPUT FORM ---
        JPanel inputPanel = new JPanel(new GridLayout(0, 4, 15, 15)); // 4 Columns for wider layout
        inputPanel.setBackground(COLOR_BG);
        inputPanel.setBorder(new CompoundBorder(
            new EmptyBorder(20, 20, 10, 20),
            new TitledBorder(new LineBorder(COLOR_ACCENT), "Student Entry", TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14), COLOR_ACCENT)
        ));

        // Fields
        JLabel nameLabel = createStyledLabel("Name:");
        JTextField nameField = createStyledTextField();

        JLabel courseLabel = createStyledLabel("Course:");
        JTextField courseField = createStyledTextField();

        JLabel yearLabel = createStyledLabel("Year Level:");
        JTextField yearField = createStyledTextField();
        
        // Radio Buttons
        JLabel typeLabel = createStyledLabel("Type:");
        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        radioPanel.setBackground(COLOR_BG);
        
        JRadioButton rbIn = new JRadioButton("IN", true);
        JRadioButton rbOut = new JRadioButton("OUT");
        styleRadioButton(rbIn);
        styleRadioButton(rbOut);
        
        ButtonGroup typeGroup = new ButtonGroup();
        typeGroup.add(rbIn);
        typeGroup.add(rbOut);
        radioPanel.add(rbIn);
        radioPanel.add(new JLabel("  ")); // Spacer
        radioPanel.add(rbOut);

        // Auto-fields
        JLabel timeLabel = createStyledLabel("Time:");
        JTextField timeInField = createStyledTextField();
        timeInField.setEditable(false);
        
        JLabel signatureLabel = createStyledLabel("ID/Sig:");
        JTextField eSignatureField = createStyledTextField();
        eSignatureField.setEditable(false);

        // Add to Input Panel (Flow: Label -> Field -> Label -> Field...)
        inputPanel.add(nameLabel);      inputPanel.add(nameField);
        inputPanel.add(courseLabel);    inputPanel.add(courseField);
        inputPanel.add(yearLabel);      inputPanel.add(yearField);
        inputPanel.add(typeLabel);      inputPanel.add(radioPanel);
        inputPanel.add(timeLabel);      inputPanel.add(timeInField);
        inputPanel.add(signatureLabel); inputPanel.add(eSignatureField);

        // --- CENTER: SPLIT LOG DISPLAY ---
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 20, 0)); // 1 Row, 2 Cols, 20px gap
        centerPanel.setBackground(COLOR_BG);
        centerPanel.setBorder(new EmptyBorder(0, 20, 0, 20));

        // Left Column: TIME IN
        JPanel inPanel = new JPanel(new BorderLayout());
        inPanel.setBackground(COLOR_PANEL_BG);
        inPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JLabel inTitle = createStyledLabel("TIME IN RECORDS");
        inTitle.setForeground(Color.GREEN); // Green title for IN
        inTitle.setHorizontalAlignment(SwingConstants.CENTER);
        
        logAreaIn = createLogArea();
        JScrollPane scrollIn = createStyledScrollPane(logAreaIn);
        
        inPanel.add(inTitle, BorderLayout.NORTH);
        inPanel.add(scrollIn, BorderLayout.CENTER);

        // Right Column: TIME OUT
        JPanel outPanel = new JPanel(new BorderLayout());
        outPanel.setBackground(COLOR_PANEL_BG);
        outPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel outTitle = createStyledLabel("TIME OUT RECORDS");
        outTitle.setForeground(new Color(255, 100, 100)); // Red title for OUT
        outTitle.setHorizontalAlignment(SwingConstants.CENTER);
        
        logAreaOut = createLogArea();
        JScrollPane scrollOut = createStyledScrollPane(logAreaOut);
        
        outPanel.add(outTitle, BorderLayout.NORTH);
        outPanel.add(scrollOut, BorderLayout.CENTER);

        // Add both to center
        centerPanel.add(inPanel);
        centerPanel.add(outPanel);

        // --- BOTTOM: SUBMIT BUTTON ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(COLOR_BG);
        buttonPanel.setBorder(new EmptyBorder(10, 20, 20, 20));
        
        JButton submitButton = new JButton("RECORD ATTENDANCE");
        submitButton.setPreferredSize(new Dimension(300, 50));
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
            String eSignature = UUID.randomUUID().toString().substring(0, 8).toUpperCase(); 
            
            // Update UI
            timeInField.setText(timestamp);
            eSignatureField.setText(eSignature);
            
            // Save & Refresh
            saveToFile(name, course, year, type, timestamp, eSignature, frame);
            refreshLogDisplay();
        });

        // Add to Frame
        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(centerPanel, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);
        
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // --- HELPER: Save Logic ---
    private static void saveToFile(String name, String course, String year, String type, String time, String sign, JFrame frame) {
        File file = new File(FILE_NAME);
        
        if (file.getParentFile() != null) file.getParentFile().mkdirs();

        try (FileWriter fw = new FileWriter(file, true);
             BufferedWriter writer = new BufferedWriter(fw)) {
                
            // Format: [TYPE] Name | Course | Year | Time | ID
            String record = String.format("[%s] %s | %s | %s | %s | ID:%s", 
                type, name, course, year, time, sign);
            
            writer.write(record);
            writer.newLine();
            
            JOptionPane.showMessageDialog(frame, "Successfully recorded: " + type);
            
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(frame, "Error saving file: " + ex.getMessage());
        }
    }

    // --- HELPER: Read & Split Logic ---
    private static void refreshLogDisplay() {
        logAreaIn.setText("");
        logAreaOut.setText("");

        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (FileReader fr = new FileReader(file);
             BufferedReader reader = new BufferedReader(fr)) {
            
            String line;
            while ((line = reader.readLine()) != null) {
                // Check if the line belongs to IN or OUT
                if (line.contains("[TIME-IN]")) {
                    logAreaIn.append(line + "\n\n"); // Extra newline for spacing
                } else if (line.contains("[TIME-OUT]")) {
                    logAreaOut.append(line + "\n\n");
                }
            }
            
        } catch (IOException ex) {
            logAreaIn.setText("Error reading logs.");
        }
    }

    // --- HELPER: UI Styling ---
    private static JTextArea createLogArea() {
        JTextArea area = new JTextArea();
        area.setBackground(COLOR_FIELD_BG);
        area.setForeground(COLOR_TEXT);
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(new EmptyBorder(5, 5, 5, 5));
        return area;
    }

    private static JScrollPane createStyledScrollPane(JTextArea area) {
        JScrollPane scroll = new JScrollPane(area);
        scroll.setBorder(new LineBorder(COLOR_ACCENT, 1));
        scroll.getVerticalScrollBar().setBackground(COLOR_BG);
        return scroll;
    }

    private static JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(COLOR_TEXT);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        return label;
    }

    private static JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setBackground(COLOR_FIELD_BG);
        field.setForeground(COLOR_TEXT);
        field.setCaretColor(COLOR_ACCENT);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBorder(new CompoundBorder(new LineBorder(COLOR_ACCENT, 1), new EmptyBorder(4, 4, 4, 4)));
        return field;
    }
    
    private static void styleRadioButton(JRadioButton rb) {
        rb.setBackground(COLOR_BG);
        rb.setForeground(COLOR_TEXT);
        rb.setFont(new Font("Segoe UI", Font.BOLD, 12));
        rb.setFocusPainted(false);
        rb.setOpaque(false);
    }

    private static void styleButton(JButton btn) {
        btn.setBackground(COLOR_ACCENT);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false); 
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setBorder(new LineBorder(COLOR_ACCENT.darker(), 2));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}