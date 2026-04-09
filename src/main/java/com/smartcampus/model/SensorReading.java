package com.smartcampus.model;

/**
 * Represents a single historical reading captured by a sensor.
 * Each reading is a snapshot of the sensor value at a specific point in time.
 */
public class SensorReading {

    // Unique identifier for this reading event, UUID recommended
    private String id;

    // Epoch time in milliseconds when this reading was captured
    private long timestamp;

    // The actual metric value recorded by the sensor hardware
    private double value;

    // Default no-arg constructor required by Jackson for JSON deserialization
    public SensorReading() {}

    // Constructor for creating a reading with all fields
    public SensorReading(String id, long timestamp, double value) {
        this.id = id;
        this.timestamp = timestamp;
        this.value = value;
    }

    // --- Getters and Setters ---

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public double getValue() { return value; }
    public void setValue(double value) { this.value = value; }
}