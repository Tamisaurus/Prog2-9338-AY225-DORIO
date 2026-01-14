import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.UUID;

public class AttendanceTracker {
    // Background
    private static final Color COLOR_BG = new Color(30, 30, 30);
    private static final Color COLOR_FIELD_BG = new Color(50, 50, 50);
    private static final Color COLOR_ACCENT = new Color(180, 40, 40);
    private static final Color COLOR_TEXT = new Color(240, 240, 240);
    
    // File name constant
    private static final String FILE_NAME = "Attendance Records.txt";

    public static void main(String[] args) {
        // --- 1. Window Setup ---
        JFrame frame = new JFrame("Attendance Tracker Pro");
        frame.setSize(450, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Set the main background color
        frame.getContentPane().setBackground(COLOR_BG);
        
        // Use a container panel with padding so elements aren't stuck to the edge
        JPanel mainPanel = new JPanel(new GridLayout(7, 2, 15, 15));
        mainPanel.setBackground(COLOR_BG);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20)); // Margin around the whole form

        // --- 2. Create Components ---
        JLabel nameLabel = createStyledLabel("Name:");
        JTextField nameField = createStyledTextField();

        JLabel courseLabel = createStyledLabel("Course:");
        JTextField courseField = createStyledTextField();

        JLabel yearLabel = createStyledLabel("Year Level:");
        JTextField yearField = createStyledTextField();
        
        JLabel timeLabel = createStyledLabel("Time In:");
        JTextField timeInField = createStyledTextField();
        timeInField.setEditable(false);
        
        JLabel signatureLabel = createStyledLabel("E-Signature:");
        JTextField eSignatureField = createStyledTextField();
        eSignatureField.setEditable(false);

        // Custom Red Button
        JButton submitButton = new JButton("GENERATE & SAVE");
        styleButton(submitButton);

        JLabel spacer = new JLabel(""); // Empty slot

        // --- 3. Logic ---
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText().trim();
                String course = courseField.getText().trim();
                String year = yearField.getText().trim();

                // Validation: Check empty fields
                if (name.isEmpty() || course.isEmpty() || year.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Please fill in all fields.");
                    return;
                }

                // Validation: Check for duplicates
                if (isNameAlreadyRegistered(name)) {
                    JOptionPane.showMessageDialog(frame, "Error: '" + name + "' is already in the attendance list!", "Duplicate Entry", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // If all good, generate data
                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String timeIn = now.format(formatter);
                String eSignature = UUID.randomUUID().toString();

                // Update UI
                timeInField.setText(timeIn);
                eSignatureField.setText(eSignature);

                // Save to File
                saveToFile(name, course, year, timeIn, eSignature, frame);
            }
        });

        // --- 4. Add to Panel ---
        mainPanel.add(nameLabel);       mainPanel.add(nameField);
        mainPanel.add(courseLabel);     mainPanel.add(courseField);
        mainPanel.add(yearLabel);       mainPanel.add(yearField);       
        mainPanel.add(timeLabel);       mainPanel.add(timeInField);
        mainPanel.add(signatureLabel);  mainPanel.add(eSignatureField);
        mainPanel.add(spacer);          mainPanel.add(submitButton);

        frame.add(mainPanel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // --- HELPER: Save Logic ---
    private static void saveToFile(String name, String course, String year, String time, String sign, JFrame frame) {
        try {
            FileWriter fw = new FileWriter(FILE_NAME, true);
            BufferedWriter writer = new BufferedWriter(fw);
            
            // Format: Name | Course | Year | Time | Signature
            String record = "Name: " + name + ", Course: " + course + ", Year: " + year + ", Time: " + time + ", Sign: " + sign;
            
            writer.write(record);
            writer.newLine();
            writer.close();
            
            JOptionPane.showMessageDialog(frame, "Okay Na! Present kana!.");
            
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(frame, "Error saving file: " + ex.getMessage());
        }
    }

    // --- HELPER: Check Duplicates ---
    private static boolean isNameAlreadyRegistered(String newName) {
        File file = new File(FILE_NAME);
        if (!file.exists()) return false; // If file doesn't exist, no duplicates possible

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                // Simple check: does the line contain "Name: [newName] |"?
                // We add the " |" to ensure we don't confuse "Dan" with "Daniel"
                if (line.contains("Name: " + newName + " |")) {
                    return true;
                }
            }
        } catch (FileNotFoundException e) {
            return false;
        }
        return false;
    }

    // --- HELPER: UI Styling ---
    
    // Makes labels white and bold
    private static JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(COLOR_TEXT);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        return label;
    }

    // Makes text fields dark grey with red borders
    private static JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setBackground(COLOR_FIELD_BG);
        field.setForeground(COLOR_TEXT);
        field.setCaretColor(COLOR_ACCENT); // The blinking cursor color
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        // Add a padding inside the text box and a red border
        Border line = new LineBorder(COLOR_ACCENT, 1);
        Border margin = new EmptyBorder(5, 5, 5, 5);
        field.setBorder(new CompoundBorder(line, margin));
        
        return field;
    }

    // Makes the button Matte Red
    private static void styleButton(JButton btn) {
        btn.setBackground(COLOR_ACCENT);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false); 
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBorder(new LineBorder(COLOR_ACCENT.darker(), 2));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}