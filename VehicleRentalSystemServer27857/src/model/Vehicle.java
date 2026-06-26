package model;

import java.io.Serializable;
import java.util.List;
import javax.persistence.*;

@Entity
@Table(name = "vehicles")
public class Vehicle implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int vehicleId;

    private String plateNumber;
    private String brand;
    private String model;
    private String category;
    private double dailyRate;
    private String availabilityStatus;
    private String conditionStatus;
    private int manufactureYear;

    @OneToMany(mappedBy = "vehicle")
    private List<Rental> rentals;

    @ManyToMany
    @JoinTable(
            name = "vehicle_discount",
            joinColumns = @JoinColumn(name = "vehicle_id"),
            inverseJoinColumns = @JoinColumn(name = "discount_id")
    )
    private List<Discount> discounts;

    public Vehicle() {
    }

    public Vehicle(int vehicleId, String plateNumber, String brand, String model, String category, double dailyRate, String availabilityStatus, String conditionStatus, int manufactureYear) {
        this.vehicleId = vehicleId;
        this.plateNumber = plateNumber;
        this.brand = brand;
        this.model = model;
        this.category = category;
        this.dailyRate = dailyRate;
        this.availabilityStatus = availabilityStatus;
        this.conditionStatus = conditionStatus;
        this.manufactureYear = manufactureYear;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getDailyRate() {
        return dailyRate;
    }

    public void setDailyRate(double dailyRate) {
        this.dailyRate = dailyRate;
    }

    public String getAvailabilityStatus() {
        return availabilityStatus;
    }

    public void setAvailabilityStatus(String availabilityStatus) {
        this.availabilityStatus = availabilityStatus;
    }

    public String getConditionStatus() {
        return conditionStatus;
    }

    public void setConditionStatus(String conditionStatus) {
        this.conditionStatus = conditionStatus;
    }

    public int getManufactureYear() {
        return manufactureYear;
    }

    public void setManufactureYear(int manufactureYear) {
        this.manufactureYear = manufactureYear;
    }

    public List<Rental> getRentals() {
        return rentals;
    }

    public void setRentals(List<Rental> rentals) {
        this.rentals = rentals;
    }

    public List<Discount> getDiscounts() {
        return discounts;
    }

    public void setDiscounts(List<Discount> discounts) {
        this.discounts = discounts;
    }
}