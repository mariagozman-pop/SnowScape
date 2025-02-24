import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.plaf.basic.BasicButtonUI;

public class MainFrame extends JFrame {
        private int currentUserId = -1;  // Variable to store the current user ID

        public MainFrame() {
            setTitle("SnowScape");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(500, 300);
            setLocationRelativeTo(null);
            setLayout(new BorderLayout());

            // Background panel with an image
            JPanel backgroundPanel = new JPanel(new BorderLayout()) {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    ImageIcon background = new ImageIcon("C:/Users/Maria Gozman-Pop/OneDrive/Desktop/DB/SnowScape/resources/mountain-ranges-covered-in-snow-714258.png");
                    g.drawImage(background.getImage(), 0, 0, this.getWidth(), this.getHeight(), this);
                }
            };
            setContentPane(backgroundPanel);

            JPanel buttonPanel = new JPanel();
            buttonPanel.setOpaque(false);
            buttonPanel.setLayout(new GridLayout(3, 1, 10, 10));
            buttonPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

            JLabel welcomeLabel = new JLabel("<html>Welcome to SnowScape!<br>Choose your winter adventure!</html>", SwingConstants.CENTER);
            welcomeLabel.setForeground(Color.WHITE);
            welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 16));

            JButton loginButton = new JButton("Login");
            styleButton(loginButton);

            JButton createAccountButton = new JButton("Create Account");
            styleButton(createAccountButton);

            buttonPanel.add(welcomeLabel);
            buttonPanel.add(loginButton);
            buttonPanel.add(createAccountButton);

            backgroundPanel.add(buttonPanel, BorderLayout.CENTER);

            loginButton.addActionListener(e -> openLoginFrame());
            createAccountButton.addActionListener(e -> openCreateAccountFrame());
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
            button.setUI(new RoundedButtonUI());

            button.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    button.setBackground(new Color(93, 109, 126));
                }

                public void mouseExited(java.awt.event.MouseEvent evt) {
                    button.setBackground(new Color(108, 122, 137));
                }
            });
        }

        // Custom UI class to create rounded buttons
        public static class RoundedButtonUI extends BasicButtonUI {
            private static final int RADIUS = 30;

            @Override
            public void paint(Graphics g, JComponent c) {
                AbstractButton b = (AbstractButton) c;
                ButtonModel model = b.getModel();

                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (model.isPressed()) {
                    g2.setColor(b.getBackground().darker());
                } else if (b.isRolloverEnabled() && model.isRollover()) {
                    g2.setColor(b.getBackground().brighter());
                } else {
                    g2.setColor(b.getBackground());
                }

                g2.fill(new RoundRectangle2D.Float(0, 0, c.getWidth(), c.getHeight(), RADIUS, RADIUS));
                super.paint(g2, c);

                g2.dispose();
            }
        }

        private void openLoginFrame() {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
            loginFrame.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                    if (loginFrame.isSuccessfulLogin()) {
                        currentUserId = loginFrame.getCurrentUserId();
                        openSearchFrame();
                        dispose();
                    }
                }
            });
        }

        private void openCreateAccountFrame() {
            CreateAccountFrame createAccountFrame = new CreateAccountFrame();
            createAccountFrame.setVisible(true);
            createAccountFrame.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                    if (createAccountFrame.isSuccessfulAccountCreation()) {
                        currentUserId = createAccountFrame.getCurrentUserId();
                        openSearchFrame();
                        dispose();
                    }
                }
            });
        }

        private void openSearchFrame() {
            SkiCenterSearchFrame searchFrame = new SkiCenterSearchFrame(currentUserId);
            searchFrame.setVisible(true);
        }
    }
