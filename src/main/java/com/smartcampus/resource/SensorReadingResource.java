package com.smartcampus.resource;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;
import com.smartcampus.store.DataStore;

/**
 * Sub-resource class handling all reading operations for a specific sensor.
 * Accessed via /api/v1/sensors/{sensorId}/readings
 * This class is instantiated by the sub-resource locator in SensorResource.
 */
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorReadingResource {

    // The sensor ID passed down from the parent SensorResource locator
    private final String sensorId;

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    /**
     * GET /api/v1/sensors/{sensorId}/readings
     * Returns the full reading history for the specified sensor.
     * Returns 404 if the sensor does not exist.
     */
    @GET
    public Response getReadings() {
        // Check sensor exists
        if (!DataStore.sensors.containsKey(sensorId)) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Sensor not found: " + sensorId + "\"}")
                    .build();
        }

        // Return readings list, empty list if no readings yet
        List<SensorReading> history = DataStore.readings.getOrDefault(sensorId, new ArrayList<>());
        return Response.ok(history).build();
    }

    /**
     * POST /api/v1/sensors/{sensorId}/readings
     * Appends a new reading to the sensor's history.
     * Side effect: updates the currentValue on the parent sensor object.
     * Returns 404 if sensor not found.
     * Returns 403 if sensor is under MAINTENANCE.
     */
    @POST
    public Response addReading(SensorReading reading) {
        // Check sensor exists
        if (!DataStore.sensors.containsKey(sensorId)) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Sensor not found: " + sensorId + "\"}")
                    .build();
        }

        Sensor sensor = DataStore.sensors.get(sensorId);

        // Block readings if sensor is under maintenance
        if ("MAINTENANCE".equalsIgnoreCase(sensor.getStatus())) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("{\"error\": \"Sensor " + sensorId + " is under MAINTENANCE and cannot accept new readings.\"}")
                    .build();
        }

        // Auto generate ID if not provided
        if (reading.getId() == null || reading.getId().isEmpty()) {
            reading.setId(UUID.randomUUID().toString());
        }

        // Auto set timestamp if not provided
        if (reading.getTimestamp() == 0) {
            reading.setTimestamp(System.currentTimeMillis());
        }

        // Store the reading in the readings map
        DataStore.readings.computeIfAbsent(sensorId, k -> new ArrayList<>()).add(reading);

        // Side effect: update parent sensor's currentValue to latest reading
        sensor.setCurrentValue(reading.getValue());

        return Response.status(Response.Status.CREATED).entity(reading).build();
    }

    
}