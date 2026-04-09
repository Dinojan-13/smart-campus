package com.smartcampus.model;

/**
 * Represents a sensor deployed in a Smart Campus room.
 * Sensors can be of various types such as Temperature, CO2, or Occupancy.
 */
public class Sensor {

    // Unique identifier for the sensor e.g. "TEMP-001"
    private String id;

    // Category of the sensor e.g. "Temperature", "CO2", "Occupancy"
    private String type;

    // Current operational state: "ACTIVE", "MAINTENANCE", or "OFFLINE"
    private String status;

    // The most recent measurement recorded by this sensor
    private double currentValue;

    // Foreign key linking this sensor to the room it is installed in
    private String roomId;

    // Default no-arg constructor required by Jackson for JSON deserialization
    public Sensor() {}

    // Constructor for creating a sensor with all fields
    public Sensor(String id, String type, String status, double currentValue, String roomId) {
        this.id = id;
        this.type = type;
        this.status = status;
        this.currentValue = currentValue;
        this.roomId = roomId;
    }

    // --- Getters and Setters ---

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getCurrentValue() { return currentValue; }
    public void setCurrentValue(double currentValue) { this.currentValue = currentValue; }

    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }
}