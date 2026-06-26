package view;

import view.util.AppTheme;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * SplashScreen - Premium animated startup splash.
 * Usage: new SplashScreen(afterRunnable).setVisible(true);
 * Note: SplashScreen is a JWindow (not JFrame), so it's primarily code-based.
 */
public class SplashScreen extends JWindow {

    private int progress = 0;
    private JProgressBar progressBar;
    private JLabel statusLabel;
    private final Runnable onComplete;

    private static final String[] LOADING_MESSAGES = {
        "Initializing system...",
        "Connecting to RMI registry...",
        "Loading vehicle database...",
        "Preparing dashboard...",
        "Ready!"
    };

    public SplashScreen(Runnable onComplete) {
        this.onComplete = onComplete;
        initComponents();
        buildUI();
    }

    /**
     * Initialize JWindow components.
     */
    private void initComponents() {
        // JWindow initialization
    }

    private void buildUI() {
        setSize(520, 340);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Gradient background
                GradientPaint gp = new GradientPaint(
                    0, 0, AppTheme.SIDEBAR_BG,
                    getWidth(), getHeight(), new Color(15, 40, 80)
                );
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.dispose();
            }
        };
        root.setOpaque(false);
        root.setBorder(BorderFactory.createEmptyBorder(40, 50, 36, 50));

        // Center content
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);

        // Logo icon
        //JLabel logoEmoji = new JLabel("🚗", JLabel.CENTER);
        //logoEmoji.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 52));
        //logoEmoji.setAlignmentX(Component.CENTER_ALIGNMENT);

        // App name
        JLabel appName = new JLabel("Vehicle Rental System", JLabel.CENTER);
        appName.setFont(new Font("Segoe UI", Font.BOLD, 26));
        appName.setForeground(Color.WHITE);
        appName.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Tagline
        JLabel tagline = new JLabel("Enterprise Management Platform", JLabel.CENTER);
        tagline.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tagline.setForeground(new Color(148, 163, 184));
        tagline.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Version
        JLabel version = new JLabel("MBISHIBISHI Flavien  ·  27857", JLabel.CENTER);
        version.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        version.setForeground(new Color(71, 85, 105));
        version.setAlignmentX(Component.CENTER_ALIGNMENT);

        //centerPanel.add(logoEmoji);
        centerPanel.add(Box.createVerticalStrut(12));
        centerPanel.add(appName);
        centerPanel.add(Box.createVerticalStrut(6));
        centerPanel.add(tagline);
        centerPanel.add(Box.createVerticalStrut(4));
        centerPanel.add(version);

        // Bottom: progress area
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setOpaque(false);

        statusLabel = new JLabel(LOADING_MESSAGES[0], JLabel.LEFT);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusLabel.setForeground(new Color(100, 116, 139));
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        progressBar = new JProgressBar(0, 100) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Track
                g2.setColor(new Color(30, 41, 59));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                // Fill
                int fillW = (int) (getWidth() * (getValue() / (double) getMaximum()));
                if (fillW > 0) {
                    GradientPaint gp = new GradientPaint(
                        0, 0, AppTheme.PRIMARY,
                        fillW, 0, new Color(96, 165, 250)
                    );
                    g2.setPaint(gp);
                    g2.fillRoundRect(0, 0, fillW, getHeight(), 6, 6);
                }
                g2.dispose();
            }
        };
        progressBar.setBorderPainted(false);
        progressBar.setOpaque(false);
        progressBar.setPreferredSize(new Dimension(0, 6));
        progressBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 6));
        progressBar.setAlignmentX(Component.LEFT_ALIGNMENT);

        bottomPanel.add(statusLabel);
        bottomPanel.add(Box.createVerticalStrut(8));
        bottomPanel.add(progressBar);

        // Credit line
        JLabel credit = new JLabel("© 2024 VehicleRentalSystemServer27857", JLabel.CENTER);
        credit.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        credit.setForeground(new Color(51, 65, 85));
        credit.setAlignmentX(Component.CENTER_ALIGNMENT);
        credit.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        root.add(centerPanel, BorderLayout.CENTER);
        root.add(bottomPanel, BorderLayout.SOUTH);

        // Border
        getRootPane().setBorder(BorderFactory.createLineBorder(new Color(30, 41, 59), 1));
        add(root);

        // Animate loading
        Timer timer = new Timer(60, null);
        timer.addActionListener(new ActionListener() {
            int msgIdx = 0;
            @Override
            public void actionPerformed(ActionEvent e) {
                progress += 2;
                progressBar.setValue(progress);
                if (progress % 20 == 0 && msgIdx < LOADING_MESSAGES.length - 1) {
                    msgIdx++;
                    statusLabel.setText(LOADING_MESSAGES[msgIdx]);
                }
                if (progress >= 100) {
                    timer.stop();
                    Timer closeTimer = new Timer(400, ev -> {
                        setVisible(false);
                        dispose();
                        if (onComplete != null) {
                            SwingUtilities.invokeLater(onComplete);
                        }
                    });
                    closeTimer.setRepeats(false);
                    closeTimer.start();
                }
            }
        });
        timer.start();
    }
}
