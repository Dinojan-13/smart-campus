package com.smartcampus.store;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.smartcampus.model.Room;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;

/**
 * Central in-memory data store for the Smart Campus API.
 * Uses static ConcurrentHashMap so all resource instances share the same data.
 * ConcurrentHashMap is used instead of HashMap to handle concurrent requests safely.
 */
public class DataStore {

    // Stores all rooms keyed by room ID
    public static final Map<String, Room> rooms = new ConcurrentHashMap<>();

    // Stores all sensors keyed by sensor ID
    public static final Map<String, Sensor> sensors = new ConcurrentHashMap<>();

    // Stores sensor reading history keyed by sensor ID
    // Each sensor ID maps to a list of its historical readings
    public static final Map<String, List<SensorReading>> readings = new ConcurrentHashMap<>();
}