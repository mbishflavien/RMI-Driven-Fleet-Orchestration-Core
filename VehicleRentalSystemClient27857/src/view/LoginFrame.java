package view;

import view.util.AppTheme;
import controller.ClientRegistry;
import model.OTPVerification;
import model.User;
import service.OTPService;
import service.UserService;

import java.awt.*;
import java.awt.event.*;
import java.rmi.registry.Registry;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import javax.swing.*;
import com.formdev.flatlaf.FlatLightLaf;

/**
 * LoginFrame - Premium redesigned login screen with OTP email verification.
 */
public class LoginFrame extends javax.swing.JFrame {

    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnRegister;
    private JLabel lblError;
    private JLabel lblStatus;

    public LoginFrame() {
        AppTheme.applyGlobalDefaults();
        initComponents();
        setTitle("Vehicle Rental System — Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(960, 620);
        setLocationRelativeTo(null);
        setResizable(false);
        buildUI();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(AppTheme.SIDEBAR_BG);

        // ── LEFT BRAND PANEL ──────────────────────────────────────────────────
        JPanel brandPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                    0, 0, new Color(78, 52, 46),
                    getWidth(), getHeight(), new Color(141, 110, 99)
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        brandPanel.setOpaque(false);
        brandPanel.setPreferredSize(new Dimension(400, 0));

        JPanel brandContent = new JPanel();
        brandContent.setLayout(new BoxLayout(brandContent, BoxLayout.Y_AXIS));
        brandContent.setOpaque(false);
        brandContent.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 40));

        JLabel logoIcon = new JLabel("", JLabel.CENTER);
        logoIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 56));
        logoIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel appName = new JLabel("Vehicle Rental", JLabel.CENTER);
        appName.setFont(new Font("Segoe UI", Font.BOLD, 28));
        appName.setForeground(Color.WHITE);
        appName.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel appName2 = new JLabel("Management System", JLabel.CENTER);
        appName2.setFont(new Font("Segoe UI", Font.BOLD, 28));
        appName2.setForeground(Color.WHITE);
        appName2.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel tagline = new JLabel("Enterprise Fleet & Rental Platform", JLabel.CENTER);
        tagline.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tagline.setForeground(new Color(148, 163, 184));
        tagline.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel features = new JPanel();
        features.setLayout(new BoxLayout(features, BoxLayout.Y_AXIS));
        features.setOpaque(false);
        features.setBorder(BorderFactory.createEmptyBorder(24, 0, 0, 0));
        String[] feats = {
            " Real-time Vehicle Tracking",
            " Automated Payment Processing",
            " Advanced Analytics & Reporting",
            " Multi-role Access Control"
        };
        for (String f : feats) {
            JLabel fl = new JLabel(f);
            fl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            fl.setForeground(Color.WHITE);
            fl.setAlignmentX(Component.CENTER_ALIGNMENT);
            fl.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
            features.add(fl);
        }

        brandContent.add(logoIcon);
        brandContent.add(Box.createVerticalStrut(16));
        brandContent.add(appName);
        brandContent.add(appName2);
        brandContent.add(Box.createVerticalStrut(8));
        brandContent.add(tagline);
        brandContent.add(features);

        brandPanel.add(brandContent);

        // ── RIGHT FORM PANEL ──────────────────────────────────────────────────
        JPanel formOuter = new JPanel(new GridBagLayout());
        formOuter.setBackground(AppTheme.CONTENT_BG);

        JPanel formCard = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                for (int i = 6; i > 0; i--) {
                    g2.setColor(new Color(0, 0, 0, 4 * i));
                    g2.fillRoundRect(i, i, getWidth() - i * 2, getHeight() - i * 2, 16, 16);
                }
                g2.setColor(AppTheme.CARD_BG);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        formCard.setOpaque(false);
        formCard.setPreferredSize(new Dimension(380, 460));
        formCard.setLayout(new BorderLayout());
        formCard.setBorder(BorderFactory.createEmptyBorder(36, 36, 36, 36));

        JPanel formInner = new JPanel();
        formInner.setLayout(new BoxLayout(formInner, BoxLayout.Y_AXIS));
        formInner.setOpaque(false);

        JLabel welcomeLabel = new JLabel("Welcome Back !");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        welcomeLabel.setForeground(AppTheme.TEXT_PRIMARY);
        welcomeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Sign in to your account");
        subtitleLabel.setFont(AppTheme.FONT_BODY);
        subtitleLabel.setForeground(AppTheme.TEXT_SECONDARY);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        formInner.add(welcomeLabel);
        formInner.add(Box.createVerticalStrut(4));
        formInner.add(subtitleLabel);
        formInner.add(Box.createVerticalStrut(28));

        // Email field
        JLabel emailLbl = new JLabel("Email Address");
        emailLbl.setFont(AppTheme.FONT_BODY_BOLD);
        emailLbl.setForeground(AppTheme.TEXT_PRIMARY);
        emailLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtEmail = AppTheme.createTextField("you@example.com");
        txtEmail.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        txtEmail.setAlignmentX(Component.LEFT_ALIGNMENT);

        formInner.add(emailLbl);
        formInner.add(Box.createVerticalStrut(6));
        formInner.add(txtEmail);
        formInner.add(Box.createVerticalStrut(16));

        // Password field
        JLabel passLbl = new JLabel("Password");
        passLbl.setFont(AppTheme.FONT_BODY_BOLD);
        passLbl.setForeground(AppTheme.TEXT_PRIMARY);
        passLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtPassword = AppTheme.createPasswordField();
        txtPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        txtPassword.setAlignmentX(Component.LEFT_ALIGNMENT);

        formInner.add(passLbl);
        formInner.add(Box.createVerticalStrut(6));
        formInner.add(txtPassword);
        formInner.add(Box.createVerticalStrut(6));

        // Error label
        lblError = new JLabel(" ");
        lblError.setFont(AppTheme.FONT_SMALL);
        lblError.setForeground(AppTheme.DANGER);
        lblError.setAlignmentX(Component.LEFT_ALIGNMENT);
        formInner.add(lblError);
        formInner.add(Box.createVerticalStrut(20));

        // Login button
        btnLogin = AppTheme.createPrimaryButton("Sign In");
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btnLogin.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnLogin.addActionListener(e -> doLogin());

        formInner.add(btnLogin);
        formInner.add(Box.createVerticalStrut(12));

        // Register link
        JPanel regPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        regPanel.setOpaque(false);
        regPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        JLabel regTxt = new JLabel("Don't have an account?");
        regTxt.setFont(AppTheme.FONT_SMALL);
        regTxt.setForeground(AppTheme.TEXT_SECONDARY);
        btnRegister = new JButton("Create Account");
        btnRegister.setFont(AppTheme.FONT_SMALL);
        btnRegister.setForeground(AppTheme.PRIMARY);
        btnRegister.setContentAreaFilled(false);
        btnRegister.setBorderPainted(false);
        btnRegister.setFocusPainted(false);
        btnRegister.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnRegister.addActionListener(e -> {
            new RegisterFrame().setVisible(true);
            dispose();
        });
        regPanel.add(regTxt);
        regPanel.add(btnRegister);
        formInner.add(regPanel);

        // Status label
        lblStatus = new JLabel(" ", JLabel.CENTER);
        lblStatus.setFont(AppTheme.FONT_SMALL);
        lblStatus.setForeground(AppTheme.TEXT_MUTED);
        lblStatus.setAlignmentX(Component.CENTER_ALIGNMENT);
        formInner.add(Box.createVerticalStrut(16));
        formInner.add(lblStatus);

        formCard.add(formInner, BorderLayout.CENTER);
        formOuter.add(formCard);

        root.add(brandPanel, BorderLayout.WEST);
        root.add(formOuter, BorderLayout.CENTER);
        setContentPane(root);

        getRootPane().setDefaultButton(btnLogin);
    }

    // ── Generates a random 6-digit OTP string ────────────────────────────────
    private String generateOtpCode() {
        return String.format("%06d", new Random().nextInt(1000000));
    }

    // ── Expiry: now + 2 minutes ───────────────────────────────────────────────
    private Date expiryIn2Minutes() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, 2);
        return cal.getTime();
    }

    private void doLogin() {
        String email    = txtEmail.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (email.isEmpty() || password.isEmpty()) {
            lblError.setText("Please fill in both email and password.");
            return;
        }

        lblError.setText(" ");
        lblStatus.setText("Authenticating...");
        btnLogin.setEnabled(false);

        SwingWorker<User, Void> worker = new SwingWorker<User, Void>() {
            @Override
            protected User doInBackground() throws Exception {
                Registry registry = ClientRegistry.getRegistry();
                UserService service = (UserService) registry.lookup("user");
                for (User u : service.displayAllUsers()) {
                    if (u.getEmail().equals(email) && u.getPassword().equals(password)) {
                        return u;
                    }
                }
                return null;
            }

            @Override
            protected void done() {
                btnLogin.setEnabled(true);
                lblStatus.setText(" ");
                try {
                    User user = get();
                    if (user != null) {
                        // Credentials OK — now send OTP
                        sendOtpAndVerify(user);
                    } else {
                        lblError.setText("Invalid email or password. Please try again.");
                    }
                } catch (Exception ex) {
                    lblError.setText("Connection error: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    /**
     * Generates an OTP, saves it (server emails it automatically),
     * then opens the OTP dialog. On success, opens the dashboard.
     */
    private void sendOtpAndVerify(User user) {
        String otpCode = generateOtpCode();
        Date   expiry  = expiryIn2Minutes();

        lblStatus.setText("Sending OTP to " + user.getEmail() + "...");
        btnLogin.setEnabled(false);

        SwingWorker<String, Void> otpWorker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                Registry registry = ClientRegistry.getRegistry();
                OTPService otpService = (OTPService) registry.lookup("otp");
                OTPVerification otp = new OTPVerification(0, user.getEmail(), otpCode, expiry, false);
                return otpService.saveOTP(otp);   // server sends the email here
            }

            @Override
            protected void done() {
                btnLogin.setEnabled(true);
                lblStatus.setText(" ");
                try {
                    get(); // rethrow any exception
                    // Open OTP dialog — on verified, open dashboard
                    OTPVerificationDialog.show(LoginFrame.this, user.getEmail(), () -> {
                        if (user.getRole().equalsIgnoreCase("ADMIN")) {
                            new AdminDashboardFrame(user).setVisible(true);
                        } else {
                            new CustomerDashboardFrame(user).setVisible(true);
                        }
                        dispose();
                    });
                } catch (Exception ex) {
                    lblError.setText("Failed to send OTP: " + ex.getMessage());
                }
            }
        };
        otpWorker.execute();
    }

    public static void main(String[] args) {
        try {
            com.formdev.flatlaf.FlatLightLaf.setup();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        AppTheme.applyGlobalDefaults();
        SwingUtilities.invokeLater(() -> {
            new SplashScreen(
                () -> new LoginFrame().setVisible(true)
            ).setVisible(true);
        });
    }
}
