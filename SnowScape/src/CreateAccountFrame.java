import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;

public class CreateAccountFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private boolean successfulAccountCreation = false;
    private int currentUserId = -1;

    public CreateAccountFrame() {
        setTitle("Create Account");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(300, 200);
        setLocationRelativeTo(null);

        // Create an instance of BackgroundPanel and set it as the content pane
        BackgroundPanel backgroundPanel = new BackgroundPanel();
        setContentPane(backgroundPanel);
        backgroundPanel.setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setOpaque(false);

        usernameField = new JTextField();
        passwordField = new JPasswordField();

        JButton createAccountButton = new JButton("Create Account");
        styleButton(createAccountButton);

        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(createAccountButton);

        backgroundPanel.add(panel, BorderLayout.CENTER);

        createAccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String newUsername = usernameField.getText();
                String newPassword = new String(passwordField.getPassword());

                // Replace this with your actual account creation logic
                CreateAccount createAccount = new CreateAccount();
                if (createAccount.checkUsernameExists(newUsername)) {
                    JOptionPane.showMessageDialog(CreateAccountFrame.this, "Username already exists! Please try a different username.", "Username Exists", JOptionPane.ERROR_MESSAGE);
                    usernameField.setText("");
                    passwordField.setText("");
                } else {
                    int userId = createAccount.createUserAndGetId(newUsername, newPassword);
                    if (userId != -1) {
                        JOptionPane.showMessageDialog(CreateAccountFrame.this,
                                "User registered successfully!",
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                        successfulAccountCreation = true;
                        currentUserId = userId;
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(CreateAccountFrame.this,
                                "Failed to create user account.",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(108, 122, 137));
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

        // Set the button to be round
        button.setContentAreaFilled(false);
        button.setUI(new MainFrame.RoundedButtonUI());

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(93, 109, 126));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(108, 122, 137));
            }
        });
    }

    public boolean isSuccessfulAccountCreation() {
        return successfulAccountCreation;
    }

    public int getCurrentUserId() {
        return currentUserId;
    }

    private static class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel() {
            // Load the background image
            ImageIcon icon = new ImageIcon("C:/Users/Maria Gozman-Pop/OneDrive/Desktop/DB/SnowScape/resources/pexels-photo-140234-1140x760.png"); // Replace with your image path
            backgroundImage = icon.getImage();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            // Draw the background image
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, this.getWidth(), this.getHeight(), this);
            }
        }
    }
}