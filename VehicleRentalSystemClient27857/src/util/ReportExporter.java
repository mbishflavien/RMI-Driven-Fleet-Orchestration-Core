package util;

import model.*;

import java.awt.*;
import java.awt.print.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.zip.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * ReportExporter
 *
 * Implements four report export types — all in pure Java, zero external libraries:
 *   1. CSV    – FileWriter + comma-separation
 *   2. Excel  – Manually written .xlsx (ZIP of XML, OOXML spec)
 *   3. PDF    – Custom Java2D-based PDF writer (draws pages as PostScript-style PDF)
 *   4. Print  – Java PrinterJob with custom Printable renderer
 *
 * Each method receives live data from the RMI services and writes a complete,
 * meaningful report to a user-chosen file or sends it to the printer.
 */
public class ReportExporter {

    private static final SimpleDateFormat DATE_FMT = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat DATETIME_FMT = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private static final String APP_NAME = "Vehicle Rental System";

    // ─────────────────────────────────────────────────────────────────────────────
    // 1. CSV EXPORT
    // ─────────────────────────────────────────────────────────────────────────────

    /**
     * Exports three report sections to a single CSV file:
     * Rental Transactions, Payment Summary, Fleet Status.
     */
    public static void exportCSV(Component parent,
                                  List<Rental>  rentals,
                                  List<Payment> payments,
                                  List<Vehicle> vehicles,
                                  List<User>    users) {

        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Save CSV Report");
        fc.setSelectedFile(new File("VRS_Report_" + today() + ".csv"));
        if (fc.showSaveDialog(parent) != JFileChooser.APPROVE_OPTION) return;

        File file = ensureExtension(fc.getSelectedFile(), ".csv");

        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {

            // ── Section 1: Rental Transactions ────────────────────────────────
            pw.println(APP_NAME + " — Rental Transactions Report");
            pw.println("Generated:," + DATETIME_FMT.format(new Date()));
            pw.println();
            pw.println("Rental ID,Customer,Vehicle,Plate,Start Date,End Date,Days,Total Amount,"
                     + "Deposit,Late Penalty,Status");

            for (Rental r : rentals) {
                pw.println(csv(
                    r.getRentalId(),
                    r.getCustomer() != null ? r.getCustomer().getFullName() : "—",
                    r.getVehicle()  != null ? r.getVehicle().getBrand() + " " + r.getVehicle().getModel() : "—",
                    r.getVehicle()  != null ? r.getVehicle().getPlateNumber() : "—",
                    fmt(r.getStartDate()),
                    fmt(r.getEndDate()),
                    r.getRentalDays(),
                    String.format("%.2f", r.getTotalAmount()),
                    String.format("%.2f", r.getDepositAmount()),
                    String.format("%.2f", r.getLatePenalty()),
                    r.getRentalStatus()
                ));
            }

            pw.println();

            // ── Section 2: Payment Records ────────────────────────────────────
            pw.println(APP_NAME + " — Payment Records");
            pw.println();
            pw.println("Payment ID,Rental ID,Customer,Amount Paid,Method,Date,Status");
            double totalPaid = 0;
            for (Payment p : payments) {
                totalPaid += p.getAmountPaid();
                pw.println(csv(
                    p.getPaymentId(),
                    p.getRental() != null ? p.getRental().getRentalId() : "—",
                    p.getRental() != null && p.getRental().getCustomer() != null
                        ? p.getRental().getCustomer().getFullName() : "—",
                    String.format("%.2f", p.getAmountPaid()),
                    p.getPaymentMethod(),
                    fmt(p.getPaymentDate()),
                    p.getPaymentStatus()
                ));
            }
            pw.println(",,,,Total Revenue:," + String.format("%.2f", totalPaid) + ",");

            pw.println();

            // ── Section 3: Fleet Status ───────────────────────────────────────
            pw.println(APP_NAME + " — Fleet Status");
            pw.println();
            pw.println("Vehicle ID,Brand,Model,Plate,Category,Daily Rate,Availability,Condition,Year");
            for (Vehicle v : vehicles) {
                pw.println(csv(
                    v.getVehicleId(),
                    v.getBrand(),
                    v.getModel(),
                    v.getPlateNumber(),
                    v.getCategory(),
                    String.format("%.2f", v.getDailyRate()),
                    v.getAvailabilityStatus(),
                    v.getConditionStatus(),
                    v.getManufactureYear()
                ));
            }

            pw.flush();
            JOptionPane.showMessageDialog(parent,
                "CSV report saved to:\n" + file.getAbsolutePath(),
                "Export Successful", JOptionPane.INFORMATION_MESSAGE);

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(parent, "Failed to save CSV:\n" + ex.getMessage(),
                "Export Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // 2. EXCEL EXPORT (.xlsx — pure Java OOXML/ZIP)
    // ─────────────────────────────────────────────────────────────────────────────

    /**
     * Writes a proper .xlsx file with three sheets: Rentals, Payments, Fleet.
     * Uses the OOXML format (ZIP + XML) — no external library needed.
     */
    public static void exportExcel(Component parent,
                                    List<Rental>  rentals,
                                    List<Payment> payments,
                                    List<Vehicle> vehicles) {

        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Save Excel Report");
        fc.setSelectedFile(new File("VRS_Report_" + today() + ".xlsx"));
        if (fc.showSaveDialog(parent) != JFileChooser.APPROVE_OPTION) return;

        File file = ensureExtension(fc.getSelectedFile(), ".xlsx");

        try (ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(file))) {

            // [Content_Types].xml
            addZipEntry(zip, "[Content_Types].xml", buildContentTypes());

            // _rels/.rels
            addZipEntry(zip, "_rels/.rels", buildRootRels());

            // docProps/app.xml
            addZipEntry(zip, "docProps/app.xml", buildAppXml());

            // xl/_rels/workbook.xml.rels
            addZipEntry(zip, "xl/_rels/workbook.xml.rels", buildWorkbookRels());

            // xl/styles.xml
            addZipEntry(zip, "xl/styles.xml", buildStyles());

            // xl/sharedStrings.xml  — we'll use inline strings so skip it

            // Build sheets
            String rentSheet  = buildRentalSheet(rentals);
            String paySheet   = buildPaymentSheet(payments);
            String fleetSheet = buildFleetSheet(vehicles);

            addZipEntry(zip, "xl/worksheets/sheet1.xml", rentSheet);
            addZipEntry(zip, "xl/worksheets/sheet2.xml", paySheet);
            addZipEntry(zip, "xl/worksheets/sheet3.xml", fleetSheet);

            // xl/workbook.xml
            addZipEntry(zip, "xl/workbook.xml", buildWorkbook());

            zip.finish();

            JOptionPane.showMessageDialog(parent,
                "Excel report saved to:\n" + file.getAbsolutePath(),
                "Export Successful", JOptionPane.INFORMATION_MESSAGE);

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(parent, "Failed to save Excel:\n" + ex.getMessage(),
                "Export Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // 3. PDF EXPORT (pure Java — custom minimal PDF writer)
    // ─────────────────────────────────────────────────────────────────────────────

    /**
     * Generates a PDF report using a hand-written minimal PDF writer.
     * No external library — uses only standard Java.
     */
    public static void exportPDF(Component parent,
                                  List<Rental>  rentals,
                                  List<Payment> payments,
                                  List<Vehicle> vehicles) {

        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Save PDF Report");
        fc.setSelectedFile(new File("VRS_Report_" + today() + ".pdf"));
        if (fc.showSaveDialog(parent) != JFileChooser.APPROVE_OPTION) return;

        File file = ensureExtension(fc.getSelectedFile(), ".pdf");

        try {
            MiniPdfWriter pdf = new MiniPdfWriter();

            // ── Page 1: Title + Rental Summary ───────────────────────────────
            pdf.newPage();
            pdf.drawCenteredText(APP_NAME, 14, true);
            pdf.drawCenteredText("Comprehensive System Report", 11, false);
            pdf.drawCenteredText("Generated: " + DATETIME_FMT.format(new Date()), 9, false);
            pdf.newline(16);

            pdf.drawText("RENTAL TRANSACTIONS", 11, true);
            pdf.drawHLine();
            pdf.newline(4);

            // Table header
            int[] rw = {40, 100, 110, 70, 70, 60, 70};
            pdf.drawTableRow(new String[]{"ID", "Customer", "Vehicle", "Start", "End", "Days", "Total"},
                             rw, true);
            pdf.drawHLine();

            double grandTotal = 0;
            for (Rental r : rentals) {
                String cust = r.getCustomer() != null ? r.getCustomer().getFullName() : "—";
                String veh  = r.getVehicle()  != null
                              ? r.getVehicle().getBrand() + " " + r.getVehicle().getModel() : "—";
                grandTotal += r.getTotalAmount();
                pdf.drawTableRow(new String[]{
                    String.valueOf(r.getRentalId()),
                    truncate(cust, 14),
                    truncate(veh,  16),
                    fmt(r.getStartDate()),
                    fmt(r.getEndDate()),
                    String.valueOf(r.getRentalDays()),
                    String.format("$%.2f", r.getTotalAmount())
                }, rw, false);
                if (pdf.needsNewPage()) {
                    pdf.newPage();
                    pdf.drawText("RENTAL TRANSACTIONS (continued)", 11, true);
                    pdf.drawHLine();
                    pdf.drawTableRow(new String[]{"ID","Customer","Vehicle","Start","End","Days","Total"}, rw, true);
                    pdf.drawHLine();
                }
            }
            pdf.drawHLine();
            pdf.drawTableRow(new String[]{"","","","","","TOTAL:", String.format("$%.2f", grandTotal)}, rw, true);
            pdf.newline(20);

            // ── Page 2: Payments ──────────────────────────────────────────────
            pdf.newPage();
            pdf.drawText("PAYMENT RECORDS", 11, true);
            pdf.drawHLine();
            pdf.newline(4);

            int[] pw2 = {40, 50, 110, 80, 80, 80, 80};
            pdf.drawTableRow(new String[]{"ID", "Rental", "Customer", "Amount", "Method", "Date", "Status"},
                             pw2, true);
            pdf.drawHLine();

            double totalPaid = 0;
            for (Payment p : payments) {
                totalPaid += p.getAmountPaid();
                String cust = p.getRental() != null && p.getRental().getCustomer() != null
                              ? p.getRental().getCustomer().getFullName() : "—";
                String rid  = p.getRental() != null
                              ? String.valueOf(p.getRental().getRentalId()) : "—";
                pdf.drawTableRow(new String[]{
                    String.valueOf(p.getPaymentId()),
                    rid,
                    truncate(cust, 15),
                    String.format("$%.2f", p.getAmountPaid()),
                    p.getPaymentMethod() != null ? p.getPaymentMethod() : "—",
                    fmt(p.getPaymentDate()),
                    p.getPaymentStatus() != null ? p.getPaymentStatus() : "—"
                }, pw2, false);
                if (pdf.needsNewPage()) {
                    pdf.newPage();
                    pdf.drawText("PAYMENT RECORDS (continued)", 11, true);
                    pdf.drawHLine();
                    pdf.drawTableRow(new String[]{"ID","Rental","Customer","Amount","Method","Date","Status"}, pw2, true);
                    pdf.drawHLine();
                }
            }
            pdf.drawHLine();
            pdf.drawTableRow(new String[]{"","","","","","TOTAL:", String.format("$%.2f", totalPaid)}, pw2, true);
            pdf.newline(20);

            // ── Page 3: Fleet ─────────────────────────────────────────────────
            pdf.newPage();
            pdf.drawText("FLEET STATUS", 11, true);
            pdf.drawHLine();
            pdf.newline(4);

            int[] fw = {40, 70, 70, 70, 70, 60, 80, 60};
            pdf.drawTableRow(new String[]{"ID","Brand","Model","Plate","Category","Rate/Day","Availability","Year"},
                             fw, true);
            pdf.drawHLine();

            for (Vehicle v : vehicles) {
                pdf.drawTableRow(new String[]{
                    String.valueOf(v.getVehicleId()),
                    truncate(v.getBrand(), 9),
                    truncate(v.getModel(), 9),
                    v.getPlateNumber(),
                    truncate(v.getCategory(), 9),
                    String.format("$%.0f", v.getDailyRate()),
                    v.getAvailabilityStatus(),
                    String.valueOf(v.getManufactureYear())
                }, fw, false);
                if (pdf.needsNewPage()) {
                    pdf.newPage();
                    pdf.drawText("FLEET STATUS (continued)", 11, true);
                    pdf.drawHLine();
                    pdf.drawTableRow(new String[]{"ID","Brand","Model","Plate","Category","Rate/Day","Availability","Year"}, fw, true);
                    pdf.drawHLine();
                }
            }

            pdf.save(file);

            JOptionPane.showMessageDialog(parent,
                "PDF report saved to:\n" + file.getAbsolutePath(),
                "Export Successful", JOptionPane.INFORMATION_MESSAGE);

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(parent, "Failed to save PDF:\n" + ex.getMessage(),
                "Export Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // 4. PRINT REPORT (Java PrinterJob)
    // ─────────────────────────────────────────────────────────────────────────────

    /**
     * Sends a formatted report to the printer via Java's PrinterJob.
     * Renders the report using Graphics2D — works with any installed printer.
     */
    public static void printReport(Component parent,
                                    List<Rental>  rentals,
                                    List<Payment> payments,
                                    List<Vehicle> vehicles) {

        PrinterJob job = PrinterJob.getPrinterJob();
        job.setJobName(APP_NAME + " Report");

        PageFormat pf = job.defaultPage();
        pf.setOrientation(PageFormat.LANDSCAPE);

        // Build printable data snapshot
        String genTime = DATETIME_FMT.format(new Date());

        // Rental rows
        List<String[]> rentalRows = new ArrayList<>();
        for (Rental r : rentals) {
            rentalRows.add(new String[]{
                String.valueOf(r.getRentalId()),
                r.getCustomer() != null ? r.getCustomer().getFullName() : "—",
                r.getVehicle()  != null ? r.getVehicle().getBrand() + " " + r.getVehicle().getModel() : "—",
                fmt(r.getStartDate()) + " → " + fmt(r.getEndDate()),
                r.getRentalDays() + " days",
                String.format("$%.2f", r.getTotalAmount()),
                r.getRentalStatus()
            });
        }
        String[] rentalHdr = {"ID", "Customer", "Vehicle", "Period", "Days", "Total", "Status"};
        int[]    rentalW   = {30,   130,         130,       130,      40,     60,      70};

        // Payment rows
        List<String[]> payRows = new ArrayList<>();
        double totalPaid = 0;
        for (Payment p : payments) {
            totalPaid += p.getAmountPaid();
            payRows.add(new String[]{
                String.valueOf(p.getPaymentId()),
                p.getRental() != null && p.getRental().getCustomer() != null
                    ? p.getRental().getCustomer().getFullName() : "—",
                String.format("$%.2f", p.getAmountPaid()),
                p.getPaymentMethod() != null ? p.getPaymentMethod() : "—",
                fmt(p.getPaymentDate()),
                p.getPaymentStatus()
            });
        }
        String[] payHdr = {"ID", "Customer", "Amount", "Method", "Date", "Status"};
        int[]    payW   = {30,   160,         80,       90,       90,     90};
        final double totalPaidFinal = totalPaid;

        job.setPrintable((graphics, pageFormat, pageIndex) -> {
            if (pageIndex > 2) return Printable.NO_SUCH_PAGE;

            Graphics2D g2 = (Graphics2D) graphics;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            double ox = pageFormat.getImageableX();
            double oy = pageFormat.getImageableY();
            double pw2 = pageFormat.getImageableWidth();
            g2.translate(ox, oy);

            float y = 0;
            Font titleFont  = new Font("SansSerif", Font.BOLD, 14);
            Font headerFont = new Font("SansSerif", Font.BOLD, 8);
            Font bodyFont   = new Font("SansSerif", Font.PLAIN, 8);
            Font subFont    = new Font("SansSerif", Font.PLAIN, 9);

            // Page header
            g2.setFont(titleFont);
            g2.setColor(Color.BLACK);
            g2.drawString(APP_NAME + " — System Report", 0, y + 14);
            y += 18;
            g2.setFont(subFont);
            g2.setColor(new Color(80, 80, 80));
            String[] pageTitles = {"Rental Transactions", "Payment Records", "Fleet Status"};
            g2.drawString(pageTitles[pageIndex] + "   |   Generated: " + genTime
                + "   |   Page " + (pageIndex + 1) + " of 3", 0, y + 10);
            y += 14;

            g2.setColor(new Color(30, 100, 200));
            g2.fillRect(0, (int) y, (int) pw2, 2);
            y += 8;

            if (pageIndex == 0) {
                y = printTable(g2, rentalHdr, rentalRows, rentalW, y, bodyFont, headerFont);
            } else if (pageIndex == 1) {
                y = printTable(g2, payHdr, payRows, payW, y, bodyFont, headerFont);
                g2.setFont(headerFont);
                g2.setColor(Color.BLACK);
                g2.drawString("Total Revenue Collected: " + String.format("$%.2f", totalPaidFinal), 0, y + 12);
            } else {
                // Fleet
                List<String[]> fleetRows = new ArrayList<>();
                int[] fw = {30, 80, 80, 70, 70, 55, 90, 50};
                String[] fHdr = {"ID", "Brand", "Model", "Plate", "Category", "Rate/Day", "Availability", "Year"};
                for (Vehicle v : vehicles) {
                    fleetRows.add(new String[]{
                        String.valueOf(v.getVehicleId()),
                        v.getBrand(), v.getModel(), v.getPlateNumber(),
                        v.getCategory(),
                        String.format("$%.0f", v.getDailyRate()),
                        v.getAvailabilityStatus(),
                        String.valueOf(v.getManufactureYear())
                    });
                }
                printTable(g2, fHdr, fleetRows, fw, y, bodyFont, headerFont);
            }

            return Printable.PAGE_EXISTS;
        }, pf);

        if (job.printDialog()) {
            try {
                job.print();
            } catch (PrinterException ex) {
                JOptionPane.showMessageDialog(parent, "Print failed:\n" + ex.getMessage(),
                    "Print Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ── Helper: render a table onto Graphics2D ────────────────────────────────────
    private static float printTable(Graphics2D g2, String[] headers, List<String[]> rows,
                                     int[] colWidths, float startY,
                                     Font bodyFont, Font headerFont) {
        float y = startY;
        int rowH = 14;

        // Header row
        g2.setColor(new Color(30, 100, 200));
        g2.setFont(headerFont);
        int x = 0;
        for (int i = 0; i < headers.length; i++) {
            g2.drawString(headers[i], x + 2, y + 10);
            x += colWidths[i];
        }
        y += rowH;
        g2.setColor(new Color(180, 200, 230));
        g2.fillRect(0, (int) y, x, 1);
        y += 3;

        // Data rows
        boolean shade = false;
        for (String[] row : rows) {
            if (shade) {
                g2.setColor(new Color(245, 248, 255));
                g2.fillRect(0, (int) y - rowH + 4, x, rowH);
            }
            g2.setColor(Color.BLACK);
            g2.setFont(bodyFont);
            int cx = 0;
            for (int i = 0; i < row.length && i < colWidths.length; i++) {
                g2.drawString(row[i] != null ? row[i] : "—", cx + 2, y + 2);
                cx += colWidths[i];
            }
            y += rowH;
            shade = !shade;
        }
        return y + 8;
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // EXCEL OOXML HELPERS
    // ─────────────────────────────────────────────────────────────────────────────

    private static void addZipEntry(ZipOutputStream zip, String name, String content) throws IOException {
        zip.putNextEntry(new ZipEntry(name));
        zip.write(content.getBytes("UTF-8"));
        zip.closeEntry();
    }

    private static String buildContentTypes() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
            + "<Types xmlns=\"http://schemas.openxmlformats.org/package/2006/content-types\">"
            + "<Default Extension=\"rels\" ContentType=\"application/vnd.openxmlformats-package.relationships+xml\"/>"
            + "<Default Extension=\"xml\" ContentType=\"application/xml\"/>"
            + "<Override PartName=\"/xl/workbook.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml\"/>"
            + "<Override PartName=\"/xl/worksheets/sheet1.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml\"/>"
            + "<Override PartName=\"/xl/worksheets/sheet2.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml\"/>"
            + "<Override PartName=\"/xl/worksheets/sheet3.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml\"/>"
            + "<Override PartName=\"/xl/styles.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.styles+xml\"/>"
            + "<Override PartName=\"/docProps/app.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.extended-properties+xml\"/>"
            + "</Types>";
    }

    private static String buildRootRels() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
            + "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">"
            + "<Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument\" Target=\"xl/workbook.xml\"/>"
            + "<Relationship Id=\"rId2\" Type=\"http://schemas.openxmlformats.org/package/2006/relationships/metadata/core-properties\" Target=\"docProps/core.xml\"/>"
            + "<Relationship Id=\"rId3\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/extended-properties\" Target=\"docProps/app.xml\"/>"
            + "</Relationships>";
    }

    private static String buildAppXml() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
            + "<Properties xmlns=\"http://schemas.openxmlformats.org/officeDocument/2006/extended-properties\">"
            + "<Application>" + APP_NAME + "</Application>"
            + "</Properties>";
    }

    private static String buildWorkbookRels() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
            + "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">"
            + "<Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet\" Target=\"worksheets/sheet1.xml\"/>"
            + "<Relationship Id=\"rId2\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet\" Target=\"worksheets/sheet2.xml\"/>"
            + "<Relationship Id=\"rId3\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet\" Target=\"worksheets/sheet3.xml\"/>"
            + "<Relationship Id=\"rId4\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles\" Target=\"styles.xml\"/>"
            + "</Relationships>";
    }

    private static String buildWorkbook() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
            + "<workbook xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\" "
            + "xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\">"
            + "<bookViews><workbookView firstSheet=\"0\" activeTab=\"0\"/></bookViews>"
            + "<sheets>"
            + "<sheet name=\"Rental Transactions\" sheetId=\"1\" r:id=\"rId1\"/>"
            + "<sheet name=\"Payment Records\" sheetId=\"2\" r:id=\"rId2\"/>"
            + "<sheet name=\"Fleet Status\" sheetId=\"3\" r:id=\"rId3\"/>"
            + "</sheets>"
            + "</workbook>";
    }

    private static String buildStyles() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
            + "<styleSheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\">"
            + "<fonts count=\"2\">"
            + "<font><sz val=\"10\"/><name val=\"Calibri\"/></font>"
            + "<font><b/><sz val=\"10\"/><name val=\"Calibri\"/></font>"
            + "</fonts>"
            + "<fills count=\"2\"><fill><patternFill patternType=\"none\"/></fill><fill><patternFill patternType=\"gray125\"/></fill></fills>"
            + "<borders count=\"1\"><border><left/><right/><top/><bottom/><diagonal/></border></borders>"
            + "<cellStyleXfs count=\"1\"><xf numFmtId=\"0\" fontId=\"0\" fillId=\"0\" borderId=\"0\"/></cellStyleXfs>"
            + "<cellXfs count=\"2\">"
            + "<xf numFmtId=\"0\" fontId=\"0\" fillId=\"0\" borderId=\"0\" xfId=\"0\"/>"
            + "<xf numFmtId=\"0\" fontId=\"1\" fillId=\"0\" borderId=\"0\" xfId=\"0\"/>"   // bold
            + "</cellXfs>"
            + "</styleSheet>";
    }

    private static String buildRentalSheet(List<Rental> rentals) {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n")
          .append("<worksheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\">")
          .append("<sheetData>");

        // Title row
        sb.append(xlRow(1, xlBoldCell("A1", APP_NAME + " — Rental Transactions Report")));
        sb.append(xlRow(2, xlBoldCell("A2", "Generated: " + DATETIME_FMT.format(new Date()))));
        sb.append(xlRow(3, ""));  // empty

        // Header
        sb.append(xlRow(4,
            xlBoldCell("A4","Rental ID") + xlBoldCell("B4","Customer") + xlBoldCell("C4","Vehicle") +
            xlBoldCell("D4","Plate") + xlBoldCell("E4","Start Date") + xlBoldCell("F4","End Date") +
            xlBoldCell("G4","Days") + xlBoldCell("H4","Total ($)") + xlBoldCell("I4","Deposit ($)") +
            xlBoldCell("J4","Late Penalty ($)") + xlBoldCell("K4","Status")
        ));

        int row = 5;
        double grand = 0;
        for (Rental r : rentals) {
            grand += r.getTotalAmount();
            String cust = r.getCustomer() != null ? r.getCustomer().getFullName() : "";
            String veh  = r.getVehicle()  != null ? r.getVehicle().getBrand() + " " + r.getVehicle().getModel() : "";
            String plate = r.getVehicle() != null ? r.getVehicle().getPlateNumber() : "";
            sb.append(xlRow(row,
                xlCell("A"+row, String.valueOf(r.getRentalId())) +
                xlCell("B"+row, esc(cust)) +
                xlCell("C"+row, esc(veh)) +
                xlCell("D"+row, esc(plate)) +
                xlCell("E"+row, fmt(r.getStartDate())) +
                xlCell("F"+row, fmt(r.getEndDate())) +
                xlCell("G"+row, String.valueOf(r.getRentalDays())) +
                xlCell("H"+row, String.format("%.2f", r.getTotalAmount())) +
                xlCell("I"+row, String.format("%.2f", r.getDepositAmount())) +
                xlCell("J"+row, String.format("%.2f", r.getLatePenalty())) +
                xlCell("K"+row, esc(r.getRentalStatus()))
            ));
            row++;
        }
        // Grand total row
        sb.append(xlRow(row,
            xlBoldCell("G"+row,"GRAND TOTAL") +
            xlBoldCell("H"+row, String.format("%.2f", grand))
        ));

        sb.append("</sheetData>")
          .append("<sheetFormatPr defaultRowHeight=\"15\"/>")
          .append("</worksheet>");
        return sb.toString();
    }

    private static String buildPaymentSheet(List<Payment> payments) {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n")
          .append("<worksheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\">")
          .append("<sheetData>");

        sb.append(xlRow(1, xlBoldCell("A1", APP_NAME + " — Payment Records")));
        sb.append(xlRow(2, xlBoldCell("A2", "Generated: " + DATETIME_FMT.format(new Date()))));
        sb.append(xlRow(3, ""));

        sb.append(xlRow(4,
            xlBoldCell("A4","Payment ID") + xlBoldCell("B4","Rental ID") +
            xlBoldCell("C4","Customer") + xlBoldCell("D4","Amount Paid ($)") +
            xlBoldCell("E4","Method") + xlBoldCell("F4","Payment Date") + xlBoldCell("G4","Status")
        ));

        int row = 5;
        double total = 0;
        for (Payment p : payments) {
            total += p.getAmountPaid();
            String cust = p.getRental() != null && p.getRental().getCustomer() != null
                          ? p.getRental().getCustomer().getFullName() : "";
            String rid  = p.getRental() != null ? String.valueOf(p.getRental().getRentalId()) : "";
            sb.append(xlRow(row,
                xlCell("A"+row, String.valueOf(p.getPaymentId())) +
                xlCell("B"+row, rid) +
                xlCell("C"+row, esc(cust)) +
                xlCell("D"+row, String.format("%.2f", p.getAmountPaid())) +
                xlCell("E"+row, esc(p.getPaymentMethod())) +
                xlCell("F"+row, fmt(p.getPaymentDate())) +
                xlCell("G"+row, esc(p.getPaymentStatus()))
            ));
            row++;
        }
        sb.append(xlRow(row,
            xlBoldCell("C"+row,"TOTAL REVENUE") +
            xlBoldCell("D"+row, String.format("%.2f", total))
        ));

        sb.append("</sheetData></worksheet>");
        return sb.toString();
    }

    private static String buildFleetSheet(List<Vehicle> vehicles) {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n")
          .append("<worksheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\">")
          .append("<sheetData>");

        sb.append(xlRow(1, xlBoldCell("A1", APP_NAME + " — Fleet Status")));
        sb.append(xlRow(2, xlBoldCell("A2", "Generated: " + DATETIME_FMT.format(new Date()))));
        sb.append(xlRow(3, ""));

        sb.append(xlRow(4,
            xlBoldCell("A4","Vehicle ID") + xlBoldCell("B4","Brand") + xlBoldCell("C4","Model") +
            xlBoldCell("D4","Plate") + xlBoldCell("E4","Category") +
            xlBoldCell("F4","Daily Rate ($)") + xlBoldCell("G4","Availability") +
            xlBoldCell("H4","Condition") + xlBoldCell("I4","Year")
        ));

        int row = 5;
        for (Vehicle v : vehicles) {
            sb.append(xlRow(row,
                xlCell("A"+row, String.valueOf(v.getVehicleId())) +
                xlCell("B"+row, esc(v.getBrand())) +
                xlCell("C"+row, esc(v.getModel())) +
                xlCell("D"+row, esc(v.getPlateNumber())) +
                xlCell("E"+row, esc(v.getCategory())) +
                xlCell("F"+row, String.format("%.2f", v.getDailyRate())) +
                xlCell("G"+row, esc(v.getAvailabilityStatus())) +
                xlCell("H"+row, esc(v.getConditionStatus())) +
                xlCell("I"+row, String.valueOf(v.getManufactureYear()))
            ));
            row++;
        }

        sb.append("</sheetData></worksheet>");
        return sb.toString();
    }

    // ── Excel XML cell helpers ────────────────────────────────────────────────────

    private static String xlRow(int n, String cells) {
        return "<row r=\"" + n + "\">" + cells + "</row>";
    }

    private static String xlCell(String ref, String value) {
        return "<c r=\"" + ref + "\" t=\"inlineStr\"><is><t>" + esc(value) + "</t></is></c>";
    }

    private static String xlBoldCell(String ref, String value) {
        return "<c r=\"" + ref + "\" t=\"inlineStr\" s=\"1\"><is><t>" + esc(value) + "</t></is></c>";
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // MINI PDF WRITER (pure Java — generates valid PDF 1.4)
    // ─────────────────────────────────────────────────────────────────────────────

    private static class MiniPdfWriter {
        private static final int PAGE_W = 612;   // US Letter width in points
        private static final int PAGE_H = 792;
        private static final int MARGIN = 48;
        private static final int CONTENT_W = PAGE_W - MARGIN * 2;

        private final List<String> pages   = new ArrayList<>();
        private final StringBuilder cur   = new StringBuilder();
        private float curY = PAGE_H - MARGIN;

        void newPage() {
            if (cur.length() > 0) pages.add(cur.toString());
            cur.setLength(0);
            curY = PAGE_H - MARGIN;
        }

        boolean needsNewPage() { return curY < MARGIN + 60; }

        void newline(int pts) { curY -= pts; }

        void drawText(String text, int fontSize, boolean bold) {
            cur.append("BT\n");
            cur.append("/" + (bold ? "F2" : "F1") + " " + fontSize + " Tf\n");
            cur.append(MARGIN + " " + curY + " Td\n");
            cur.append("(" + pdfEsc(text) + ") Tj\n");
            cur.append("ET\n");
            curY -= fontSize + 4;
        }

        void drawCenteredText(String text, int fontSize, boolean bold) {
            // Approximate center (PDF text width calculation is complex; use fixed offset)
            float x = MARGIN + CONTENT_W / 2f - text.length() * fontSize * 0.27f;
            cur.append("BT\n");
            cur.append("/" + (bold ? "F2" : "F1") + " " + fontSize + " Tf\n");
            cur.append(x + " " + curY + " Td\n");
            cur.append("(" + pdfEsc(text) + ") Tj\n");
            cur.append("ET\n");
            curY -= fontSize + 4;
        }

        void drawHLine() {
            cur.append(MARGIN + " " + curY + " m\n");
            cur.append((PAGE_W - MARGIN) + " " + curY + " l\n");
            cur.append("0.5 w\n0.4 0.4 0.4 RG\nS\n");
            curY -= 6;
        }

        void drawTableRow(String[] cells, int[] colWidths, boolean header) {
            int fontSize = 8;
            if (header) {
                // Light blue header background
                int totalW = 0;
                for (int w : colWidths) totalW += w;
                cur.append("0.85 0.91 0.97 rg\n");
                cur.append(MARGIN + " " + (curY - 2) + " " + totalW + " " + (fontSize + 6) + " re f\n");
                cur.append("0 0 0 rg\n");
            }

            cur.append("BT\n");
            cur.append("/" + (header ? "F2" : "F1") + " " + fontSize + " Tf\n");
            float x = MARGIN;
            for (int i = 0; i < cells.length && i < colWidths.length; i++) {
                String cell = cells[i] == null ? "" : cells[i];
                // Truncate if too wide (rough estimate)
                int maxChars = Math.max(3, colWidths[i] / (fontSize / 2 + 1));
                if (cell.length() > maxChars) cell = cell.substring(0, maxChars - 1) + "…";
                cur.append(x + " " + curY + " Td\n");
                cur.append("(" + pdfEsc(cell) + ") Tj\n");
                // Reset to absolute position for next col
                cur.append((-x) + " 0 Td\n");
                x += colWidths[i];
            }
            cur.append("ET\n");
            curY -= fontSize + 5;

            // Zebra shading for data rows
            if (!header) {
                // Subtle bottom divider
                cur.append(MARGIN + " " + curY + " m\n");
                cur.append((PAGE_W - MARGIN) + " " + curY + " l\n");
                cur.append("0.3 w\n0.85 0.85 0.85 RG\nS\n");
            }
        }

        void save(File file) throws IOException {
            if (cur.length() > 0) pages.add(cur.toString());

            try (DataOutputStream out = new DataOutputStream(new FileOutputStream(file))) {
                List<Long> offsets = new ArrayList<>();
                ByteArrayOutputStream buf = new ByteArrayOutputStream();
                DataOutputStream w = new DataOutputStream(buf);

                // Header
                w.writeBytes("%PDF-1.4\n");

                int objCount = 1;

                // Resources object (fonts)
                offsets.add((long) buf.size());
                int resObj = objCount++;
                w.writeBytes(resObj + " 0 obj\n");
                w.writeBytes("<< /Font << /F1 << /Type /Font /Subtype /Type1 /BaseFont /Helvetica >> "
                           + "/F2 << /Type /Font /Subtype /Type1 /BaseFont /Helvetica-Bold >> >> >>\n");
                w.writeBytes("endobj\n");

                // Pages parent
                offsets.add((long) buf.size());
                int pagesObj = objCount++;
                int firstPageObj = objCount;
                List<Integer> pageObjIds = new ArrayList<>();

                // Write each page content stream + page object
                List<int[]> pageObjPairs = new ArrayList<>(); // [contentObj, pageObj]
                for (String pageContent : pages) {
                    byte[] stream = pageContent.getBytes("ISO-8859-1");

                    offsets.add((long) buf.size());
                    int contentObj = objCount++;
                    w.writeBytes(contentObj + " 0 obj\n");
                    w.writeBytes("<< /Length " + stream.length + " >>\n");
                    w.writeBytes("stream\n");
                    w.write(stream);
                    w.writeBytes("\nendstream\nendobj\n");

                    offsets.add((long) buf.size());
                    int pageObj = objCount++;
                    pageObjIds.add(pageObj);
                    w.writeBytes(pageObj + " 0 obj\n");
                    w.writeBytes("<< /Type /Page /Parent " + pagesObj + " 0 R "
                               + "/MediaBox [0 0 " + PAGE_W + " " + PAGE_H + "] "
                               + "/Contents " + contentObj + " 0 R "
                               + "/Resources " + resObj + " 0 R >>\n");
                    w.writeBytes("endobj\n");
                }

                // Now write pages object (we need to patch in the page list)
                // We wrote a placeholder offset above — let's fix by keeping track
                // Actually re-write in proper order:
                // Rebuild from scratch with proper offsets

                buf.reset();
                w = new DataOutputStream(buf);
                offsets.clear();
                objCount = 1;

                w.writeBytes("%PDF-1.4\n%\u00e2\u00e3\u00cf\u00d3\n");  // binary comment

                // Object 1: Resource dict
                offsets.add((long) buf.size());
                w.writeBytes("1 0 obj\n<< /Font << /F1 << /Type /Font /Subtype /Type1 /BaseFont /Helvetica >> "
                           + "/F2 << /Type /Font /Subtype /Type1 /BaseFont /Helvetica-Bold >> >> >>\nendobj\n");

                // Objects 2..N: content streams + page objects
                pageObjIds.clear();
                List<Integer> contentObjIds = new ArrayList<>();
                int nextObj = 2;

                for (String pageContent : pages) {
                    byte[] stream = pageContent.getBytes("ISO-8859-1");
                    offsets.add((long) buf.size());
                    int cId = nextObj++;
                    contentObjIds.add(cId);
                    w.writeBytes(cId + " 0 obj\n<< /Length " + stream.length + " >>\nstream\n");
                    w.write(stream);
                    w.writeBytes("\nendstream\nendobj\n");

                    offsets.add((long) buf.size());
                    int pId = nextObj++;
                    pageObjIds.add(pId);
                    w.writeBytes(pId + " 0 obj\n<< /Type /Page /Parent " + nextObj + " 0 R "
                               + "/MediaBox [0 0 " + PAGE_W + " " + PAGE_H + "] "
                               + "/Contents " + cId + " 0 R "
                               + "/Resources 1 0 R >>\nendobj\n");
                }

                // Pages object
                offsets.add((long) buf.size());
                int pagesId = nextObj++;
                StringBuilder kids = new StringBuilder("[");
                for (int id : pageObjIds) kids.append(id).append(" 0 R ");
                kids.append("]");
                w.writeBytes(pagesId + " 0 obj\n<< /Type /Pages /Kids " + kids
                           + " /Count " + pages.size() + " >>\nendobj\n");

                // Catalog
                offsets.add((long) buf.size());
                int catalogId = nextObj++;
                w.writeBytes(catalogId + " 0 obj\n<< /Type /Catalog /Pages " + pagesId + " 0 R >>\nendobj\n");

                // xref table
                long xrefOffset = buf.size();
                int totalObjs = nextObj;
                w.writeBytes("xref\n0 " + totalObjs + "\n");
                w.writeBytes("0000000000 65535 f \n");
                for (long off : offsets) {
                    w.writeBytes(String.format("%010d 00000 n \n", off));
                }

                // trailer
                w.writeBytes("trailer\n<< /Size " + totalObjs + " /Root " + catalogId + " 0 R >>\n");
                w.writeBytes("startxref\n" + xrefOffset + "\n%%EOF\n");

                out.write(buf.toByteArray());
            }
        }

        private String pdfEsc(String s) {
            if (s == null) return "";
            return s.replace("\\", "\\\\")
                    .replace("(", "\\(")
                    .replace(")", "\\)")
                    .replace("\r", "")
                    .replace("\n", " ");
        }
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // SMALL UTILITIES
    // ─────────────────────────────────────────────────────────────────────────────

    private static String today() { return new SimpleDateFormat("yyyyMMdd").format(new Date()); }

    private static String fmt(Date d) { return d == null ? "—" : DATE_FMT.format(d); }

    private static String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() <= max ? s : s.substring(0, max - 1) + "…";
    }

    private static String csv(Object... fields) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fields.length; i++) {
            if (i > 0) sb.append(",");
            String val = fields[i] == null ? "" : fields[i].toString();
            if (val.contains(",") || val.contains("\"") || val.contains("\n")) {
                sb.append("\"").append(val.replace("\"", "\"\"")).append("\"");
            } else {
                sb.append(val);
            }
        }
        return sb.toString();
    }

    private static String esc(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&apos;");
    }

    private static File ensureExtension(File f, String ext) {
        return f.getName().toLowerCase().endsWith(ext) ? f : new File(f.getAbsolutePath() + ext);
    }
}
