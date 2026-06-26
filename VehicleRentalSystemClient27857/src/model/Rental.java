package model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name = "rentals")
public class Rental implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int rentalId;

    @Temporal(TemporalType.DATE)
    private Date startDate;

    @Temporal(TemporalType.DATE)
    private Date endDate;

    @Temporal(TemporalType.DATE)
    private Date actualReturnDate;

    private int rentalDays;
    private double totalAmount;
    private double depositAmount;
    private double latePenalty;
    private String rentalStatus;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private User customer;

    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @OneToOne(mappedBy = "rental", cascade = CascadeType.ALL)
    private Payment payment;

    public Rental() {
    }

    public Rental(int rentalId, Date startDate, Date endDate, Date actualReturnDate, int rentalDays, double totalAmount, double depositAmount, double latePenalty, String rentalStatus, User customer, Vehicle vehicle) {
        this.rentalId = rentalId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.actualReturnDate = actualReturnDate;
        this.rentalDays = rentalDays;
        this.totalAmount = totalAmount;
        this.depositAmount = depositAmount;
        this.latePenalty = latePenalty;
        this.rentalStatus = rentalStatus;
        this.customer = customer;
        this.vehicle = vehicle;
    }

    public int getRentalId() {
        return rentalId;
    }

    public void setRentalId(int rentalId) {
        this.rentalId = rentalId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getActualReturnDate() {
        return actualReturnDate;
    }

    public void setActualReturnDate(Date actualReturnDate) {
        this.actualReturnDate = actualReturnDate;
    }

    public int getRentalDays() {
        return rentalDays;
    }

    public void setRentalDays(int rentalDays) {
        this.rentalDays = rentalDays;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public double getDepositAmount() {
        return depositAmount;
    }

    public void setDepositAmount(double depositAmount) {
        this.depositAmount = depositAmount;
    }

    public double getLatePenalty() {
        return latePenalty;
    }

    public void setLatePenalty(double latePenalty) {
        this.latePenalty = latePenalty;
    }

    public String getRentalStatus() {
        return rentalStatus;
    }

    public void setRentalStatus(String rentalStatus) {
        this.rentalStatus = rentalStatus;
    }

    public User getCustomer() {
        return customer;
    }

    public void setCustomer(User customer) {
        this.customer = customer;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }
}