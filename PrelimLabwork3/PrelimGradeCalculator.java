import java.awt.*;
import java.text.DecimalFormat;
import javax.swing.*;

public class PrelimGradeCalculator extends JFrame {

    // 1. Added Serial Version UID to fix warning
    private static final long serialVersionUID = 1L;

    // UI Components (Marked 'final' to fix warnings)
    private final JTextField txtAttendance, txtLab1, txtLab2, txtLab3;
    private final JLabel lblResult;
    
    // Theme Colors
    private final Color BEIGE_BG = new Color(245, 245, 220); // Beige
    private final Color BROWN_BTN = new Color(139, 69, 19);  // Saddle Brown
    private final Color BROWN_TEXT = new Color(75, 54, 33);  // Dark Brown
    private final Color LIGHT_BROWN = new Color(210, 180, 140); // Tan

    public PrelimGradeCalculator() {
        setTitle("Prelim Grade Calculator");
        setSize(450, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        getContentPane().setBackground(BEIGE_BG);
        setLocationRelativeTo(null); // Centers the window on screen

        // Header
        JLabel header = new JLabel("Prelim Grade Computer", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 20));
        header.setForeground(BROWN_TEXT);
        header.setBounds(0, 20, 450, 30);
        add(header);

        // Input Fields Helper
        int yPos = 70;
        txtAttendance = createLabeledField("Attendance Grade (0-100):", yPos);
        txtLab1 = createLabeledField("Lab Work 1 Grade:", yPos + 50);
        txtLab2 = createLabeledField("Lab Work 2 Grade:", yPos + 100);
        txtLab3 = createLabeledField("Lab Work 3 Grade:", yPos + 150);

        // Calculate Button
        JButton btnCalculate = new JButton("Calculate Requirements");
        btnCalculate.setBounds(100, 280, 230, 40);
        btnCalculate.setBackground(BROWN_BTN);
        btnCalculate.setForeground(Color.WHITE);
        btnCalculate.setFocusPainted(false);
        btnCalculate.setFont(new Font("Arial", Font.BOLD, 14));
        add(btnCalculate);

        // Output Area (Using HTML for multiline formatting in JLabel)
        lblResult = new JLabel("", SwingConstants.CENTER);
        lblResult.setBounds(20, 330, 400, 200);
        lblResult.setFont(new Font("Arial", Font.PLAIN, 14));
        lblResult.setForeground(BROWN_TEXT);
        lblResult.setVerticalAlignment(SwingConstants.TOP);
        add(lblResult);

        // Button Action (Lambda expression)
        btnCalculate.addActionListener(e -> calculateGrades());
    }

    private JTextField createLabeledField(String labelText, int y) {
        JLabel label = new JLabel(labelText);
        label.setBounds(50, y, 200, 25);
        label.setForeground(BROWN_TEXT);
        label.setFont(new Font("Arial", Font.BOLD, 13));
        add(label);

        JTextField field = new JTextField();
        field.setBounds(240, y, 140, 25);
        field.setBackground(Color.WHITE);
        field.setBorder(BorderFactory.createLineBorder(LIGHT_BROWN));
        add(field);
        return field;
    }

    private void calculateGrades() {
        try {
            // 1. Get Inputs
            double attendance = Double.parseDouble(txtAttendance.getText());
            double lab1 = Double.parseDouble(txtLab1.getText());
            double lab2 = Double.parseDouble(txtLab2.getText());
            double lab3 = Double.parseDouble(txtLab3.getText());

            // 2. Validate Inputs (Check for negatives or > 100)
            if (attendance < 0 || attendance > 100 || 
                lab1 < 0 || lab1 > 100 || 
                lab2 < 0 || lab2 > 100 || 
                lab3 < 0 || lab3 > 100) {
                
                JOptionPane.showMessageDialog(this, 
                    "All grades must be between 0 and 100.", 
                    "Invalid Input Range", 
                    JOptionPane.WARNING_MESSAGE);
                return; // Stop the calculation
            }

            // 3. Compute Averages & Standing
            double labAvg = (lab1 + lab2 + lab3) / 3.0;
            double classStanding = (attendance * 0.40) + (labAvg * 0.60);

            // 4. Compute Required Exam Scores
            // Formula: Target = (ReqExam * 0.30) + (ClassStanding * 0.70)
            // Derived: ReqExam = (Target - (ClassStanding * 0.70)) / 0.30
            double reqPass = (75 - (classStanding * 0.70)) / 0.30;
            double reqExc = (100 - (classStanding * 0.70)) / 0.30;

            // 5. Formatting Results
            DecimalFormat df = new DecimalFormat("0.00");
            
            // Check for "Impossible" condition (> 100)
            String passMsg = (reqPass > 100) 
                ? df.format(reqPass) + "<br><span style='color:red; font-size:10px;'>(Impossible)</span>" 
                : df.format(reqPass);
                
            String excMsg = (reqExc > 100) 
                ? df.format(reqExc) + "<br><span style='color:red; font-size:10px;'>(Impossible)</span>" 
                : df.format(reqExc);

            // Allow 0 if they already passed based on standing
            if (reqPass < 0) passMsg = "0.00 (Secured)";
            if (reqExc < 0) excMsg = "0.00 (Secured)";

            String resultHtml = "<html><div style='text-align: center;'>"
                    + "<b>Class Standing:</b> " + df.format(classStanding) + "<br>"
                    + "<b>Lab Average:</b> " + df.format(labAvg) + "<br><br>"
                    + "-----------------------------------------<br>"
                    + "To get a <b>75 (Passing)</b>, you need:<br>"
                    + "<b style='font-size:14px;'>" + passMsg + "</b><br><br>"
                    + "To get a <b>100 (Excellent)</b>, you need:<br>"
                    + "<b style='font-size:14px;'>" + excMsg + "</b>"
                    + "</div></html>";

            lblResult.setText(resultHtml);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        // Run GUI on the Event Dispatch Thread (Best Practice)
        SwingUtilities.invokeLater(() -> {
            new PrelimGradeCalculator().setVisible(true);
        });
    }
}