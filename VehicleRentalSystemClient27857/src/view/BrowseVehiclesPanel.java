package view;

import view.util.*;
import controller.ClientRegistry;
import model.*;
import service.*;

import controller.NotificationBus;
import java.awt.*;
import java.awt.event.*;
import java.rmi.registry.Registry;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import javax.swing.*;

/**
 * BrowseVehiclesPanel - Modern vehicle card grid browser for the customer portal.
 * Displays vehicle cards with brand, model, category, daily rate, availability.
 */
public class BrowseVehiclesPanel extends JPanel {

    private final User currentUser;
    private JPanel cardsGrid;
    private JTextField searchField;
    private JComboBox<String> filterCategory, filterAvailability;
    private JLabel resultCountLabel;
    private List<Vehicle> allVehicles = new ArrayList<>();

    public BrowseVehiclesPanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout());
        setBackground(AppTheme.CONTENT_BG);
        buildUI();
        loadVehicles();
    }

    private void buildUI() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(AppTheme.CONTENT_BG);
        main.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        // ── PAGE HEADER ────────────────────────────────────────────────────────
        JPanel headerRow = new JPanel(new BorderLayout());
        headerRow.setBackground(AppTheme.CONTENT_BG);
        headerRow.setBorder(BorderFactory.createEmptyBorder(0, 0, 18, 0));

        JPanel titleCol = new JPanel();
        titleCol.setLayout(new BoxLayout(titleCol, BoxLayout.Y_AXIS));
        titleCol.setBackground(AppTheme.CONTENT_BG);
        JLabel title = new JLabel("Browse Vehicles");
        title.setFont(AppTheme.FONT_TITLE);
        title.setForeground(AppTheme.TEXT_PRIMARY);
        JLabel sub = new JLabel("Choose from our premium vehicle fleet");
        sub.setFont(AppTheme.FONT_BODY);
        sub.setForeground(AppTheme.TEXT_SECONDARY);
        titleCol.add(title);
        titleCol.add(Box.createVerticalStrut(2));
        titleCol.add(sub);

        resultCountLabel = new JLabel("Loading...");
        resultCountLabel.setFont(AppTheme.FONT_SMALL);
        resultCountLabel.setForeground(AppTheme.TEXT_MUTED);

        headerRow.add(titleCol, BorderLayout.WEST);
        headerRow.add(resultCountLabel, BorderLayout.EAST);

        // ── SEARCH + FILTER BAR ────────────────────────────────────────────────
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterBar.setBackground(AppTheme.CONTENT_BG);
        filterBar.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));

        searchField = AppTheme.createTextField("Search brand, model...");
        searchField.setPreferredSize(new Dimension(240, 38));
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { applyFilters(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { applyFilters(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { applyFilters(); }
        });

        filterCategory = AppTheme.createComboBox(
            new String[]{"All Categories", "Sedan", "SUV", "Truck", "Van", "Motorcycle", "Electric"});
        filterCategory.setPreferredSize(new Dimension(150, 38));
        filterCategory.addActionListener(e -> applyFilters());

        filterAvailability = AppTheme.createComboBox(
            new String[]{"All Status", "AVAILABLE", "RENTED", "MAINTENANCE"});
        filterAvailability.setPreferredSize(new Dimension(140, 38));
        filterAvailability.addActionListener(e -> applyFilters());

        JButton refreshBtn = AppTheme.createSecondaryButton("↺  Refresh");
        refreshBtn.addActionListener(e -> loadVehicles());

        filterBar.add(new JLabel("🔍") {{
            setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
            setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 2));
        }});
        filterBar.add(searchField);
        filterBar.add(filterCategory);
        filterBar.add(filterAvailability);
        filterBar.add(refreshBtn);

        // ── CARD GRID (scrollable) ─────────────────────────────────────────────
        cardsGrid = new JPanel(new WrapLayout(FlowLayout.LEFT, 14, 14));
        cardsGrid.setBackground(AppTheme.CONTENT_BG);

        JScrollPane scroll = AppTheme.createScrollPane(cardsGrid);
        scroll.getViewport().setBackground(AppTheme.CONTENT_BG);

        JPanel topSection = new JPanel(new BorderLayout());
        topSection.setBackground(AppTheme.CONTENT_BG);
        topSection.add(headerRow,  BorderLayout.NORTH);
        topSection.add(filterBar,  BorderLayout.CENTER);

        main.add(topSection, BorderLayout.NORTH);
        main.add(scroll,     BorderLayout.CENTER);
        add(main, BorderLayout.CENTER);
    }

    private void loadVehicles() {
        cardsGrid.removeAll();
        cardsGrid.add(buildLoadingPlaceholder());
        cardsGrid.revalidate();

        SwingWorker<List<Vehicle>, Void> w = new SwingWorker<List<Vehicle>, Void>() {
            @Override
            protected List<Vehicle> doInBackground() throws Exception {
                Registry r = ClientRegistry.getRegistry();
                VehicleService vs = (VehicleService) r.lookup("vehicle");
                return vs.displayAllVehicles();
            }
            @Override
            protected void done() {
                try {
                    allVehicles = get();
                    applyFilters();
                } catch (Exception ex) {
                    cardsGrid.removeAll();
                    cardsGrid.add(buildEmptyState("Connection Error",
                        "Could not load vehicles. Check RMI server connection.", "🔌"));
                    cardsGrid.revalidate();
                    cardsGrid.repaint();
                }
            }
        };
        w.execute();
    }

    private void applyFilters() {
        String search   = searchField.getText().toLowerCase().trim();
        String category = (String) filterCategory.getSelectedItem();
        String status   = (String) filterAvailability.getSelectedItem();

        List<Vehicle> filtered = new ArrayList<>();
        for (Vehicle v : allVehicles) {
            boolean matchSearch   = search.isEmpty() ||
                v.getBrand().toLowerCase().contains(search) ||
                v.getModel().toLowerCase().contains(search) ||
                v.getPlateNumber().toLowerCase().contains(search);
            boolean matchCategory = "All Categories".equals(category) ||
                (v.getCategory() != null && v.getCategory().equalsIgnoreCase(category));
            boolean matchStatus   = "All Status".equals(status) ||
                (v.getAvailabilityStatus() != null && v.getAvailabilityStatus().equalsIgnoreCase(status));
            if (matchSearch && matchCategory && matchStatus) filtered.add(v);
        }

        cardsGrid.removeAll();
        if (filtered.isEmpty()) {
            cardsGrid.add(buildEmptyState("No Vehicles Found",
                "Try adjusting your search or filters.", "🚗"));
        } else {
            for (Vehicle v : filtered) cardsGrid.add(buildVehicleCard(v));
        }
        resultCountLabel.setText(filtered.size() + " vehicle" + (filtered.size() != 1 ? "s" : "") + " found");
        cardsGrid.revalidate();
        cardsGrid.repaint();
    }

    // ── VEHICLE CARD ──────────────────────────────────────────────────────────────
    private JPanel buildVehicleCard(Vehicle v) {
        boolean available = "AVAILABLE".equalsIgnoreCase(v.getAvailabilityStatus());

        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // shadow
                for (int i = 5; i > 0; i--) {
                    g2.setColor(new Color(0, 0, 0, 4 * i));
                    g2.fillRoundRect(i, i, getWidth() - i * 2, getHeight() - i * 2, 14, 14);
                }
                g2.setColor(AppTheme.CARD_BG);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(220, 280));
        card.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        // Vehicle image placeholder
        JPanel imagePlaceholder = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = available ? AppTheme.PRIMARY_LIGHT : new Color(241, 245, 249);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        imagePlaceholder.setOpaque(false);
        imagePlaceholder.setPreferredSize(new Dimension(188, 100));
        imagePlaceholder.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        imagePlaceholder.setAlignmentX(Component.LEFT_ALIGNMENT);

        String emoji = vehicleEmoji(v.getCategory());
        JLabel carIcon = new JLabel(emoji, JLabel.CENTER);
        carIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 42));
        imagePlaceholder.add(carIcon);

        card.add(imagePlaceholder);
        card.add(Box.createVerticalStrut(12));

        // Category chip
        JLabel categoryChip = buildChip(v.getCategory() != null ? v.getCategory() : "Vehicle");
        categoryChip.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(categoryChip);
        card.add(Box.createVerticalStrut(6));

        // Brand + Model
        JLabel brandModel = new JLabel(v.getBrand() + " " + v.getModel());
        brandModel.setFont(AppTheme.FONT_BODY_BOLD);
        brandModel.setForeground(AppTheme.TEXT_PRIMARY);
        brandModel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(brandModel);

        // Year + plate
        JLabel meta = new JLabel(v.getManufactureYear() + "  ·  " + v.getPlateNumber());
        meta.setFont(AppTheme.FONT_SMALL);
        meta.setForeground(AppTheme.TEXT_MUTED);
        meta.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(meta);
        card.add(Box.createVerticalStrut(10));

        // Price row
        JPanel priceRow = new JPanel(new BorderLayout());
        priceRow.setBackground(AppTheme.CARD_BG);
        priceRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        priceRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));

        JLabel price = new JLabel(String.format("$%.0f", v.getDailyRate()));
        price.setFont(new Font("Segoe UI", Font.BOLD, 20));
        price.setForeground(AppTheme.PRIMARY);
        JLabel perDay = new JLabel("/day");
        perDay.setFont(AppTheme.FONT_SMALL);
        perDay.setForeground(AppTheme.TEXT_MUTED);

        JPanel priceLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        priceLeft.setBackground(AppTheme.CARD_BG);
        priceLeft.add(price); priceLeft.add(perDay);

        JLabel statusBadge = AppTheme.createStatusBadge(v.getAvailabilityStatus());
        priceRow.add(priceLeft, BorderLayout.WEST);
        priceRow.add(statusBadge, BorderLayout.EAST);
        card.add(priceRow);
        card.add(Box.createVerticalStrut(12));

        // Rent button
        JButton rentBtn = available ?
            AppTheme.createPrimaryButton("🚗  Rent Now") :
            AppTheme.createSecondaryButton("Unavailable");
        rentBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        rentBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        rentBtn.setEnabled(available);
        if (available) {
            rentBtn.addActionListener(e -> showRentDialog(v));
        }
        card.add(rentBtn);

        return card;
    }

    private void showRentDialog(Vehicle v) {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this),
            "Rent Vehicle", true);
        dialog.setSize(440, 420);
        dialog.setLocationRelativeTo(this);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(AppTheme.CARD_BG);
        content.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        JLabel title = new JLabel("Create Rental");
        title.setFont(AppTheme.FONT_SUBTITLE);
        title.setForeground(AppTheme.TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel vInfo = new JLabel(v.getBrand() + " " + v.getModel() + "  ·  " +
            v.getPlateNumber() + "  ·  $" + String.format("%.0f", v.getDailyRate()) + "/day");
        vInfo.setFont(AppTheme.FONT_SMALL);
        vInfo.setForeground(AppTheme.TEXT_SECONDARY);
        vInfo.setAlignmentX(Component.LEFT_ALIGNMENT);

        content.add(title);
        content.add(Box.createVerticalStrut(4));
        content.add(vInfo);
        content.add(Box.createVerticalStrut(18));

        JTextField[] startField = {null};
        JTextField[] endField   = {null};
        JLabel[] totalLabel     = {null};

        startField[0] = dialogField(content, "Start Date (yyyy-MM-dd)", "2024-01-01");
        endField[0]   = dialogField(content, "Return Date (yyyy-MM-dd)", "2024-01-08");

        content.add(Box.createVerticalStrut(8));
        totalLabel[0] = new JLabel("Estimated Total: —");
        totalLabel[0].setFont(AppTheme.FONT_BODY_BOLD);
        totalLabel[0].setForeground(AppTheme.PRIMARY);
        totalLabel[0].setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(totalLabel[0]);
        content.add(Box.createVerticalStrut(4));

        JLabel noteLabel = new JLabel("Deposit (110%) will be collected at rental creation.");
        noteLabel.setFont(AppTheme.FONT_SMALL);
        noteLabel.setForeground(AppTheme.TEXT_MUTED);
        noteLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(noteLabel);
        content.add(Box.createVerticalStrut(20));

        // Auto-calculate
        javax.swing.event.DocumentListener calc = new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { recalc(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { recalc(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { recalc(); }
            void recalc() {
                try {
                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
                    java.util.Date s = sdf.parse(startField[0].getText().trim());
                    java.util.Date e2 = sdf.parse(endField[0].getText().trim());
                    long days = (e2.getTime() - s.getTime()) / (1000 * 60 * 60 * 24);
                    if (days > 0) {
                        double total = days * v.getDailyRate();
                        totalLabel[0].setText(String.format(
                            "Estimated Total: $%.2f  (%d days × $%.0f)", total, days, v.getDailyRate()));
                    }
                } catch (Exception ignored) {}
            }
        };
        startField[0].getDocument().addDocumentListener(calc);
        endField[0].getDocument().addDocumentListener(calc);

        JButton confirmBtn = AppTheme.createPrimaryButton("Confirm Rental");
        confirmBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        confirmBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        confirmBtn.addActionListener(e -> {
            // Validate dates
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date startDate, endDate;
            try {
                startDate = sdf.parse(startField[0].getText().trim());
                endDate   = sdf.parse(endField[0].getText().trim());
            } catch (java.text.ParseException pe) {
                JOptionPane.showMessageDialog(dialog,
                    "Invalid date format. Use yyyy-MM-dd.", "Date Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!endDate.after(startDate)) {
                JOptionPane.showMessageDialog(dialog,
                    "Return date must be after start date.", "Date Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            long days = (endDate.getTime() - startDate.getTime()) / (1000L * 60 * 60 * 24);
            double total   = days * v.getDailyRate();
            double deposit = total * 1.10;

            // Build rental object
            model.Rental rental = new model.Rental();
            rental.setCustomer(currentUser);
            rental.setVehicle(v);
            rental.setStartDate(startDate);
            rental.setEndDate(endDate);
            rental.setRentalDays((int) days);
            rental.setTotalAmount(total);
            rental.setDepositAmount(deposit);
            rental.setRentalStatus("ONGOING");

            // Submit via RMI
            new javax.swing.SwingWorker<String, Void>() {
                @Override protected String doInBackground() throws Exception {
                    Registry reg = controller.ClientRegistry.getRegistry();
                    String result = ((service.RentalService) reg.lookup("rental")).registerRental(rental);
                    // Mark vehicle as RENTED
                    v.setAvailabilityStatus("RENTED");
                    ((service.VehicleService) reg.lookup("vehicle")).updateVehicle(v);
                    return result;
                }
                @Override protected void done() {
                    try {
                        get();
                        dialog.dispose();
                        // Determine created rental ID (re-fetch is cleanest; use 0 as placeholder)
                        String vehicleDesc = v.getBrand() + " " + v.getModel() + " (" + v.getPlateNumber() + ")";
                        NotificationBus.get().rentalCreated(
                            currentUser.getUserId(),
                            0, // ID not returned by registerRental; bus still fires
                            currentUser.getFullName(),
                            vehicleDesc,
                            (int) days,
                            total
                        );
                        // Refresh vehicle list
                        loadVehicles();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(dialog,
                            "Error creating rental: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        });
        JButton cancelBtn = AppTheme.createSecondaryButton("Cancel");
        cancelBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        cancelBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        cancelBtn.addActionListener(e -> dialog.dispose());

        content.add(confirmBtn);
        content.add(Box.createVerticalStrut(6));
        content.add(cancelBtn);

        dialog.add(content);
        dialog.setVisible(true);
    }

    private JTextField dialogField(JPanel p, String label, String ph) {
        JLabel l = new JLabel(label);
        l.setFont(AppTheme.FONT_BODY_BOLD);
        l.setForeground(AppTheme.TEXT_PRIMARY);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        JTextField tf = AppTheme.createTextField(ph);
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        tf.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(l); p.add(Box.createVerticalStrut(4)); p.add(tf); p.add(Box.createVerticalStrut(12));
        return tf;
    }

    private JLabel buildChip(String text) {
        JLabel chip = new JLabel(text, JLabel.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AppTheme.PRIMARY_LIGHT);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        chip.setFont(new Font("Segoe UI", Font.BOLD, 10));
        chip.setForeground(AppTheme.PRIMARY);
        chip.setOpaque(false);
        chip.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
        return chip;
    }

    private JPanel buildEmptyState(String title, String sub, String icon) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(AppTheme.CONTENT_BG);
        p.setPreferredSize(new Dimension(800, 300));

        JPanel inner = new JPanel();
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setBackground(AppTheme.CONTENT_BG);

        JLabel iconL = new JLabel(icon, JLabel.CENTER);
        iconL.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 52));
        iconL.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleL = new JLabel(title, JLabel.CENTER);
        titleL.setFont(AppTheme.FONT_SUBTITLE);
        titleL.setForeground(AppTheme.TEXT_SECONDARY);
        titleL.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subL = new JLabel(sub, JLabel.CENTER);
        subL.setFont(AppTheme.FONT_SMALL);
        subL.setForeground(AppTheme.TEXT_MUTED);
        subL.setAlignmentX(Component.CENTER_ALIGNMENT);

        inner.add(iconL);
        inner.add(Box.createVerticalStrut(12));
        inner.add(titleL);
        inner.add(Box.createVerticalStrut(6));
        inner.add(subL);
        p.add(inner);
        return p;
    }

    private JPanel buildLoadingPlaceholder() {
        return buildEmptyState("Loading Fleet...", "Please wait while vehicles are fetched.", "⏳");
    }

    private String vehicleEmoji(String category) {
        if (category == null) return "🚗";
        switch (category.toUpperCase()) {
            case "SUV":        return "🚙";
            case "TRUCK":      return "🚛";
            case "VAN":        return "🚐";
            case "MOTORCYCLE": return "🏍️";
            case "ELECTRIC":   return "⚡";
            default:           return "🚗";
        }
    }

    /**
     * WrapLayout - FlowLayout variant that wraps to new lines as container shrinks.
     * Standard utility for card grids in Swing.
     */
    static class WrapLayout extends FlowLayout {
        public WrapLayout(int align, int hgap, int vgap) {
            super(align, hgap, vgap);
        }
        @Override
        public Dimension preferredLayoutSize(Container target) {
            return layoutSize(target, true);
        }
        @Override
        public Dimension minimumLayoutSize(Container target) {
            Dimension minimum = layoutSize(target, false);
            minimum.width -= (getHgap() + 1);
            return minimum;
        }
        private Dimension layoutSize(Container target, boolean preferred) {
            synchronized (target.getTreeLock()) {
                int targetWidth = target.getSize().width;
                if (targetWidth == 0) targetWidth = Integer.MAX_VALUE;
                int hgap = getHgap(), vgap = getVgap();
                Insets insets = target.getInsets();
                int maxWidth = targetWidth - (insets.left + insets.right + hgap * 2);
                Dimension dim = new Dimension(0, 0);
                int rowWidth = 0, rowHeight = 0;
                int nmembers = target.getComponentCount();
                for (int i = 0; i < nmembers; i++) {
                    Component m = target.getComponent(i);
                    if (m.isVisible()) {
                        Dimension d = preferred ? m.getPreferredSize() : m.getMinimumSize();
                        if (rowWidth + d.width > maxWidth) {
                            addRow(dim, rowWidth, rowHeight);
                            rowWidth = 0; rowHeight = 0;
                        }
                        if (rowWidth != 0) rowWidth += hgap;
                        rowWidth += d.width;
                        rowHeight = Math.max(rowHeight, d.height);
                    }
                }
                addRow(dim, rowWidth, rowHeight);
                dim.width += insets.left + insets.right + hgap * 2;
                dim.height += insets.top + insets.bottom + vgap * 2;
                return dim;
            }
        }
        private void addRow(Dimension dim, int rowWidth, int rowHeight) {
            dim.width = Math.max(dim.width, rowWidth);
            if (dim.height > 0) dim.height += getVgap();
            dim.height += rowHeight;
        }
    }
}