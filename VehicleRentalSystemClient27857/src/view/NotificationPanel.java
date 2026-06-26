package view;

import view.util.AppTheme;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import javax.swing.*;

/**
 * NotificationPanel - Live notification / activity feed.
 *
 * Register with NotificationBus so it receives real-time push events:
 *   NotificationBus.get().register(this, NotificationBus.ROLE_ADMIN);
 *
 * Call addNotification() directly (or via the bus) to push new entries.
 * ownerId is used by the bus to route customer-specific messages.
 */
public class NotificationPanel extends JPanel {

    public enum NotifType { RENTAL, PAYMENT, VEHICLE, SYSTEM, ALERT }

    private JPanel     feedPanel;
    private JLabel     countBadge;
    private JLabel     filterActive = null;
    private String     currentFilter = "All";

    private final List<NotifEntry> notifications = new ArrayList<>();
    private int unreadCount = 0;

    /** The userId this panel belongs to; -1 means admin (receives all). */
    private int ownerId = -1;

    public NotificationPanel() {
        setLayout(new BorderLayout());
        setBackground(AppTheme.CONTENT_BG);
        buildUI();
    }

    /** Set the owner userId so the NotificationBus can route targeted pushes. */
    public void setOwnerId(int id) { this.ownerId = id; }
    public int  getOwnerId()       { return ownerId;   }

    // ── BUILD UI ──────────────────────────────────────────────────────────────

    private void buildUI() {
        // ── Header bar ──────────────────────────────────────────────────────
        JPanel headerBar = new JPanel(new BorderLayout());
        headerBar.setBackground(AppTheme.CARD_BG);
        headerBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, AppTheme.CARD_BORDER),
            BorderFactory.createEmptyBorder(14, 18, 14, 18)
        ));

        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titleRow.setBackground(AppTheme.CARD_BG);

        // Bell icon drawn, no emoji
        JPanel bellIcon = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AppTheme.PRIMARY);
                // Bell body (rounded rect)
                g2.fillRoundRect(3, 4, 14, 11, 5, 5);
                // Bell clapper
                g2.fillOval(7, 14, 6, 3);
                // Bell handle
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawArc(7, 1, 6, 6, 0, 180);
                g2.dispose();
            }
            @Override public Dimension getPreferredSize() { return new Dimension(20, 20); }
        };
        bellIcon.setOpaque(false);

        JLabel title = new JLabel("Notifications");
        title.setFont(AppTheme.FONT_SUBTITLE);
        title.setForeground(AppTheme.TEXT_PRIMARY);

        countBadge = new JLabel("0") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AppTheme.DANGER);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        countBadge.setFont(new Font("Georgia", Font.BOLD, 10));
        countBadge.setForeground(Color.WHITE);
        countBadge.setOpaque(false);
        countBadge.setHorizontalAlignment(JLabel.CENTER);
        countBadge.setPreferredSize(new Dimension(20, 20));
        countBadge.setVisible(false);

        titleRow.add(bellIcon);
        titleRow.add(title);
        titleRow.add(countBadge);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setBackground(AppTheme.CARD_BG);

        JButton markAllRead = AppTheme.createSecondaryButton("Mark all read");
        markAllRead.addActionListener(e -> markAllAsRead());

        JButton clearAll = AppTheme.createSecondaryButton("Clear all");
        clearAll.addActionListener(e -> clearAllNotifications());

        actions.add(markAllRead);
        actions.add(clearAll);

        headerBar.add(titleRow, BorderLayout.WEST);
        headerBar.add(actions,  BorderLayout.EAST);

        // ── Filter chips ────────────────────────────────────────────────────
        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        filterRow.setBackground(AppTheme.CONTENT_BG);
        filterRow.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, AppTheme.CARD_BORDER));

        String[] filters = {"All", "Rentals", "Payments", "Alerts", "System"};
        for (int i = 0; i < filters.length; i++) {
            boolean first = i == 0;
            JLabel chip = buildFilterChip(filters[i], first);
            if (first) filterActive = chip;
            filterRow.add(chip);
        }

        // ── Feed panel ──────────────────────────────────────────────────────
        feedPanel = new JPanel();
        feedPanel.setLayout(new BoxLayout(feedPanel, BoxLayout.Y_AXIS));
        feedPanel.setBackground(AppTheme.CONTENT_BG);
        feedPanel.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

        JScrollPane scroll = AppTheme.createScrollPane(feedPanel);
        scroll.getViewport().setBackground(AppTheme.CONTENT_BG);

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(AppTheme.CONTENT_BG);
        top.add(headerBar,  BorderLayout.NORTH);
        top.add(filterRow,  BorderLayout.SOUTH);

        add(top,    BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        // Populate with welcome message
        addNotification("System Ready",
            "Notification centre is live. Events will appear here in real time.",
            NotifType.SYSTEM);
    }

    // ── NOTIFICATION ENTRY UI ─────────────────────────────────────────────────

    private JPanel buildNotifItem(NotifEntry entry) {
        JPanel item = new JPanel(new BorderLayout(12, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(entry.read ? AppTheme.CONTENT_BG : new Color(255, 245, 228));
                g2.fillRect(0, 0, getWidth(), getHeight());
                if (!entry.read) {
                    g2.setColor(AppTheme.PRIMARY); // amber-brown left bar
                    g2.fillRect(0, 0, 3, getHeight());
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        item.setOpaque(false);
        item.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, AppTheme.CARD_BORDER),
            BorderFactory.createEmptyBorder(12, 18, 12, 16)
        ));
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 76));
        item.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        item.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { item.setBackground(AppTheme.PRIMARY_LIGHT); item.repaint(); }
            @Override public void mouseExited(MouseEvent e)  { item.repaint(); }
            @Override public void mouseClicked(MouseEvent e) {
                if (!entry.read) { entry.read = true; unreadCount = Math.max(0, unreadCount - 1); updateBadge(); }
                item.repaint();
            }
        });

        // Colored type dot (replaces emoji icon circle)
        JPanel dot = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c = typeColor(entry.type);
                g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 40));
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(c);
                // Draw a type-specific symbol using geometry, not emoji
                g2.setStroke(new BasicStroke(1.8f));
                drawTypeSymbol(g2, entry.type, getWidth(), getHeight());
                g2.dispose();
            }
        };
        dot.setOpaque(false);
        dot.setPreferredSize(new Dimension(38, 38));
        dot.setMinimumSize(new Dimension(38, 38));
        dot.setMaximumSize(new Dimension(38, 38));

        // Text
        JPanel textCol = new JPanel();
        textCol.setLayout(new BoxLayout(textCol, BoxLayout.Y_AXIS));
        textCol.setOpaque(false);

        JLabel titleLbl = new JLabel(entry.title);
        titleLbl.setFont(entry.read ? AppTheme.FONT_BODY : AppTheme.FONT_BODY_BOLD);
        titleLbl.setForeground(AppTheme.TEXT_PRIMARY);

        JLabel msgLbl = new JLabel(entry.message);
        msgLbl.setFont(AppTheme.FONT_SMALL);
        msgLbl.setForeground(AppTheme.TEXT_SECONDARY);

        textCol.add(titleLbl);
        textCol.add(Box.createVerticalStrut(3));
        textCol.add(msgLbl);

        // Right: time + type badge
        JPanel rightCol = new JPanel();
        rightCol.setLayout(new BoxLayout(rightCol, BoxLayout.Y_AXIS));
        rightCol.setOpaque(false);

        JLabel timeLbl = new JLabel(entry.timeAgo);
        timeLbl.setFont(AppTheme.FONT_SMALL);
        timeLbl.setForeground(AppTheme.TEXT_MUTED);
        timeLbl.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JLabel typeLbl = AppTheme.createStatusBadge(entry.type.name());
        typeLbl.setAlignmentX(Component.RIGHT_ALIGNMENT);

        rightCol.add(timeLbl);
        rightCol.add(Box.createVerticalStrut(4));
        rightCol.add(typeLbl);

        item.add(dot,     BorderLayout.WEST);
        item.add(textCol, BorderLayout.CENTER);
        item.add(rightCol,BorderLayout.EAST);

        return item;
    }

    /** Draw a simple geometric symbol representing the notification type. */
    private void drawTypeSymbol(Graphics2D g2, NotifType type, int w, int h) {
        int cx = w / 2, cy = h / 2;
        switch (type) {
            case RENTAL:
                // Simple car silhouette: rectangle body + two circles for wheels
                g2.fillRoundRect(cx - 9, cy - 4, 18, 8, 3, 3);
                g2.fillOval(cx - 7, cy + 3, 5, 5);
                g2.fillOval(cx + 2, cy + 3, 5, 5);
                break;
            case PAYMENT:
                // Dollar sign outline
                g2.drawRoundRect(cx - 8, cy - 7, 16, 14, 3, 3);
                g2.drawLine(cx, cy - 9, cx, cy + 7);
                break;
            case VEHICLE:
                // Wrench-like cross
                g2.drawLine(cx - 7, cy - 7, cx + 7, cy + 7);
                g2.drawLine(cx - 7, cy + 7, cx + 7, cy - 7);
                break;
            case ALERT:
                // Triangle with exclamation
                int[] xs = {cx, cx - 9, cx + 9};
                int[] ys = {cy - 9, cy + 7, cy + 7};
                g2.drawPolygon(xs, ys, 3);
                g2.drawLine(cx, cy - 3, cx, cy + 2);
                g2.fillOval(cx - 1, cy + 4, 3, 3);
                break;
            default:
                // Gear-like circle
                g2.drawOval(cx - 7, cy - 7, 14, 14);
                g2.drawLine(cx, cy - 10, cx, cy + 10);
                g2.drawLine(cx - 10, cy, cx + 10, cy);
                break;
        }
    }

    // ── PUBLIC API ────────────────────────────────────────────────────────────

    /** Push a new notification to the top of the feed. Call from EDT. */
    public void addNotification(String title, String message, NotifType type) {
        NotifEntry entry = new NotifEntry(title, message, type, false, "Just now");
        notifications.add(0, entry);
        unreadCount++;
        refreshFeed();
        updateBadge();
    }

    // ── PRIVATE HELPERS ───────────────────────────────────────────────────────

    private void refreshFeed() {
        feedPanel.removeAll();
        List<NotifEntry> visible = new ArrayList<>();
        for (NotifEntry e : notifications) {
            if (matchesFilter(e)) visible.add(e);
        }

        if (visible.isEmpty()) {
            JPanel empty = new JPanel(new GridBagLayout());
            empty.setBackground(AppTheme.CONTENT_BG);
            empty.setPreferredSize(new Dimension(0, 200));
            JPanel inner = new JPanel();
            inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
            inner.setBackground(AppTheme.CONTENT_BG);
            JLabel msg = new JLabel("No notifications", JLabel.CENTER);
            msg.setFont(AppTheme.FONT_BODY);
            msg.setForeground(AppTheme.TEXT_MUTED);
            msg.setAlignmentX(Component.CENTER_ALIGNMENT);
            inner.add(msg);
            empty.add(inner);
            feedPanel.add(empty);
        } else {
            for (NotifEntry entry : visible) {
                feedPanel.add(buildNotifItem(entry));
            }
        }
        feedPanel.revalidate();
        feedPanel.repaint();
    }

    private boolean matchesFilter(NotifEntry e) {
        switch (currentFilter) {
            case "Rentals":  return e.type == NotifType.RENTAL;
            case "Payments": return e.type == NotifType.PAYMENT;
            case "Alerts":   return e.type == NotifType.ALERT;
            case "System":   return e.type == NotifType.SYSTEM || e.type == NotifType.VEHICLE;
            default:         return true;
        }
    }

    private void markAllAsRead() {
        notifications.forEach(n -> n.read = true);
        unreadCount = 0;
        updateBadge();
        refreshFeed();
    }

    private void clearAllNotifications() {
        notifications.clear();
        unreadCount = 0;
        updateBadge();
        refreshFeed();
    }

    private void updateBadge() {
        countBadge.setText(String.valueOf(unreadCount));
        countBadge.setVisible(unreadCount > 0);
    }

    private JLabel buildFilterChip(String text, boolean active) {
        JLabel chip = new JLabel(text, JLabel.CENTER) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(active
                    ? AppTheme.PRIMARY
                    : new Color(218, 190, 155)); // warm caramel inactive
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        chip.setFont(new Font("Georgia", Font.BOLD, 11));
        chip.setForeground(active ? new Color(255, 240, 215) : AppTheme.TEXT_SECONDARY);
        chip.setOpaque(false);
        chip.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
        chip.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        chip.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                currentFilter = text;
                // Visually toggle chips by repainting — a full rebuild is simplest
                // since chips are rebuilt on each refreshFeed parent rebuild.
                // We re-trigger the filter immediately.
                refreshFeed();
            }
        });
        return chip;
    }

    private Color typeColor(NotifType type) {
        switch (type) {
            case RENTAL:  return AppTheme.PRIMARY;   // amber-brown
            case PAYMENT: return AppTheme.SUCCESS;   // muted green
            case VEHICLE: return AppTheme.INFO;      // dusty blue
            case ALERT:   return AppTheme.DANGER;    // terracotta
            default:      return AppTheme.TEXT_MUTED;
        }
    }

    // ── DATA MODEL ────────────────────────────────────────────────────────────

    public static class NotifEntry {
        public String    title, message, timeAgo;
        public NotifType type;
        public boolean   read;

        public NotifEntry(String title, String message, NotifType type,
                          boolean read, String timeAgo) {
            this.title   = title;
            this.message = message;
            this.type    = type;
            this.read    = read;
            this.timeAgo = timeAgo;
        }
    }

    /** Convenience: show as a floating non-modal dialog. */
    public static void showAsDialog(java.awt.Window parent, int ownerId) {
        JDialog dialog = new JDialog(
            parent instanceof JFrame ? (JFrame) parent : null,
            "Notifications", false);
        dialog.setSize(480, 620);
        dialog.setLocationRelativeTo(parent);
        NotificationPanel np = new NotificationPanel();
        np.setOwnerId(ownerId);
        dialog.add(np);
        dialog.setVisible(true);
    }
}