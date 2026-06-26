package view.util;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.BasicScrollBarUI;

/**
 * AppTheme - Central theme engine for VehicleRentalSystem27857
 * Warm Brown & Leather — a rich, earthy enterprise design system.
 */
public class AppTheme {

    // ─── COLOR PALETTE ──────────────────────────────────────────────────────
    // Sidebar: deep espresso brown
    public static final Color SIDEBAR_BG           = new Color(42, 24, 12);      // deep espresso
    public static final Color SIDEBAR_HOVER        = new Color(65, 38, 18);      // medium roast
    public static final Color SIDEBAR_ACTIVE       = new Color(160, 90, 30);     // warm amber-brown
    public static final Color SIDEBAR_TEXT         = new Color(196, 165, 135);   // warm tan
    public static final Color SIDEBAR_TEXT_ACTIVE  = new Color(255, 240, 215);   // warm cream
    public static final Color SIDEBAR_LOGO_BG      = new Color(160, 90, 30);     // amber-brown

    // Top bar: light warm parchment
    public static final Color TOPBAR_BG            = new Color(253, 248, 240);   // warm parchment
    public static final Color TOPBAR_BORDER        = new Color(220, 195, 165);   // tan border

    // Content area
    public static final Color CONTENT_BG           = new Color(250, 244, 234);   // warm cream
    public static final Color CARD_BG              = new Color(255, 251, 245);   // lightest cream
    public static final Color CARD_BORDER          = new Color(218, 190, 155);   // light caramel border

    // Primary action: warm amber-brown
    public static final Color PRIMARY              = new Color(160, 90, 30);     // amber-brown
    public static final Color PRIMARY_HOVER        = new Color(130, 70, 20);     // darker amber
    public static final Color PRIMARY_LIGHT        = new Color(255, 235, 200);   // light peach

    // Semantic colors — tinted warm to match palette
    public static final Color SUCCESS              = new Color(80, 140, 80);     // muted forest green
    public static final Color SUCCESS_LIGHT        = new Color(220, 245, 220);
    public static final Color WARNING              = new Color(200, 130, 20);    // golden amber
    public static final Color WARNING_LIGHT        = new Color(255, 240, 195);
    public static final Color DANGER               = new Color(185, 60, 45);     // terracotta red
    public static final Color DANGER_LIGHT         = new Color(255, 225, 215);
    public static final Color INFO                 = new Color(100, 130, 160);   // dusty blue
    public static final Color INFO_LIGHT           = new Color(215, 230, 245);
    public static final Color PURPLE               = new Color(130, 90, 150);    // dusty mauve
    public static final Color PURPLE_LIGHT         = new Color(235, 220, 245);

    // Text
    public static final Color TEXT_PRIMARY         = new Color(40, 22, 8);       // deep espresso text
    public static final Color TEXT_SECONDARY       = new Color(110, 75, 45);     // warm brown text
    public static final Color TEXT_MUTED           = new Color(170, 135, 100);   // light tan text

    // Table
    public static final Color TABLE_HEADER_BG     = new Color(55, 30, 12);      // dark roast
    public static final Color TABLE_ROW_EVEN       = new Color(255, 251, 245);   // cream
    public static final Color TABLE_ROW_ODD        = new Color(248, 240, 225);   // warm off-white
    public static final Color TABLE_SELECTION      = new Color(255, 225, 180);   // light amber selection
    public static final Color TABLE_GRID           = new Color(218, 190, 155);   // caramel grid

    // Input fields
    public static final Color INPUT_BG             = new Color(255, 252, 246);   // near-white warm
    public static final Color INPUT_BORDER         = new Color(200, 165, 125);   // caramel border
    public static final Color INPUT_FOCUS          = new Color(160, 90, 30);     // amber focus ring
    public static final Color INPUT_TEXT           = new Color(40, 22, 8);       // espresso text

    // ─── FONTS ──────────────────────────────────────────────────────────────
    // Using Palatino Linotype for an elegant, warm serif feel; fallback chain included
    private static final String DISPLAY_FONT = "Palatino Linotype";
    private static final String BODY_FONT    = "Georgia";

    public static final Font  FONT_TITLE        = new Font(DISPLAY_FONT, Font.BOLD, 22);
    public static final Font  FONT_SUBTITLE     = new Font(DISPLAY_FONT, Font.BOLD, 16);
    public static final Font  FONT_BODY         = new Font(BODY_FONT, Font.PLAIN, 13);
    public static final Font  FONT_BODY_BOLD    = new Font(BODY_FONT, Font.BOLD, 13);
    public static final Font  FONT_SMALL        = new Font(BODY_FONT, Font.PLAIN, 11);
    public static final Font  FONT_LARGE        = new Font(DISPLAY_FONT, Font.BOLD, 28);
    public static final Font  FONT_XLARGE       = new Font(DISPLAY_FONT, Font.BOLD, 36);
    public static final Font  FONT_SIDEBAR_ITEM = new Font(BODY_FONT, Font.PLAIN, 13);
    public static final Font  FONT_SIDEBAR_LOGO = new Font(DISPLAY_FONT, Font.BOLD, 14);
    public static final Font  FONT_TABLE_HEADER = new Font(DISPLAY_FONT, Font.BOLD, 12);
    public static final Font  FONT_TABLE_CELL   = new Font(BODY_FONT, Font.PLAIN, 12);

    // ─── APPLY GLOBAL DEFAULTS ──────────────────────────────────────────────
    public static void applyGlobalDefaults() {
        UIManager.put("Panel.background",           CONTENT_BG);
        UIManager.put("OptionPane.background",      CARD_BG);
        UIManager.put("OptionPane.messageFont",     FONT_BODY);
        UIManager.put("OptionPane.buttonFont",      FONT_BODY_BOLD);
        UIManager.put("Button.font",                FONT_BODY_BOLD);
        UIManager.put("Label.font",                 FONT_BODY);
        UIManager.put("TextField.font",             FONT_BODY);
        UIManager.put("PasswordField.font",         FONT_BODY);
        UIManager.put("ComboBox.font",              FONT_BODY);
        UIManager.put("Table.font",                 FONT_TABLE_CELL);
        UIManager.put("TableHeader.font",           FONT_TABLE_HEADER);
        UIManager.put("ScrollPane.border",          BorderFactory.createEmptyBorder());
    }

    // ─── BUTTON FACTORY ─────────────────────────────────────────────────────
    public static JButton createPrimaryButton(String text) {
        return createStyledButton(text, PRIMARY, PRIMARY_HOVER, new Color(255, 240, 215));
    }

    public static JButton createSuccessButton(String text) {
        return createStyledButton(text, SUCCESS, new Color(55, 105, 55), Color.WHITE);
    }

    public static JButton createDangerButton(String text) {
        return createStyledButton(text, DANGER, new Color(145, 40, 30), Color.WHITE);
    }

    public static JButton createWarningButton(String text) {
        return createStyledButton(text, WARNING, new Color(160, 100, 10), Color.WHITE);
    }

    public static JButton createSecondaryButton(String text) {
        return createStyledButton(text, new Color(235, 215, 190), new Color(210, 185, 155), TEXT_PRIMARY);
    }

    /** Convenience overload accepting an ActionListener */
    public static JButton createStyledButton(String text, Color color, java.awt.event.ActionListener action) {
        JButton btn = createStyledButton(text, color, color.darker(), new Color(255, 240, 215));
        btn.addActionListener(action);
        return btn;
    }

    private static JButton createStyledButton(String text, final Color normal, final Color hover, Color fg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Subtle warm gradient on button
                GradientPaint gp = new GradientPaint(
                    0, 0, getModel().isRollover() ? hover : normal,
                    0, getHeight(), getModel().isRollover() ? hover.darker() : normal.darker()
                );
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(FONT_BODY_BOLD);
        btn.setForeground(fg);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(btn.getPreferredSize().width + 24, 36));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        return btn;
    }

    // ─── ICON BUTTON (small square) ─────────────────────────────────────────
    public static JButton createIconButton(String text, Color color) {
        JButton btn = createStyledButton(text, color, color.darker(), new Color(255, 240, 215));
        btn.setPreferredSize(new Dimension(32, 32));
        btn.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        return btn;
    }

    // ─── TEXT FIELD FACTORY ─────────────────────────────────────────────────
    public static JTextField createTextField(String placeholder) {
        JTextField field = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(INPUT_BG);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        styleInputField(field);
        field.putClientProperty("placeholder", placeholder);
        return field;
    }

    public static JPasswordField createPasswordField() {
        JPasswordField field = new JPasswordField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(INPUT_BG);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        styleInputField(field);
        return field;
    }

    private static void styleInputField(JComponent field) {
        field.setFont(FONT_BODY);
        field.setForeground(INPUT_TEXT);
        field.setBackground(INPUT_BG);
        field.setOpaque(false);
        field.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(INPUT_BORDER, 8, 1),
            BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        field.setPreferredSize(new Dimension(field.getPreferredSize().width, 38));
    }

    public static JComboBox createComboBox(String[] items) {
        JComboBox combo = new JComboBox(items);
        combo.setFont(FONT_BODY);
        combo.setBackground(INPUT_BG);
        combo.setForeground(INPUT_TEXT);
        combo.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(INPUT_BORDER, 8, 1),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        combo.setPreferredSize(new Dimension(combo.getPreferredSize().width, 38));
        return combo;
    }

    // ─── CARD PANEL ─────────────────────────────────────────────────────────
    public static JPanel createCard() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Warm shadow
                for (int i = 4; i > 0; i--) {
                    g2.setColor(new Color(100, 50, 10, 6 * i));
                    g2.fillRoundRect(i, i, getWidth() - i * 2, getHeight() - i * 2, 12, 12);
                }
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                // Subtle top highlight line
                g2.setColor(new Color(255, 220, 170, 80));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        return card;
    }

    // ─── SECTION TITLE LABEL ────────────────────────────────────────────────
    public static JLabel createSectionTitle(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_SUBTITLE);
        lbl.setForeground(TEXT_PRIMARY);
        return lbl;
    }

    public static JLabel createMutedLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_SMALL);
        lbl.setForeground(TEXT_MUTED);
        return lbl;
    }

    // ─── STYLED SCROLL PANE ─────────────────────────────────────────────────
    public static JScrollPane createScrollPane(Component view) {
        JScrollPane sp = new JScrollPane(view);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.setBackground(CONTENT_BG);
        sp.getViewport().setBackground(CONTENT_BG);
        sp.getVerticalScrollBar().setUI(new FlatScrollBarUI());
        sp.getHorizontalScrollBar().setUI(new FlatScrollBarUI());
        sp.getVerticalScrollBar().setUnitIncrement(12);
        return sp;
    }

    // ─── SEPARATOR LINE ─────────────────────────────────────────────────────
    public static JSeparator createSeparator() {
        JSeparator sep = new JSeparator();
        sep.setForeground(CARD_BORDER);
        sep.setBackground(CARD_BORDER);
        return sep;
    }

    // ─── STATUS BADGE ────────────────────────────────────────────────────────
    public static JLabel createStatusBadge(String text) {
        Color bg, fg;
        String upper = text == null ? "" : text.toUpperCase();
        if (upper.contains("AVAILABLE") || upper.contains("PAID") || upper.contains("ACTIVE") || upper.contains("COMPLETED")) {
            bg = SUCCESS_LIGHT; fg = SUCCESS;
        } else if (upper.contains("RENTED") || upper.contains("PENDING") || upper.contains("ONGOING")) {
            bg = WARNING_LIGHT; fg = WARNING;
        } else if (upper.contains("LATE") || upper.contains("OVERDUE") || upper.contains("INACTIVE") || upper.contains("DELETED")) {
            bg = DANGER_LIGHT; fg = DANGER;
        } else {
            bg = INFO_LIGHT; fg = INFO;
        }
        JLabel badge = new JLabel(text, JLabel.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        badge.setFont(new Font(BODY_FONT, Font.BOLD, 10));
        badge.setForeground(fg);
        badge.setOpaque(false);
        badge.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
        return badge;
    }

    // ─── INNER CLASSES ───────────────────────────────────────────────────────

    /** Rounded border utility */
    public static class RoundedBorder extends AbstractBorder {
        private final Color color;
        private final int radius;
        private final int thickness;

        public RoundedBorder(Color color, int radius, int thickness) {
            this.color = color;
            this.radius = radius;
            this.thickness = thickness;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(thickness));
            g2.drawRoundRect(x, y, w - 1, h - 1, radius, radius);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(thickness, thickness, thickness, thickness);
        }
    }

    /** Flat warm scrollbar */
    public static class FlatScrollBarUI extends BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            thumbColor = new Color(185, 145, 100);   // warm caramel thumb
            trackColor = new Color(240, 225, 200);   // light parchment track
        }

        @Override
        protected JButton createDecreaseButton(int orientation) { return createZeroButton(); }
        @Override
        protected JButton createIncreaseButton(int orientation) { return createZeroButton(); }

        private JButton createZeroButton() {
            JButton btn = new JButton();
            btn.setPreferredSize(new Dimension(0, 0));
            btn.setMinimumSize(new Dimension(0, 0));
            btn.setMaximumSize(new Dimension(0, 0));
            return btn;
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            if (thumbBounds.isEmpty()) return;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(thumbColor);
            g2.fillRoundRect(thumbBounds.x + 2, thumbBounds.y + 2,
                             thumbBounds.width - 4, thumbBounds.height - 4, 6, 6);
            g2.dispose();
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
            g.setColor(trackColor);
            g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
        }
    }
}