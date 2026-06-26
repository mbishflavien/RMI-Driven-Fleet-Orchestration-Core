package view;

import view.util.*;
import controller.ClientRegistry;
import model.*;
import service.*;
import util.ReportExporter;

import java.awt.*;
import java.awt.event.*;
import java.rmi.registry.Registry;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.*;

/**
 * ReportsAnalyticsFrame — Executive analytics dashboard with fully working
 * CSV, Excel (.xlsx), PDF, and Print export functionality.
 * All exports are pure Java — no external libraries required.
 */
public class ReportsAnalyticsFrame extends javax.swing.JFrame {

    public ReportsAnalyticsFrame() {
        AppTheme.applyGlobalDefaults();
        initComponents();
        setTitle("VRS — Reports & Analytics");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1280, 820);
        setLocationRelativeTo(null);
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(AppTheme.CONTENT_BG);
        root.add(new AnalyticsPanel(), BorderLayout.CENTER);
        setContentPane(root);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 400, Short.MAX_VALUE));
        layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 300, Short.MAX_VALUE));
        pack();
    }// </editor-fold>//GEN-END:initComponents

    // ── EMBEDDABLE ANALYTICS PANEL ──────────────────────────────────────────────
    public static class AnalyticsPanel extends JPanel {

        // Live data cache — populated by loadAnalytics(), used by export buttons
        private List<Rental>  cachedRentals  = new ArrayList<>();
        private List<Payment> cachedPayments = new ArrayList<>();
        private List<Vehicle> cachedVehicles = new ArrayList<>();
        private List<User>    cachedUsers    = new ArrayList<>();

        private KpiCard kpiRevenue, kpiRentals, kpiCustomers, kpiLate;
        private KpiCard kpiAvgRental, kpiFleetUtil, kpiAvailable, kpiCompleted;

        private BarChartPanel revenueChart;
        private PieChartPanel fleetPieChart;
        private JTable topCustTable;
        private DefaultTableModel topCustModel;

        // Export buttons — kept as fields so we can enable after data loads
        private JButton btnExportCSV, btnExportPDF, btnExportExcel, btnPrint;

        public AnalyticsPanel() {
            setLayout(new BorderLayout());
            setBackground(AppTheme.CONTENT_BG);
            buildUI();
            loadAnalytics();
        }

        private void buildUI() {
            JPanel main = new JPanel();
            main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
            main.setBackground(AppTheme.CONTENT_BG);
            main.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

            // ── PAGE HEADER ───────────────────────────────────────────────────
            JPanel header = new JPanel(new BorderLayout());
            header.setBackground(AppTheme.CONTENT_BG);
            header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));
            header.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));

            JPanel titleCol = new JPanel();
            titleCol.setLayout(new BoxLayout(titleCol, BoxLayout.Y_AXIS));
            titleCol.setBackground(AppTheme.CONTENT_BG);
            JLabel title = new JLabel("Reports & Analytics");
            title.setFont(AppTheme.FONT_TITLE);
            title.setForeground(AppTheme.TEXT_PRIMARY);
            JLabel sub = new JLabel("Executive fleet performance overview");
            sub.setFont(AppTheme.FONT_BODY);
            sub.setForeground(AppTheme.TEXT_SECONDARY);
            titleCol.add(title); titleCol.add(sub);

            JPanel dateRange = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
            dateRange.setBackground(AppTheme.CONTENT_BG);
            JButton refreshBtn = AppTheme.createSecondaryButton("↺  Refresh Data");
            refreshBtn.addActionListener(e -> loadAnalytics());
            dateRange.add(refreshBtn);

            header.add(titleCol,  BorderLayout.WEST);
            header.add(dateRange, BorderLayout.EAST);
            main.add(header);

            // ── KPI ROW 1 ─────────────────────────────────────────────────────
            kpiRevenue   = new KpiCard("💰", "Total Revenue",     "$0.00", "All payments",   AppTheme.SUCCESS);
            kpiRentals   = new KpiCard("📋", "Total Rentals",     "0",     "All time",       AppTheme.PRIMARY);
            kpiCustomers = new KpiCard("👥", "Total Customers",   "0",     "Registered",     AppTheme.INFO);
            kpiLate      = new KpiCard("⚠️", "Late Returns",      "0",     "Overdue",        AppTheme.DANGER);

            JPanel row1 = KpiCard.buildKpiRow(kpiRevenue, kpiRentals, kpiCustomers, kpiLate);
            row1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
            main.add(row1);
            main.add(Box.createVerticalStrut(12));

            // ── KPI ROW 2 ─────────────────────────────────────────────────────
            kpiAvgRental = new KpiCard("📊", "Avg Rental Value",  "$0.00", "Per transaction", AppTheme.PURPLE);
            kpiFleetUtil = new KpiCard("🚗", "Fleet Utilization", "0%",    "Active vs total", AppTheme.WARNING);
            kpiAvailable = new KpiCard("✅", "Available Vehicles","0",     "Ready to rent",   AppTheme.SUCCESS);
            kpiCompleted = new KpiCard("🏁", "Completed Rentals", "0",     "Returned",        AppTheme.PRIMARY);

            JPanel row2 = KpiCard.buildKpiRow(kpiAvgRental, kpiFleetUtil, kpiAvailable, kpiCompleted);
            row2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
            main.add(row2);
            main.add(Box.createVerticalStrut(20));

            // ── CHART ROW ──────────────────────────────────────────────────────
            JPanel chartRow = new JPanel(new GridLayout(1, 2, 14, 0));
            chartRow.setBackground(AppTheme.CONTENT_BG);
            chartRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 260));

            JPanel revCard = AppTheme.createCard();
            revCard.setLayout(new BorderLayout(0, 10));
            revCard.add(AppTheme.createSectionTitle("Monthly Revenue"), BorderLayout.NORTH);
            revenueChart = new BarChartPanel();
            revCard.add(revenueChart, BorderLayout.CENTER);

            JPanel pieCard = AppTheme.createCard();
            pieCard.setLayout(new BorderLayout(0, 10));
            pieCard.add(AppTheme.createSectionTitle("Fleet Status"), BorderLayout.NORTH);
            fleetPieChart = new PieChartPanel();
            pieCard.add(fleetPieChart, BorderLayout.CENTER);

            chartRow.add(revCard);
            chartRow.add(pieCard);
            main.add(chartRow);
            main.add(Box.createVerticalStrut(14));

            // ── BOTTOM ROW ─────────────────────────────────────────────────────
            JPanel bottomRow = new JPanel(new GridLayout(1, 2, 14, 0));
            bottomRow.setBackground(AppTheme.CONTENT_BG);
            bottomRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 280));

            JPanel custCard = AppTheme.createCard();
            custCard.setLayout(new BorderLayout());
            custCard.add(AppTheme.createSectionTitle("Top Customers"), BorderLayout.NORTH);
            String[] custCols = {"Customer", "Rentals", "Total Spent", "Status"};
            topCustModel = new DefaultTableModel(custCols, 0) {
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };
            topCustTable = new JTable(topCustModel);
            TableStyler.applyStyle(topCustTable);
            topCustTable.getColumnModel().getColumn(3).setCellRenderer(new TableStyler.StatusBadgeRenderer());
            custCard.add(AppTheme.createScrollPane(topCustTable), BorderLayout.CENTER);

            JPanel statusCard = AppTheme.createCard();
            statusCard.setLayout(new BorderLayout(0, 10));
            statusCard.add(AppTheme.createSectionTitle("Rental Status Breakdown"), BorderLayout.NORTH);

            JPanel statusBars = new JPanel();
            statusBars.setLayout(new BoxLayout(statusBars, BoxLayout.Y_AXIS));
            statusBars.setBackground(AppTheme.CARD_BG);
            statusBars.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
            statusBars.add(buildStatusBar("Ongoing",   AppTheme.WARNING, 0));
            statusBars.add(Box.createVerticalStrut(10));
            statusBars.add(buildStatusBar("Completed", AppTheme.SUCCESS, 0));
            statusBars.add(Box.createVerticalStrut(10));
            statusBars.add(buildStatusBar("Cancelled", AppTheme.DANGER,  0));
            statusBars.add(Box.createVerticalStrut(10));
            statusBars.add(buildStatusBar("Overdue",   AppTheme.PURPLE,  0));
            statusCard.add(statusBars, BorderLayout.CENTER);

            bottomRow.add(custCard);
            bottomRow.add(statusCard);
            main.add(bottomRow);
            main.add(Box.createVerticalStrut(14));

            // ── EXPORT SECTION ─────────────────────────────────────────────────
            JPanel exportCard = AppTheme.createCard();
            exportCard.setLayout(new BorderLayout());
            exportCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 105));

            JPanel exportHeader = new JPanel(new BorderLayout());
            exportHeader.setBackground(AppTheme.CARD_BG);
            exportHeader.add(AppTheme.createSectionTitle("Export & Reports"), BorderLayout.WEST);

            JLabel exportNote = new JLabel("Exports use live data — refresh first to ensure latest figures.");
            exportNote.setFont(AppTheme.FONT_SMALL);
            exportNote.setForeground(AppTheme.TEXT_MUTED);
            exportHeader.add(exportNote, BorderLayout.EAST);
            exportCard.add(exportHeader, BorderLayout.NORTH);

            JPanel exportBtns = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
            exportBtns.setBackground(AppTheme.CARD_BG);

            // CSV
            btnExportCSV = AppTheme.createSuccessButton("Export CSV");
            btnExportCSV.setToolTipText("Export rentals, payments and fleet data to a CSV file");
            btnExportCSV.setEnabled(false);
            btnExportCSV.addActionListener(e -> {
                btnExportCSV.setEnabled(false);
                btnExportCSV.setText("Exporting...");
                SwingUtilities.invokeLater(() -> {
                    ReportExporter.exportCSV(AnalyticsPanel.this,
                        cachedRentals, cachedPayments, cachedVehicles, cachedUsers);
                    btnExportCSV.setText("Export CSV");
                    btnExportCSV.setEnabled(true);
                });
            });

            // Excel
            btnExportExcel = AppTheme.createSuccessButton("Export Excel");
            btnExportExcel.setToolTipText("Export to .xlsx with three sheets: Rentals, Payments, Fleet");
            btnExportExcel.setEnabled(false);
            btnExportExcel.addActionListener(e -> {
                btnExportExcel.setEnabled(false);
                btnExportExcel.setText("Exporting...");
                SwingUtilities.invokeLater(() -> {
                    ReportExporter.exportExcel(AnalyticsPanel.this,
                        cachedRentals, cachedPayments, cachedVehicles);
                    btnExportExcel.setText("Export Excel");
                    btnExportExcel.setEnabled(true);
                });
            });

            // PDF
            btnExportPDF = AppTheme.createDangerButton("Export PDF");
            btnExportPDF.setToolTipText("Export a full system report as a PDF document");
            btnExportPDF.setEnabled(false);
            btnExportPDF.addActionListener(e -> {
                btnExportPDF.setEnabled(false);
                btnExportPDF.setText("Generating PDF...");
                SwingUtilities.invokeLater(() -> {
                    ReportExporter.exportPDF(AnalyticsPanel.this,
                        cachedRentals, cachedPayments, cachedVehicles);
                    btnExportPDF.setText("?Export PDF");
                    btnExportPDF.setEnabled(true);
                });
            });

            // Print
            btnPrint = AppTheme.createSecondaryButton("Print Report");
            btnPrint.setToolTipText("Send a 3-page report to a connected printer");
            btnPrint.setEnabled(false);
            btnPrint.addActionListener(e -> {
                ReportExporter.printReport(AnalyticsPanel.this,
                    cachedRentals, cachedPayments, cachedVehicles);
            });

            exportBtns.add(btnExportCSV);
            exportBtns.add(btnExportExcel);
            exportBtns.add(btnExportPDF);
            exportBtns.add(btnPrint);

            // Loading indicator
            JLabel loadingLbl = new JLabel("Loading data for export...");
            loadingLbl.setFont(AppTheme.FONT_SMALL);
            loadingLbl.setForeground(AppTheme.TEXT_MUTED);
            loadingLbl.setName("loadingLbl");
            exportBtns.add(loadingLbl);

            exportCard.add(exportBtns, BorderLayout.CENTER);
            main.add(exportCard);
            main.add(Box.createVerticalStrut(24));

            add(AppTheme.createScrollPane(main), BorderLayout.CENTER);
        }

        // ── DATA LOADING ─────────────────────────────────────────────────────────
        private void loadAnalytics() {
            // Disable buttons while loading
            setExportButtonsEnabled(false);

            SwingWorker<Map<String, Object>, Void> w = new SwingWorker<Map<String, Object>, Void>() {
                @Override
                protected Map<String, Object> doInBackground() throws Exception {
                    Map<String, Object> data = new HashMap<>();
                    try {
                        Registry r = ClientRegistry.getRegistry();
                        VehicleService vs  = (VehicleService)  r.lookup("vehicle");
                        UserService    us  = (UserService)     r.lookup("user");
                        RentalService  rs  = (RentalService)   r.lookup("rental");
                        PaymentService ps  = (PaymentService)  r.lookup("payment");

                        List<Vehicle> vehicles   = vs.displayAllVehicles();
                        List<User>    users       = us.displayAllUsers();
                        List<Rental>  rentals     = rs.displayAllRentals();
                        List<Payment> payments    = ps.displayAllPayments();

                        double totalRev = 0;
                        for (Payment p : payments) totalRev += p.getAmountPaid();
                        data.put("totalRev", totalRev);
                        data.put("avgRental", payments.isEmpty() ? 0.0 : totalRev / payments.size());

                        data.put("totalRentals",   rentals.size());
                        data.put("totalCustomers", users.stream().filter(u -> "CUSTOMER".equalsIgnoreCase(u.getRole())).count());

                        long available = vehicles.stream().filter(v -> "AVAILABLE".equalsIgnoreCase(v.getAvailabilityStatus())).count();
                        long rented    = vehicles.stream().filter(v -> "RENTED".equalsIgnoreCase(v.getAvailabilityStatus())).count();
                        data.put("available",  available);
                        data.put("rented",     rented);
                        data.put("fleetTotal", vehicles.size());
                        data.put("utilPct",    vehicles.isEmpty() ? 0 : (int)(rented * 100 / vehicles.size()));

                        int ongoing=0, completed=0, cancelled=0, overdue=0, late=0;
                        for (Rental rental : rentals) {
                            String s = rental.getRentalStatus() == null ? "" : rental.getRentalStatus().toUpperCase();
                            if (s.contains("ONGOING"))    ongoing++;
                            else if (s.contains("COMPLET")) completed++;
                            else if (s.contains("CANCEL"))  cancelled++;
                            else if (s.contains("OVERDU") || s.contains("LATE")) { overdue++; late++; }
                        }
                        data.put("ongoing", ongoing); data.put("completed", completed);
                        data.put("cancelled", cancelled); data.put("overdue", overdue);
                        data.put("lateReturns", late);

                        int[] monthlyRev = new int[6];
                        Calendar cal = Calendar.getInstance();
                        for (Payment p : payments) {
                            if (p.getPaymentDate() != null) {
                                int monthsDiff = (int)((cal.getTimeInMillis() - p.getPaymentDate().getTime())
                                    / (1000L * 60 * 60 * 24 * 30));
                                if (monthsDiff >= 0 && monthsDiff < 6)
                                    monthlyRev[5 - monthsDiff] += (int) p.getAmountPaid();
                            }
                        }
                        data.put("monthlyRev", monthlyRev);
                        data.put("vehicles", vehicles);
                        data.put("users",    users);
                        data.put("rentals",  rentals);
                        data.put("payments", payments);

                    } catch (Exception ignored) {}
                    return data;
                }

                @Override
                @SuppressWarnings("unchecked")
                protected void done() {
                    try {
                        Map<String, Object> d = get();

                        // Cache for export use
                        cachedVehicles = (List<Vehicle>) d.getOrDefault("vehicles", new ArrayList<>());
                        cachedUsers    = (List<User>)    d.getOrDefault("users",    new ArrayList<>());
                        cachedRentals  = (List<Rental>)  d.getOrDefault("rentals",  new ArrayList<>());
                        cachedPayments = (List<Payment>) d.getOrDefault("payments", new ArrayList<>());

                        double totalRev = (Double) d.getOrDefault("totalRev", 0.0);
                        double avgR     = (Double) d.getOrDefault("avgRental", 0.0);
                        int totalRent   = (Integer) d.getOrDefault("totalRentals", 0);
                        long custCount  = (Long) d.getOrDefault("totalCustomers", 0L);
                        int lateRet     = (Integer) d.getOrDefault("lateReturns", 0);
                        long avail      = (Long) d.getOrDefault("available", 0L);
                        int fleetTotal  = (Integer) d.getOrDefault("fleetTotal", 0);
                        int utilPct     = (Integer) d.getOrDefault("utilPct", 0);
                        int completed   = (Integer) d.getOrDefault("completed", 0);

                        kpiRevenue.setValue(String.format("$%,.2f", totalRev));
                        kpiRevenue.setTrend("All payments");
                        kpiRentals.setValue(String.valueOf(totalRent));
                        kpiRentals.setTrend("All time");
                        kpiCustomers.setValue(String.valueOf(custCount));
                        kpiCustomers.setTrend("Registered accounts");
                        kpiLate.setValue(String.valueOf(lateRet));
                        kpiLate.setTrend(lateRet > 0 ? "Action required" : "None ✓");
                        kpiAvgRental.setValue(String.format("$%.2f", avgR));
                        kpiAvgRental.setTrend("Per transaction");
                        kpiFleetUtil.setValue(utilPct + "%");
                        kpiFleetUtil.setTrend(utilPct > 70 ? "High demand" : "Normal");
                        kpiAvailable.setValue(String.valueOf(avail));
                        kpiAvailable.setTrend("of " + fleetTotal + " total");
                        kpiCompleted.setValue(String.valueOf(completed));
                        kpiCompleted.setTrend("Returned rentals");

                        int[] monthly = (int[]) d.getOrDefault("monthlyRev", new int[6]);
                        revenueChart.setData(monthly);

                        long rented = (Long) d.getOrDefault("rented", 0L);
                        long maint  = Math.max(0, fleetTotal - avail - rented);
                        fleetPieChart.setData(
                            new long[]{avail, rented, maint},
                            new String[]{"Available", "Rented", "Maintenance"},
                            new Color[]{AppTheme.SUCCESS, AppTheme.WARNING, AppTheme.DANGER}
                        );

                        topCustModel.setRowCount(0);
                        Map<Integer, int[]> custStats = new HashMap<>();
                        for (Rental rental : cachedRentals) {
                            if (rental.getCustomer() != null) {
                                int uid = rental.getCustomer().getUserId();
                                custStats.computeIfAbsent(uid, k -> new int[]{0, 0})[0]++;
                            }
                        }
                        for (Payment p : cachedPayments) {
                            if (p.getRental() != null && p.getRental().getCustomer() != null) {
                                int uid = p.getRental().getCustomer().getUserId();
                                custStats.computeIfAbsent(uid, k -> new int[]{0, 0})[1] += (int) p.getAmountPaid();
                            }
                        }
                        custStats.entrySet().stream()
                            .sorted((a, b) -> b.getValue()[1] - a.getValue()[1])
                            .limit(5)
                            .forEach(entry -> {
                                String name = cachedUsers.stream()
                                    .filter(u -> u.getUserId() == entry.getKey())
                                    .map(User::getFullName).findFirst().orElse("ID " + entry.getKey());
                                topCustModel.addRow(new Object[]{
                                    name, entry.getValue()[0],
                                    String.format("$%,.0f", (double) entry.getValue()[1]),
                                    "ACTIVE"
                                });
                            });

                        // Enable export buttons now that data is loaded
                        setExportButtonsEnabled(true);

                    } catch (Exception ignored) {
                        setExportButtonsEnabled(false);
                    }
                }
            };
            w.execute();
        }

        private void setExportButtonsEnabled(boolean enabled) {
            if (btnExportCSV   != null) btnExportCSV.setEnabled(enabled);
            if (btnExportExcel != null) btnExportExcel.setEnabled(enabled);
            if (btnExportPDF   != null) btnExportPDF.setEnabled(enabled);
            if (btnPrint       != null) btnPrint.setEnabled(enabled);
        }

        private JPanel buildStatusBar(String label, Color color, int percent) {
            JPanel row = new JPanel(new BorderLayout(10, 0));
            row.setBackground(AppTheme.CARD_BG);
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
            JLabel lbl = new JLabel(label);
            lbl.setFont(AppTheme.FONT_SMALL);
            lbl.setForeground(AppTheme.TEXT_SECONDARY);
            lbl.setPreferredSize(new Dimension(80, 20));
            JPanel barOuter = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(226, 232, 240));
                    g2.fillRoundRect(0, 4, getWidth(), 12, 6, 6);
                    int fill = (int)(getWidth() * (Math.min(percent, 100) / 100.0));
                    if (fill > 0) { g2.setColor(color); g2.fillRoundRect(0, 4, fill, 12, 6, 6); }
                    g2.dispose();
                }
            };
            barOuter.setBackground(AppTheme.CARD_BG);
            barOuter.setPreferredSize(new Dimension(0, 20));
            JLabel pctLbl = new JLabel(percent + "%");
            pctLbl.setFont(AppTheme.FONT_SMALL);
            pctLbl.setForeground(AppTheme.TEXT_MUTED);
            pctLbl.setPreferredSize(new Dimension(36, 20));
            row.add(lbl,      BorderLayout.WEST);
            row.add(barOuter, BorderLayout.CENTER);
            row.add(pctLbl,   BorderLayout.EAST);
            return row;
        }
    }

    // ── BAR CHART PANEL ───────────────────────────────────────────────────────────
    public static class BarChartPanel extends JPanel {
        private int[] data = {0, 0, 0, 0, 0, 0};
        private final String[] months;

        public BarChartPanel() {
            setBackground(AppTheme.CARD_BG);
            setPreferredSize(new Dimension(0, 160));
            String[] mNames = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
            months = new String[6];
            Calendar cal = Calendar.getInstance();
            for (int i = 5; i >= 0; i--) {
                months[5 - i] = mNames[cal.get(Calendar.MONTH)];
                cal.add(Calendar.MONTH, -1);
            }
        }

        public void setData(int[] monthlyData) { this.data = monthlyData; repaint(); }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            int padL = 50, padB = 30, padT = 16, padR = 16;
            int chartW = w - padL - padR, chartH = h - padB - padT;
            int max = 1;
            for (int v : data) max = Math.max(max, v);
            g2.setStroke(new BasicStroke(0.5f));
            for (int i = 0; i <= 4; i++) {
                int y = padT + (int)(chartH * (1 - i / 4.0));
                g2.setColor(new Color(226, 232, 240));
                g2.drawLine(padL, y, w - padR, y);
                g2.setColor(AppTheme.TEXT_MUTED);
                g2.setFont(AppTheme.FONT_SMALL);
                g2.drawString("$" + (int)(max * i / 4.0), 2, y + 4);
            }
            int barW   = (int)(chartW / (data.length * 1.6));
            int barGap = (chartW - barW * data.length) / (data.length + 1);
            for (int i = 0; i < data.length; i++) {
                int barH = (int)(chartH * ((double) data[i] / max));
                int x = padL + barGap + i * (barW + barGap);
                int y = padT + chartH - barH;
                GradientPaint gp = new GradientPaint(x, y, AppTheme.PRIMARY, x, padT + chartH, new Color(96, 165, 250));
                g2.setPaint(gp);
                g2.fillRoundRect(x, y, barW, barH, 6, 6);
                g2.setColor(AppTheme.TEXT_MUTED);
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                g2.drawString(months[i], x + barW / 2 - 8, h - 8);
            }
            g2.dispose();
        }
    }

    // ── PIE CHART PANEL ───────────────────────────────────────────────────────────
    public static class PieChartPanel extends JPanel {
        private long[]   values = {1, 0, 0};
        private String[] labels = {"Available", "Rented", "Maintenance"};
        private Color[]  colors = {AppTheme.SUCCESS, AppTheme.WARNING, AppTheme.DANGER};

        public PieChartPanel() { setBackground(AppTheme.CARD_BG); setPreferredSize(new Dimension(0, 160)); }

        public void setData(long[] values, String[] labels, Color[] colors) {
            this.values = values; this.labels = labels; this.colors = colors; repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            int size = Math.min(w / 2, h) - 24;
            int cx = w / 4, cy = h / 2;
            int x = cx - size / 2, y = cy - size / 2;
            long total = 0;
            for (long v : values) total += v;
            if (total == 0) { values = new long[]{1, 0, 0}; total = 1; }
            double startAngle = 0;
            for (int i = 0; i < values.length; i++) {
                double sweep = 360.0 * values[i] / total;
                g2.setColor(colors[i]);
                g2.fillArc(x, y, size, size, (int) startAngle, (int) sweep);
                startAngle += sweep;
            }
            g2.setColor(AppTheme.CARD_BG);
            int hole = size / 3;
            g2.fillOval(cx - hole / 2, cy - hole / 2, hole, hole);
            g2.setFont(AppTheme.FONT_SMALL);
            int legendX = w / 2 + 10, legendY = cy - (labels.length * 22 / 2);
            for (int i = 0; i < labels.length; i++) {
                g2.setColor(colors[i]);
                g2.fillRoundRect(legendX, legendY + i * 22 + 2, 12, 12, 4, 4);
                g2.setColor(AppTheme.TEXT_PRIMARY);
                long pct = total == 0 ? 0 : values[i] * 100 / total;
                g2.drawString(labels[i] + " (" + pct + "%)", legendX + 18, legendY + i * 22 + 13);
            }
            g2.dispose();
        }
    }
}
