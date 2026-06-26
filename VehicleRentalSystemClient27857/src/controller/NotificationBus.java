package controller;

import view.NotificationPanel;
import view.NotificationPanel.NotifType;
import java.util.ArrayList;
import java.util.List;

/**
 * NotificationBus — client-side singleton event bus.
 *
 * Any frame (Admin or Customer) registers its NotificationPanel here.
 * Any action anywhere calls NotificationBus.get().push(...) to broadcast
 * a notification to every registered panel in real time.
 *
 * Usage:
 *   // Register (in dashboard constructors):
 *   NotificationBus.get().register(myNotifPanel, "ADMIN");
 *   NotificationBus.get().register(myNotifPanel, "CUSTOMER");
 *
 *   // Fire events (after createRental, returnVehicle, etc.):
 *   NotificationBus.get().rentalCreated(rentalId, customerName, vehicleDesc);
 *   NotificationBus.get().rentalReturned(rentalId, customerName, vehicleDesc, penalty);
 *   NotificationBus.get().rentalUpdated(rentalId, newStatus);
 *   NotificationBus.get().rentalDeleted(rentalId);
 *   NotificationBus.get().paymentReceived(paymentId, amount);
 *   NotificationBus.get().vehicleStatusChanged(vehicleDesc, newStatus);
 */
public class NotificationBus {

    // ── Singleton ─────────────────────────────────────────────────────────────
    private static final NotificationBus INSTANCE = new NotificationBus();
    public static NotificationBus get() { return INSTANCE; }
    private NotificationBus() {}

    // ── Registered panels ─────────────────────────────────────────────────────
    public static final String ROLE_ADMIN    = "ADMIN";
    public static final String ROLE_CUSTOMER = "CUSTOMER";

    private static class PanelEntry {
        final NotificationPanel panel;
        final String role;
        PanelEntry(NotificationPanel p, String r) { panel = p; role = r; }
    }

    private final List<PanelEntry> panels = new ArrayList<>();

    /** Call this in each dashboard constructor after building the NotificationPanel. */
    public synchronized void register(NotificationPanel panel, String role) {
        // Avoid duplicate registrations (e.g. on page rebuild)
        panels.removeIf(e -> e.panel == panel);
        panels.add(new PanelEntry(panel, role));
    }

    /** Call this when a dashboard is closing so its panel is no longer notified. */
    public synchronized void unregister(NotificationPanel panel) {
        panels.removeIf(e -> e.panel == panel);
    }

    // ── Push helpers ──────────────────────────────────────────────────────────

    private synchronized void pushToAdmins(String title, String message, NotifType type) {
        for (PanelEntry e : panels) {
            if (ROLE_ADMIN.equals(e.role)) {
                final NotificationPanel p = e.panel;
                javax.swing.SwingUtilities.invokeLater(() -> p.addNotification(title, message, type));
            }
        }
    }

    private synchronized void pushToCustomer(int customerId, String title, String message, NotifType type) {
        for (PanelEntry e : panels) {
            if (ROLE_CUSTOMER.equals(e.role) && e.panel.getOwnerId() == customerId) {
                final NotificationPanel p = e.panel;
                javax.swing.SwingUtilities.invokeLater(() -> p.addNotification(title, message, type));
            }
        }
    }

    private synchronized void pushToAll(String title, String message, NotifType type) {
        for (PanelEntry e : panels) {
            final NotificationPanel p = e.panel;
            javax.swing.SwingUtilities.invokeLater(() -> p.addNotification(title, message, type));
        }
    }

    // ── Domain events ─────────────────────────────────────────────────────────

    /**
     * Customer created a rental — notify admins and confirm to the customer.
     * @param customerId  the customer's userId (for targeted push)
     * @param rentalId    the new rental ID (-1 if not yet known)
     * @param customerName  display name of the customer
     * @param vehicleDesc   e.g. "Toyota RAV4 (RAB-123)"
     * @param days          number of rental days
     * @param totalAmount   calculated total
     */
    public void rentalCreated(int customerId, int rentalId,
                              String customerName, String vehicleDesc,
                              int days, double totalAmount) {
        String idStr = rentalId > 0 ? " #" + rentalId : "";
        // Admin sees full details
        pushToAdmins(
            "New Rental Created",
            customerName + " rented " + vehicleDesc + " for " + days +
                " day(s)  —  $" + String.format("%.2f", totalAmount),
            NotifType.RENTAL
        );
        // Customer gets a confirmation
        pushToCustomer(customerId,
            "Rental Confirmed" + idStr,
            "Your rental of " + vehicleDesc + " for " + days +
                " day(s) has been confirmed. Total: $" + String.format("%.2f", totalAmount),
            NotifType.RENTAL
        );
    }

    /**
     * Admin processed a vehicle return — notify the customer it is complete.
     */
    public void rentalReturned(int customerId, int rentalId,
                               String customerName, String vehicleDesc,
                               double latePenalty) {
        String pen = latePenalty > 0
            ? String.format("  Late penalty: $%.2f.", latePenalty) : "  No late penalty.";
        pushToAdmins(
            "Vehicle Returned",
            "Rental #" + rentalId + " — " + customerName +
                " returned " + vehicleDesc + "." + pen,
            NotifType.RENTAL
        );
        pushToCustomer(customerId,
            "Rental Completed",
            "Your rental of " + vehicleDesc + " is now closed." + pen,
            NotifType.RENTAL
        );
    }

    /**
     * Admin updated a rental's status (e.g. ONGOING → OVERDUE).
     */
    public void rentalStatusUpdated(int customerId, int rentalId,
                                    String vehicleDesc, String newStatus) {
        pushToAdmins(
            "Rental Updated",
            "Rental #" + rentalId + " — " + vehicleDesc +
                " status changed to " + newStatus + ".",
            NotifType.RENTAL
        );
        pushToCustomer(customerId,
            "Rental Status Update",
            "Your rental of " + vehicleDesc +
                " has been updated to: " + newStatus + ".",
            NotifType.RENTAL
        );
    }

    /**
     * A rental was deleted by admin.
     */
    public void rentalDeleted(int rentalId) {
        pushToAdmins(
            "Rental Deleted",
            "Rental #" + rentalId + " has been removed from the system.",
            NotifType.RENTAL
        );
    }

    /**
     * A payment was confirmed — notify both sides.
     */
    public void paymentReceived(int customerId, int paymentId,
                                String customerName, double amount) {
        pushToAdmins(
            "Payment Received",
            "Payment #" + paymentId + " of $" + String.format("%.2f", amount) +
                " received from " + customerName + ".",
            NotifType.PAYMENT
        );
        pushToCustomer(customerId,
            "Payment Confirmed",
            "Your payment of $" + String.format("%.2f", amount) +
                " (ref #" + paymentId + ") has been confirmed.",
            NotifType.PAYMENT
        );
    }

    /**
     * Admin marked a vehicle overdue — notify both sides.
     */
    public void rentalOverdue(int customerId, int rentalId, String vehicleDesc) {
        pushToAdmins(
            "Overdue Rental Alert",
            "Rental #" + rentalId + " — " + vehicleDesc + " is past its return date.",
            NotifType.ALERT
        );
        pushToCustomer(customerId,
            "Rental Overdue",
            "Your rental of " + vehicleDesc +
                " (#" + rentalId + ") is overdue. Please return the vehicle.",
            NotifType.ALERT
        );
    }

    /**
     * A vehicle's availability changed (maintenance, etc.) — admins only.
     */
    public void vehicleStatusChanged(String vehicleDesc, String newStatus) {
        pushToAdmins(
            "Vehicle Status Changed",
            vehicleDesc + " is now " + newStatus + ".",
            NotifType.VEHICLE
        );
    }

    /**
     * Generic system broadcast to all connected panels.
     */
    public void systemMessage(String title, String message) {
        pushToAll(title, message, NotifType.SYSTEM);
    }
}