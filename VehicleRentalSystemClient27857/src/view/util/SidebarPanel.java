package view.util;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * SidebarPanel - Reusable warm-brown sidebar with nav items and profile section.
 * No emoji — icons are Unicode symbols rendered via standard fonts.
 */
public class SidebarPanel extends JPanel {

    private JPanel menuContainer;
    private JLabel logoLabel;
    private JLabel logoSub;
    private JPanel profilePanel;
    private JLabel profileName;
    private JLabel profileRole;
    private SidebarItem activeItem;

    public SidebarPanel() {
        setPreferredSize(new Dimension(230, 0));
        setBackground(AppTheme.SIDEBAR_BG);
        setLayout(new BorderLayout());

        // ── Logo Section ──────────────────────────────────────────────────────
        JPanel logoSection = new JPanel();
        logoSection.setBackground(AppTheme.SIDEBAR_BG);
        logoSection.setLayout(new BoxLayout(logoSection, BoxLayout.Y_AXIS));
        logoSection.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        JPanel logoBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 16));
        logoBox.setBackground(AppTheme.SIDEBAR_BG);

        // Drawn logo icon — amber-brown rounded square with a "V" monogram
        JPanel logoIconPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Background square
                g2.setColor(AppTheme.SIDEBAR_LOGO_BG);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                // "V" letter as the icon
                g2.setColor(new Color(255, 240, 215));
                g2.setFont(new Font("Georgia", Font.BOLD, 20));
                FontMetrics fm = g2.getFontMetrics();
                String letter = "V";
                int x = (getWidth() - fm.stringWidth(letter)) / 2;
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(letter, x, y);
                g2.dispose();
            }
        };
        logoIconPanel.setPreferredSize(new Dimension(38, 38));
        logoIconPanel.setOpaque(false);

        JPanel logoText = new JPanel();
        logoText.setLayout(new BoxLayout(logoText, BoxLayout.Y_AXIS));
        logoText.setBackground(AppTheme.SIDEBAR_BG);
        logoLabel = new JLabel("VRS System");
        logoLabel.setFont(AppTheme.FONT_SIDEBAR_LOGO);
        logoLabel.setForeground(Color.WHITE);
        logoSub = new JLabel("Rental Platform");
        logoSub.setFont(AppTheme.FONT_SMALL);
        logoSub.setForeground(AppTheme.SIDEBAR_TEXT);
        logoText.add(logoLabel);
        logoText.add(logoSub);

        logoBox.add(logoIconPanel);
        logoBox.add(logoText);

        // Divider
        JPanel divider = new JPanel();
        divider.setBackground(new Color(65, 38, 18));
        divider.setPreferredSize(new Dimension(0, 1));
        divider.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        logoSection.add(logoBox);
        logoSection.add(divider);

        // ── Menu Container ────────────────────────────────────────────────────
        menuContainer = new JPanel();
        menuContainer.setLayout(new BoxLayout(menuContainer, BoxLayout.Y_AXIS));
        menuContainer.setBackground(AppTheme.SIDEBAR_BG);
        menuContainer.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

        JScrollPane menuScroll = new JScrollPane(menuContainer);
        menuScroll.setBorder(BorderFactory.createEmptyBorder());
        menuScroll.setBackground(AppTheme.SIDEBAR_BG);
        menuScroll.getViewport().setBackground(AppTheme.SIDEBAR_BG);
        menuScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        menuScroll.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));

        // ── Profile Section ───────────────────────────────────────────────────
        profilePanel = new JPanel(new BorderLayout());
        profilePanel.setBackground(new Color(55, 30, 12));
        profilePanel.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));

        JPanel profileInfo = new JPanel(new GridLayout(2, 1, 0, 2));
        profileInfo.setBackground(new Color(55, 30, 12));
        profileName = new JLabel("Administrator");
        profileName.setFont(AppTheme.FONT_BODY_BOLD);
        profileName.setForeground(Color.WHITE);
        profileRole = new JLabel("System Admin");
        profileRole.setFont(AppTheme.FONT_SMALL);
        profileRole.setForeground(AppTheme.SIDEBAR_TEXT);
        profileInfo.add(profileName);
        profileInfo.add(profileRole);

        // Avatar: drawn initials circle instead of emoji
        JPanel avatarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AppTheme.SIDEBAR_LOGO_BG);
                g2.fillOval(0, 2, 30, 30);
                g2.setColor(new Color(255, 240, 215));
                g2.setFont(new Font("Georgia", Font.BOLD, 12));
                FontMetrics fm = g2.getFontMetrics();
                String initials = "AD";
                int x = (30 - fm.stringWidth(initials)) / 2;
                int y = 2 + (30 - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(initials, x, y);
                g2.dispose();
            }
        };
        avatarPanel.setPreferredSize(new Dimension(38, 34));
        avatarPanel.setOpaque(false);

        profilePanel.add(avatarPanel, BorderLayout.WEST);
        profilePanel.add(profileInfo, BorderLayout.CENTER);

        add(logoSection, BorderLayout.NORTH);
        add(menuScroll, BorderLayout.CENTER);
        add(profilePanel, BorderLayout.SOUTH);
    }

    /**
     * Add a section heading (e.g., "MAIN MENU", "REPORTS").
     */
    public void addSectionLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Georgia", Font.BOLD, 10));
        lbl.setForeground(new Color(120, 80, 50));
        lbl.setBorder(BorderFactory.createEmptyBorder(16, 20, 6, 20));
        lbl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        menuContainer.add(lbl);
    }

    /**
     * Add a clickable menu item.
     * icon: a short Unicode symbol string, e.g. "\u25A0", "\u2630", "\u25B6"
     */
    public SidebarItem addMenuItem(String icon, String label, ActionListener action) {
        SidebarItem item = new SidebarItem(icon, label, action, this);
        menuContainer.add(item);
        return item;
    }

    public void addSeparator() {
        JPanel sep = new JPanel();
        sep.setBackground(new Color(65, 38, 18));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
        menuContainer.add(Box.createVerticalStrut(4));
        menuContainer.add(sep);
        menuContainer.add(Box.createVerticalStrut(4));
    }

    public void setActiveItem(SidebarItem item) {
        if (activeItem != null) activeItem.setActive(false);
        activeItem = item;
        if (item != null) item.setActive(true);
    }

    public void setProfileName(String name) { profileName.setText(name); }
    public void setProfileRole(String role) { profileRole.setText(role); }

    // ─── SIDEBAR ITEM ─────────────────────────────────────────────────────────
    public static class SidebarItem extends JPanel {
        private boolean active = false;
        private final JLabel iconLabel;
        private final JLabel textLabel;
        private final SidebarPanel sidebar;

        public SidebarItem(String icon, String text, ActionListener action, SidebarPanel sidebar) {
            this.sidebar = sidebar;
            setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
            setBackground(AppTheme.SIDEBAR_BG);
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
            setPreferredSize(new Dimension(230, 44));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));

            JPanel inner = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
            inner.setOpaque(false);
            inner.setPreferredSize(new Dimension(210, 44));

            // Icon: standard font, no emoji font
            iconLabel = new JLabel(icon);
            iconLabel.setFont(new Font("Dialog", Font.PLAIN, 14));
            iconLabel.setForeground(AppTheme.SIDEBAR_TEXT);

            textLabel = new JLabel(text);
            textLabel.setFont(AppTheme.FONT_SIDEBAR_ITEM);
            textLabel.setForeground(AppTheme.SIDEBAR_TEXT);

            inner.add(iconLabel);
            inner.add(textLabel);
            add(inner);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (!active) setBackground(AppTheme.SIDEBAR_HOVER);
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    if (!active) setBackground(AppTheme.SIDEBAR_BG);
                }
                @Override
                public void mouseClicked(MouseEvent e) {
                    sidebar.setActiveItem(SidebarItem.this);
                    action.actionPerformed(null);
                }
            });
        }

        public void setActive(boolean active) {
            this.active = active;
            setBackground(active ? AppTheme.SIDEBAR_ACTIVE : AppTheme.SIDEBAR_BG);
            textLabel.setForeground(active ? AppTheme.SIDEBAR_TEXT_ACTIVE : AppTheme.SIDEBAR_TEXT);
            iconLabel.setForeground(active ? AppTheme.SIDEBAR_TEXT_ACTIVE : AppTheme.SIDEBAR_TEXT);
        }
    }
}