package tn.esprit.finalproject.Entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "accommodation")
public class Accommodation {

    @PrimaryKey(autoGenerate = true)
    private long accommodationId;
    private String name;
    private String address;
    private int capacity;
    private double latitude;
    private double longitude;
    private double pricePerNight;
    private String description;

    // Default constructor
    public Accommodation() {
    }

    // Constructor with parameters
    public Accommodation(String name, String address, int capacity, double latitude, double longitude, double pricePerNight, String description) {
        this.name = name;
        this.address = address;
        this.capacity = capacity;
        this.latitude = latitude;
        this.longitude = longitude;
        this.pricePerNight = pricePerNight;
        this.description = description;
    }

    // Getters and Setters
    public long getAccommodationId() {
        return accommodationId;
    }

    public void setAccommodationId(long accommodationId) {
        this.accommodationId = accommodationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {

        this.capacity = capacity;
    }


    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(double pricePerNight) {
        if (pricePerNight < 0) {
            throw new IllegalArgumentException("Le prix par nuit ne peut pas être négatif.");
        }
        this.pricePerNight = pricePerNight;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
