package view.util;

import java.awt.*;
import javax.swing.*;

/**
 * KpiCard - A modern analytics KPI card with icon, value, label and trend.
 */
public class KpiCard extends JPanel {

    private JLabel valueLabel;
    private JLabel titleLabel;
    private JLabel trendLabel;
    private JLabel iconLabel;
    private Color accentColor;

    public KpiCard(String icon, String title, String value, String trend, Color accent) {
        this.accentColor = accent;
        setOpaque(false);
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(200, 120));

        // Card content
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // shadow layers
                for (int i = 5; i > 0; i--) {
                    g2.setColor(new Color(0, 0, 0, 4 * i));
                    g2.fillRoundRect(i, i, getWidth() - i * 2, getHeight() - i * 2, 14, 14);
                }
                g2.setColor(AppTheme.CARD_BG);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
                // left accent strip
                g2.setColor(accentColor);
                g2.fillRoundRect(0, 0, 5, getHeight(), 4, 4);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 14));

        // Left: text column
        JPanel textCol = new JPanel();
        textCol.setLayout(new BoxLayout(textCol, BoxLayout.Y_AXIS));
        textCol.setOpaque(false);

        titleLabel = new JLabel(title);
        titleLabel.setFont(AppTheme.FONT_SMALL);
        titleLabel.setForeground(AppTheme.TEXT_SECONDARY);

        valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        valueLabel.setForeground(AppTheme.TEXT_PRIMARY);

        trendLabel = new JLabel(trend);
        trendLabel.setFont(AppTheme.FONT_SMALL);
        boolean positive = trend != null && (trend.startsWith("+") || trend.startsWith("▲"));
        trendLabel.setForeground(positive ? AppTheme.SUCCESS : AppTheme.DANGER);

        textCol.add(titleLabel);
        textCol.add(Box.createVerticalStrut(6));
        textCol.add(valueLabel);
        textCol.add(Box.createVerticalStrut(4));
        textCol.add(trendLabel);

        // Right: icon circle
        iconLabel = new JLabel(icon, JLabel.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color iconBg = new Color(accentColor.getRed(), accentColor.getGreen(),
                                         accentColor.getBlue(), 30);
                g2.setColor(iconBg);
                int sz = Math.min(getWidth(), getHeight());
                g2.fillOval((getWidth() - sz) / 2, (getHeight() - sz) / 2, sz, sz);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        iconLabel.setForeground(accentColor);
        iconLabel.setPreferredSize(new Dimension(52, 52));

        JPanel iconWrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        iconWrap.setOpaque(false);
        iconWrap.setPreferredSize(new Dimension(60, 60));
        iconWrap.add(iconLabel);

        card.add(textCol, BorderLayout.CENTER);
        card.add(iconWrap, BorderLayout.EAST);

        add(card, BorderLayout.CENTER);
    }

    public void setValue(String value) { valueLabel.setText(value); }
    public void setTrend(String trend) {
        trendLabel.setText(trend);
        boolean positive = trend != null && (trend.startsWith("+") || trend.startsWith("▲"));
        trendLabel.setForeground(positive ? AppTheme.SUCCESS : AppTheme.DANGER);
    }

    /** Build a row of KPI cards in a grid panel */
    public static JPanel buildKpiRow(KpiCard... cards) {
        JPanel row = new JPanel(new GridLayout(1, cards.length, 14, 0));
        row.setBackground(AppTheme.CONTENT_BG);
        row.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        for (KpiCard c : cards) row.add(c);
        return row;
    }
}
