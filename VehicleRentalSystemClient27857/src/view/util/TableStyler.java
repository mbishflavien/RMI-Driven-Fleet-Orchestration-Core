package view.util;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;

/**
 * TableStyler - Applies premium enterprise styling to any JTable.
 */
public class TableStyler {

    /**
     * Apply the full premium style to a JTable and return it wrapped in a scroll pane card.
     */
    public static void applyStyle(JTable table) {
        table.setFont(AppTheme.FONT_TABLE_CELL);
        table.setForeground(AppTheme.TEXT_PRIMARY);
        table.setBackground(AppTheme.TABLE_ROW_EVEN);
        table.setSelectionBackground(AppTheme.TABLE_SELECTION);
        table.setSelectionForeground(AppTheme.TEXT_PRIMARY);
        table.setGridColor(AppTheme.TABLE_GRID);
        table.setRowHeight(40);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setFillsViewportHeight(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Alternating row renderer
        table.setDefaultRenderer(Object.class, new AlternatingRowRenderer());

        // Header style
        styleHeader(table.getTableHeader());
    }

    private static void styleHeader(JTableHeader header) {
        header.setFont(AppTheme.FONT_TABLE_HEADER);
        header.setBackground(AppTheme.TABLE_HEADER_BG);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 44));
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = new JLabel(value == null ? "" : value.toString()) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setColor(AppTheme.TABLE_HEADER_BG);
                        g2.fillRect(0, 0, getWidth(), getHeight());
                        g2.dispose();
                        super.paintComponent(g);
                        // bottom accent line
                        Graphics2D g3 = (Graphics2D) g.create();
                        g3.setColor(AppTheme.PRIMARY);
                        g3.fillRect(0, getHeight() - 3, getWidth(), 3);
                        g3.dispose();
                    }
                };
                lbl.setFont(AppTheme.FONT_TABLE_HEADER);
                lbl.setForeground(Color.WHITE);
                lbl.setHorizontalAlignment(JLabel.CENTER);
                lbl.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
                lbl.setOpaque(false);
                return lbl;
            }
        });
        header.setReorderingAllowed(false);
        header.setResizingAllowed(true);
    }

    /**
     * Build a search toolbar for a table (search field + refresh button + optional export button).
     */
    public static JPanel buildTableToolbar(final JTable table,
                                            final javax.swing.table.DefaultTableModel model,
                                            String title,
                                            boolean showExport) {
        JPanel toolbar = new JPanel(new BorderLayout(12, 0));
        toolbar.setBackground(AppTheme.CARD_BG);
        toolbar.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        // Title
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(AppTheme.FONT_SUBTITLE);
        titleLabel.setForeground(AppTheme.TEXT_PRIMARY);

        // Search
        JTextField searchField = AppTheme.createTextField("  Search...");
        searchField.setPreferredSize(new Dimension(220, 36));
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            void filter() {
                String text = searchField.getText().toLowerCase().trim();
                if (table.getRowSorter() instanceof TableRowSorter) {
                    TableRowSorter sorter = (TableRowSorter) table.getRowSorter();
                    if (text.isEmpty()) {
                        sorter.setRowFilter(null);
                    } else {
                        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                    }
                }
            }
        });

        // Enable sorting
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightPanel.setBackground(AppTheme.CARD_BG);
        rightPanel.add(searchField);

        JButton refreshBtn = AppTheme.createSecondaryButton("↺  Refresh");
        rightPanel.add(refreshBtn);

        if (showExport) {
            JButton exportBtn = AppTheme.createSecondaryButton("⬇  Export CSV");
            exportBtn.addActionListener(e -> {
                JFileChooser fc = new JFileChooser();
                fc.setDialogTitle("Save CSV");
                fc.setSelectedFile(new java.io.File(title.replaceAll("\\s+", "_") + "_"
                    + new java.text.SimpleDateFormat("yyyyMMdd").format(new java.util.Date()) + ".csv"));
                if (fc.showSaveDialog(table) != JFileChooser.APPROVE_OPTION) return;
                java.io.File file = fc.getSelectedFile();
                if (!file.getName().toLowerCase().endsWith(".csv"))
                    file = new java.io.File(file.getAbsolutePath() + ".csv");
                try (java.io.PrintWriter pw = new java.io.PrintWriter(new java.io.FileWriter(file))) {
                    StringBuilder hdr = new StringBuilder();
                    for (int c = 0; c < model.getColumnCount(); c++) {
                        if (c > 0) hdr.append(",");
                        hdr.append(csvEsc(model.getColumnName(c)));
                    }
                    pw.println(hdr);
                    for (int r = 0; r < table.getRowCount(); r++) {
                        StringBuilder row = new StringBuilder();
                        for (int c = 0; c < model.getColumnCount(); c++) {
                            if (c > 0) row.append(",");
                            Object val = table.getValueAt(r, c);
                            row.append(csvEsc(val == null ? "" : val.toString()));
                        }
                        pw.println(row);
                    }
                    pw.flush();
                    JOptionPane.showMessageDialog(table,
                        "CSV saved to:\n" + file.getAbsolutePath(),
                        "Export Successful", JOptionPane.INFORMATION_MESSAGE);
                } catch (java.io.IOException ex) {
                    JOptionPane.showMessageDialog(table,
                        "Failed to save CSV:\n" + ex.getMessage(),
                        "Export Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            rightPanel.add(exportBtn);
        }

        toolbar.add(titleLabel, BorderLayout.WEST);
        toolbar.add(rightPanel, BorderLayout.EAST);
        return toolbar;
    }

    // ─── RENDERERS ────────────────────────────────────────────────────────────

    public static class AlternatingRowRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setFont(AppTheme.FONT_TABLE_CELL);
            setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
            setHorizontalAlignment(JLabel.CENTER);
            if (isSelected) {
                setBackground(AppTheme.TABLE_SELECTION);
                setForeground(AppTheme.TEXT_PRIMARY);
            } else {
                setBackground(row % 2 == 0 ? AppTheme.TABLE_ROW_EVEN : AppTheme.TABLE_ROW_ODD);
                setForeground(AppTheme.TEXT_PRIMARY);
            }
            return this;
        }
    }

    /** Renderer that draws a colored status badge */
    public static class StatusBadgeRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel badge = AppTheme.createStatusBadge(value == null ? "" : value.toString());
            JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 6));
            wrapper.setBackground(isSelected ? AppTheme.TABLE_SELECTION :
                (row % 2 == 0 ? AppTheme.TABLE_ROW_EVEN : AppTheme.TABLE_ROW_ODD));
            wrapper.add(badge);
            return wrapper;
        }
    }

    // ── CSV escape helper ─────────────────────────────────────────────────────
    private static String csvEsc(String val) {
        if (val == null) return "";
        if (val.contains(",") || val.contains("\"") || val.contains("\n")) {
            return "\"" + val.replace("\"", "\"\"") + "\"";
        }
        return val;
    }
}
