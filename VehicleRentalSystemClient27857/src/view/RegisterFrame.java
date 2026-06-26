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

/**
 * RegisterFrame - Premium redesigned registration screen with OTP email verification.
 */
public class RegisterFrame extends javax.swing.JFrame {

    private JTextField txtFullName, txtEmail, txtPhone;
    private JPasswordField txtPassword, txtConfirmPassword;
    private JComboBox<String> cmbRole;
    private JButton btnRegister, btnBack;
    private JLabel lblError;

    public RegisterFrame() {
        AppTheme.applyGlobalDefaults();
        initComponents();
        setTitle("Vehicle Rental System — Register");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(960, 620);
        setLocationRelativeTo(null);
        setResizable(true);
        buildUI();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

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
        root.setBackground(AppTheme.CONTENT_BG);

        // ── LEFT BRAND PANEL ──────────────────────────────────────────────────
        JPanel brandPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                GradientPaint gp = new GradientPaint(0, 0, new Color(15, 23, 42),
                    getWidth(), getHeight(), new Color(20, 60, 110));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        brandPanel.setOpaque(false);
        brandPanel.setPreferredSize(new Dimension(360, 0));

        JPanel brandContent = new JPanel();
        brandContent.setLayout(new BoxLayout(brandContent, BoxLayout.Y_AXIS));
        brandContent.setOpaque(false);
        brandContent.setBorder(BorderFactory.createEmptyBorder(0, 36, 0, 36));

        JLabel icon = new JLabel("🔐", JLabel.CENTER);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 52));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("Join the Platform", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Create your account to access", JLabel.CENTER);
        sub.setFont(AppTheme.FONT_BODY);
        sub.setForeground(new Color(148, 163, 184));
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub2 = new JLabel("the rental management system.", JLabel.CENTER);
        sub2.setFont(AppTheme.FONT_BODY);
        sub2.setForeground(new Color(148, 163, 184));
        sub2.setAlignmentX(Component.CENTER_ALIGNMENT);

        brandContent.add(icon);
        brandContent.add(Box.createVerticalStrut(16));
        brandContent.add(title);
        brandContent.add(Box.createVerticalStrut(10));
        brandContent.add(sub);
        brandContent.add(sub2);

        brandPanel.add(brandContent);

        // ── RIGHT FORM ────────────────────────────────────────────────────────
        JPanel formOuter = new JPanel(new GridBagLayout());
        formOuter.setBackground(AppTheme.CONTENT_BG);

        JPanel formCard = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                for (int i = 5; i > 0; i--) {
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
        formCard.setPreferredSize(new Dimension(440, 760));
        formCard.setLayout(new BorderLayout());
        formCard.setBorder(BorderFactory.createEmptyBorder(30, 36, 30, 36));

        JPanel formInner = new JPanel();
        formInner.setLayout(new BoxLayout(formInner, BoxLayout.Y_AXIS));
        formInner.setOpaque(false);
        formInner.setPreferredSize(new Dimension(400, 700));

        JLabel heading = new JLabel("Create Account");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 22));
        heading.setForeground(AppTheme.TEXT_PRIMARY);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sub3 = new JLabel("Fill in your information below");
        sub3.setFont(AppTheme.FONT_BODY);
        sub3.setForeground(AppTheme.TEXT_SECONDARY);
        sub3.setAlignmentX(Component.LEFT_ALIGNMENT);

        formInner.add(heading);
        formInner.add(Box.createVerticalStrut(4));
        formInner.add(sub3);
        formInner.add(Box.createVerticalStrut(20));

        txtFullName = field(formInner, "Full Name",     "John Doe",          false);
        txtEmail    = field(formInner, "Email Address", "you@example.com",   false);
        txtPhone    = field(formInner, "Phone Number",  "+1 (555) 000-0000", false);
        txtPassword = null;
        txtConfirmPassword = null;

        JLabel passLbl = lbl("Password");
        passLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtPassword = AppTheme.createPasswordField();
        txtPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        txtPassword.setAlignmentX(Component.LEFT_ALIGNMENT);
        formInner.add(passLbl);
        formInner.add(Box.createVerticalStrut(5));
        formInner.add(txtPassword);
        formInner.add(Box.createVerticalStrut(12));

        JLabel confirmLbl = lbl("Confirm Password");
        confirmLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtConfirmPassword = AppTheme.createPasswordField();
        txtConfirmPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        txtConfirmPassword.setAlignmentX(Component.LEFT_ALIGNMENT);
        formInner.add(confirmLbl);
        formInner.add(Box.createVerticalStrut(5));
        formInner.add(txtConfirmPassword);
        formInner.add(Box.createVerticalStrut(12));

        JLabel roleLbl = lbl("Account Type");
        roleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        cmbRole = AppTheme.createComboBox(new String[]{"CUSTOMER", "ADMIN"});
        cmbRole.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        cmbRole.setAlignmentX(Component.LEFT_ALIGNMENT);
        formInner.add(roleLbl);
        formInner.add(Box.createVerticalStrut(5));
        formInner.add(cmbRole);
        formInner.add(Box.createVerticalStrut(6));

        lblError = new JLabel(" ");
        lblError.setFont(AppTheme.FONT_SMALL);
        lblError.setForeground(AppTheme.DANGER);
        lblError.setAlignmentX(Component.LEFT_ALIGNMENT);
        formInner.add(lblError);
        formInner.add(Box.createVerticalStrut(14));

        btnRegister = AppTheme.createPrimaryButton("Create Account");
        btnRegister.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btnRegister.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnRegister.addActionListener(e -> doRegister());

        btnBack = AppTheme.createSecondaryButton("Back to Login");
        btnBack.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnBack.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnBack.addActionListener(e -> { new LoginFrame().setVisible(true); dispose(); });

        formInner.add(btnRegister);
        formInner.add(Box.createVerticalStrut(8));
        formInner.add(btnBack);

        formCard.add(formInner, BorderLayout.CENTER);
        formOuter.add(formCard);

        root.add(brandPanel, BorderLayout.WEST);
        JScrollPane scroll =
            new JScrollPane(formOuter);

        scroll.setBorder(null);

        scroll.getVerticalScrollBar()
              .setUnitIncrement(16);

        scroll.setHorizontalScrollBarPolicy(
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );

        root.add(scroll, BorderLayout.CENTER);
                setContentPane(root);
    }

    private JTextField field(JPanel parent, String labelText, String placeholder, boolean isPassword) {
        JLabel lbl = lbl(labelText);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        JTextField tf = AppTheme.createTextField(placeholder);
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        tf.setAlignmentX(Component.LEFT_ALIGNMENT);
        parent.add(lbl);
        parent.add(Box.createVerticalStrut(5));
        parent.add(tf);
        parent.add(Box.createVerticalStrut(12));
        return tf;
    }

    private JLabel lbl(String text) {
        JLabel l = new JLabel(text);
        l.setFont(AppTheme.FONT_BODY_BOLD);
        l.setForeground(AppTheme.TEXT_PRIMARY);
        return l;
    }

    private String generateOtpCode() {
        return String.format("%06d", new Random().nextInt(1000000));
    }

    private Date expiryIn2Minutes() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, 2);
        return cal.getTime();
    }

    private void doRegister() {
        String name    = txtFullName.getText().trim();
        String email   = txtEmail.getText().trim();
        String phone   = txtPhone.getText().trim();
        String pass    = new String(txtPassword.getPassword()).trim();
        String confirm = new String(txtConfirmPassword.getPassword()).trim();
        String role    = (String) cmbRole.getSelectedItem();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || pass.isEmpty()) {
            lblError.setText("All fields are required."); return;
        }
        if (!pass.equals(confirm)) {
            lblError.setText("Passwords do not match."); return;
        }
        lblError.setText(" ");
        btnRegister.setEnabled(false);

        // Step 1: save user account
        SwingWorker<String, Void> regWorker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                Registry registry = ClientRegistry.getRegistry();
                UserService service = (UserService) registry.lookup("user");
                User user = new User(0, name, email, pass, phone, role, "ACTIVE");
                return service.registerUser(user);
            }
            @Override
            protected void done() {
                try {
                    get(); // rethrow if exception
                    // Step 2: send OTP for email verification
                    sendOtpAndVerify(email);
                } catch (Exception ex) {
                    btnRegister.setEnabled(true);
                    lblError.setText("Error: " + ex.getMessage());
                }
            }
        };
        regWorker.execute();
    }

    /**
     * Generates OTP, saves it via RMI (server emails it), then shows the OTP dialog.
     * On success, navigates to LoginFrame.
     */
    private void sendOtpAndVerify(String email) {
        String otpCode = generateOtpCode();
        Date   expiry  = expiryIn2Minutes();

        SwingWorker<String, Void> otpWorker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                Registry registry = ClientRegistry.getRegistry();
                OTPService otpService = (OTPService) registry.lookup("otp");
                OTPVerification otp = new OTPVerification(0, email, otpCode, expiry, false);
                return otpService.saveOTP(otp);
            }
            @Override
            protected void done() {
                btnRegister.setEnabled(true);
                try {
                    get();
                    // Show OTP dialog; on success, go to login
                    OTPVerificationDialog.show(RegisterFrame.this, email, () -> {
                        JOptionPane.showMessageDialog(null,
                            "Email verified! Your account is ready.\nYou can now log in.",
                            "Registration Complete", JOptionPane.INFORMATION_MESSAGE);
                        new LoginFrame().setVisible(true);
                        dispose();
                    });
                } catch (Exception ex) {
                    lblError.setText("Failed to send OTP: " + ex.getMessage());
                }
            }
        };
        otpWorker.execute();
    }
}
