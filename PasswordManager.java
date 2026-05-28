import javax.swing.*;
import java.awt.*;
import java.security.MessageDigest;
import java.util.Base64;

public class PasswordManager extends JFrame {

    JTextField nameField = new JTextField(15);
    JTextField panField  = new JTextField(15);
    JTextField dobField  = new JTextField(15);
    JTextField passField = new JTextField(20);
    JLabel strengthLabel = new JLabel("Strength: —");

    public PasswordManager() {
        setTitle("Password Manager");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(6, 2, 8, 8));
        getRootPane().setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        add(new JLabel("Name:"));         add(nameField);
        add(new JLabel("PAN:"));          add(panField);
        add(new JLabel("DOB (DD/MM/YYYY):")); add(dobField);
        add(new JLabel("Password:"));     add(passField);
        add(strengthLabel);

        JButton generate = new JButton("Generate Password");
        generate.addActionListener(e -> generate());
        add(generate);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    void generate() {
        String name = nameField.getText().trim();
        String pan  = panField.getText().trim().toUpperCase();
        String dob  = dobField.getText().trim().replaceAll("/", "");

        if (name.isEmpty() || pan.isEmpty() || dob.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.");
            return;
        }

        try {
            String raw = name + pan + dob + "Salt#2025";
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(raw.getBytes());
            String base = Base64.getEncoder().encodeToString(hash).substring(0, 12);

            // Ensure at least one digit, special char, uppercase
            String password = base + pan.charAt(0) + dob.charAt(0) + "@" + name.length();
            passField.setText(password);
            strengthLabel.setText("Strength: " + classify(password));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    String classify(String p) {
        int score = 0;
        if (p.length() >= 12) score++;
        if (p.matches(".*[A-Z].*")) score++;
        if (p.matches(".*[0-9].*")) score++;
        if (p.matches(".*[^A-Za-z0-9].*")) score++;
        return switch (score) {
            case 4 -> "🟢 Strong";
            case 3 -> "🟡 Medium";
            default -> "🔴 Weak";
        };
    }

    public static void main(String[] args) {
        // Test with multiple users
        String[][] users = {
            {"Alice",  "ABCDE1234F", "01/01/1990"},
            {"Bob",    "PQRST5678G", "15/06/1985"},
            {"Charlie","XYZAB9012H", "20/12/2000"}
        };

        System.out.println("=== Password Manager Test ===");
        PasswordManager pm = new PasswordManager();
        for (String[] u : users) {
            pm.nameField.setText(u[0]);
            pm.panField.setText(u[1]);
            pm.dobField.setText(u[2]);
            pm.generate();
            System.out.printf("Name: %-10s | Password: %-18s | %s%n",
                u[0], pm.passField.getText(), pm.strengthLabel.getText());
        }
        System.out.println("=============================");
    }
}
