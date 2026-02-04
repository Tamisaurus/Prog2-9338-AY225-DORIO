/*
 * Programmer: Raphael Louis P. Dorio [25-2280-916]
 * Project: Student Record System - Java (Final Validation Update)
 */

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class StudentRecordSystem extends JFrame {
    
    private JTable table;
    private DefaultTableModel model;
    private JTextField txtID, txtName, txtGrade;

    // --- THEME COLORS ---
    private final Color COLOR_BG = new Color(18, 18, 18);
    private final Color COLOR_PANEL = new Color(30, 30, 30);
    private final Color COLOR_ACCENT = new Color(180, 0, 0);
    private final Color COLOR_TEXT = new Color(240, 240, 240);
    private final Font FONT_MAIN = new Font("Consolas", Font.PLAIN, 14);

    public StudentRecordSystem() {
        // --- 1. Identity & Frame Setup ---
        this.setTitle("Records - Raphael Louis P. Dorio [25-2280-916]");
        this.setSize(900, 650); 
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setLayout(new BorderLayout());
        this.getContentPane().setBackground(COLOR_BG);

        // --- 2. HEADER SECTION ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(COLOR_BG);
        headerPanel.setBorder(new EmptyBorder(10, 0, 10, 0));

        JLabel lblTitle = new JLabel("S T U D E N T   R E C O R D S");
        lblTitle.setFont(new Font("Consolas", Font.BOLD, 28));
        lblTitle.setForeground(COLOR_ACCENT);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        
        headerPanel.add(lblTitle, BorderLayout.CENTER);
        this.add(headerPanel, BorderLayout.NORTH);

        // --- 3. Table Setup (With Sorting) ---
        model = new DefaultTableModel(new String[]{"ID", "First Name", "Last Name", "Grade"}, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 3) return Integer.class;
                return String.class;
            }
        };

        table = new JTable(model);
        styleTable(table);
        table.setAutoCreateRowSorter(true);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(COLOR_BG);
        scrollPane.setBorder(new LineBorder(COLOR_ACCENT, 1));
        
        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setBackground(COLOR_BG);
        tableContainer.setBorder(new EmptyBorder(0, 15, 0, 15));
        tableContainer.add(scrollPane, BorderLayout.CENTER);
        
        this.add(tableContainer, BorderLayout.CENTER);

        // --- 4. Input Panel (South) ---
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 15));
        inputPanel.setBackground(COLOR_PANEL);
        inputPanel.setBorder(new LineBorder(COLOR_ACCENT, 1));

        // Use '9' here to signal the createStyledField method to add a limit
        txtID = createStyledField(9); 
        txtName = createStyledField(20);
        txtGrade = createStyledField(5);
        
        JButton btnAdd = createStyledButton("ADD RECORD");
        JButton btnDelete = createStyledButton("DELETE SELECTED");

        inputPanel.add(createStyledLabel("ID:"));
        inputPanel.add(txtID);
        inputPanel.add(createStyledLabel("Name (First Last):"));
        inputPanel.add(txtName);
        inputPanel.add(createStyledLabel("Grade:"));
        inputPanel.add(txtGrade);
        inputPanel.add(btnAdd);
        inputPanel.add(btnDelete);

        this.add(inputPanel, BorderLayout.SOUTH);

        // --- 5. Load Data ---
        loadCSV("MOCK_DATA.csv");

        // --- 6. Button Logic (WITH ALL VALIDATIONS) ---
        btnAdd.addActionListener(e -> {
            String id = txtID.getText().trim();
            String nameFull = txtName.getText().trim();
            String gradeStr = txtGrade.getText().trim();

            // Check Empty
            if(id.isEmpty() || nameFull.isEmpty() || gradeStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // ID Validation: Must be exactly 9 digits
            if (!id.matches("\\d{9}")) {
                JOptionPane.showMessageDialog(this, "ID Error: Must be exactly 9 numbers.", "Invalid ID", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Name Validation: Letters, spaces, and apostrophes ONLY
            if (!nameFull.matches("^[a-zA-Z\\s']+$")) {
                JOptionPane.showMessageDialog(this, "Name Error: No special characters allowed (except apostrophes).", "Invalid Name", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                // Parse Grade
                int grade = Integer.parseInt(gradeStr);

                // *** NEW VALIDATION: GRADE LIMIT (0-100) ***
                if (grade < 0 || grade > 100) {
                    JOptionPane.showMessageDialog(this, "Grade Error: Must be between 0 and 100.", "Invalid Grade", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String[] nameParts = nameFull.split(" ", 2);
                String first = nameParts[0];
                String last = (nameParts.length > 1) ? nameParts[1] : "-";

                model.addRow(new Object[]{id, first, last, grade});
                txtID.setText(""); txtName.setText(""); txtGrade.setText("");

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Grade must be a number!", "Invalid Grade", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnDelete.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                int modelRow = table.convertRowIndexToModel(selectedRow);
                model.removeRow(modelRow);
            } else {
                JOptionPane.showMessageDialog(this, "Select a row to delete.");
            }
        });

        this.setVisible(true);
    }

    private void loadCSV(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            br.readLine(); 
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if(data.length >= 8) {
                    try {
                        int grade = Integer.parseInt(data[7]); 
                        model.addRow(new Object[]{data[0], data[1], data[2], grade});
                    } catch (NumberFormatException e) {
                        model.addRow(new Object[]{data[0], data[1], data[2], 0});
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }

    private void styleTable(JTable table) {
        table.setBackground(COLOR_BG);
        table.setForeground(COLOR_TEXT);
        table.setGridColor(new Color(60, 60, 60));
        table.setSelectionBackground(COLOR_ACCENT);
        table.setSelectionForeground(Color.WHITE);
        table.setRowHeight(25);
        table.setFont(FONT_MAIN);

        JTableHeader header = table.getTableHeader();
        header.setBackground(COLOR_ACCENT);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Consolas", Font.BOLD, 14));
        header.setBorder(new LineBorder(Color.BLACK));
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.setDefaultRenderer(Object.class, centerRenderer);
        table.setDefaultRenderer(Integer.class, centerRenderer);
    }

    // --- Field Creator with Character Limiter ---
    private JTextField createStyledField(int cols) {
        JTextField field = new JTextField(cols);
        
        // If cols is 9 (ID), apply a document filter to limit text length
        if (cols == 9) {
            field.setDocument(new PlainDocument() {
                @Override
                public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
                    // Limit to 9 characters max
                    if (str == null) return;
                    if ((getLength() + str.length()) <= 9) {
                        super.insertString(offs, str, a);
                    }
                }
            });
        }

        field.setBackground(new Color(50, 50, 50));
        field.setForeground(Color.WHITE);
        field.setCaretColor(COLOR_ACCENT);
        field.setBorder(new LineBorder(COLOR_ACCENT, 1));
        field.setFont(FONT_MAIN);
        return field;
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(COLOR_ACCENT);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Consolas", Font.BOLD, 12));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JLabel createStyledLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(COLOR_TEXT);
        lbl.setFont(FONT_MAIN);
        return lbl;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StudentRecordSystem());
    }
}