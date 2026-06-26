package view.util;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * TopBar - Premium top navigation bar with breadcrumb, clock and notification area.
 */
public class TopBar extends JPanel {

    private JLabel breadcrumb;
    private JLabel clockLabel;
    private JLabel notifLabel;
    private Timer clockTimer;

    public TopBar(String pageTitle) {
        setBackground(AppTheme.TOPBAR_BG);
        setPreferredSize(new Dimension(0, 58));
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, AppTheme.TOPBAR_BORDER));
        setLayout(new BorderLayout());

        // Left: breadcrumb
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        leftPanel.setBackground(AppTheme.TOPBAR_BG);

        JLabel homeLabel = new JLabel("Home");
        homeLabel.setFont(AppTheme.FONT_SMALL);
        homeLabel.setForeground(AppTheme.TEXT_MUTED);

        JLabel arrow = new JLabel("›");
        arrow.setFont(AppTheme.FONT_BODY);
        arrow.setForeground(AppTheme.TEXT_MUTED);

        breadcrumb = new JLabel(pageTitle);
        breadcrumb.setFont(AppTheme.FONT_BODY_BOLD);
        breadcrumb.setForeground(AppTheme.TEXT_PRIMARY);

        leftPanel.add(homeLabel);
        leftPanel.add(arrow);
        leftPanel.add(breadcrumb);

        // Right: clock + notifications + avatar
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 0));
        rightPanel.setBackground(AppTheme.TOPBAR_BG);

        // Live clock
        clockLabel = new JLabel();
        clockLabel.setFont(AppTheme.FONT_SMALL);
        clockLabel.setForeground(AppTheme.TEXT_SECONDARY);
        updateClock();
        clockTimer = new Timer(1000, e -> updateClock());
        clockTimer.start();

        // Notification bell
        notifLabel = new JLabel("🔔");
        notifLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        notifLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        notifLabel.setToolTipText("Notifications");

        // Vertical separator
        JSeparator sep = new JSeparator(JSeparator.VERTICAL);
        sep.setPreferredSize(new Dimension(1, 24));
        sep.setForeground(AppTheme.TOPBAR_BORDER);

        // User avatar chip
        JPanel userChip = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        userChip.setBackground(new Color(241, 245, 249));
        userChip.setBorder(BorderFactory.createCompoundBorder(
            new AppTheme.RoundedBorder(AppTheme.CARD_BORDER, 20, 1),
            BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));
        JLabel userAvatar = new JLabel("👤");
        userAvatar.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        JLabel userName = new JLabel("Admin");
        userName.setFont(AppTheme.FONT_SMALL);
        userName.setForeground(AppTheme.TEXT_SECONDARY);
        userChip.add(userAvatar);
        userChip.add(userName);

        rightPanel.add(clockLabel);
        rightPanel.add(sep);
        rightPanel.add(notifLabel);
        rightPanel.add(userChip);

        // Center panels vertically
        JPanel centerLeft = new JPanel(new GridBagLayout());
        centerLeft.setBackground(AppTheme.TOPBAR_BG);
        centerLeft.add(leftPanel);

        JPanel centerRight = new JPanel(new GridBagLayout());
        centerRight.setBackground(AppTheme.TOPBAR_BG);
        centerRight.add(rightPanel);

        add(centerLeft, BorderLayout.WEST);
        add(centerRight, BorderLayout.EAST);
    }

    private void updateClock() {
        clockLabel.setText(new SimpleDateFormat("EEE, MMM d  HH:mm:ss").format(new Date()));
    }

    public void setBreadcrumb(String pageTitle) {
        breadcrumb.setText(pageTitle);
    }

    /** Stop the clock timer when frame closes */
    public void dispose() {
        if (clockTimer != null) clockTimer.stop();
    }
}
