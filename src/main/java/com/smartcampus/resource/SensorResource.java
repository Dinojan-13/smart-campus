package com.smartcampus.resource;

import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.smartcampus.model.Sensor;
import com.smartcampus.store.DataStore;

/**
 * Handles all HTTP requests for the /api/v1/sensors endpoint.
 * Supports listing, filtering, creating and fetching sensors.
 */
@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {

    /**
     * GET /api/v1/sensors
     * Returns a list of all sensors currently in the system.
     */
    @GET
    public Response getAllSensors() {
        Collection<Sensor> sensors = DataStore.sensors.values();
        return Response.ok(sensors).build();
    }

    /**
     * POST /api/v1/sensors
     * Registers a new sensor in the system.
     * Validates that the referenced roomId actually exists before saving.
     * Returns 400 if sensor ID is missing.
     * Returns 409 if sensor already exists.
     * Returns 422 if the referenced room does not exist.
     */
    @POST
    public Response createSensor(Sensor sensor) {
        // Validate sensor ID is provided
        if (sensor.getId() == null || sensor.getId().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Sensor ID is required\"}")
                    .build();
        }

        // Check if sensor with same ID already exists
        if (DataStore.sensors.containsKey(sensor.getId())) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("{\"error\": \"Sensor with this ID already exists\"}")
                    .build();
        }

        // Validate that the referenced room exists
        // Cannot register a sensor without a valid room
        if (sensor.getRoomId() == null || !DataStore.rooms.containsKey(sensor.getRoomId())) {
            return Response.status(422)
                    .entity("{\"error\": \"Room not found: " + sensor.getRoomId() + ". Cannot register sensor without a valid room.\"}")
                    .build();
        }

        // Link sensor ID to the room's sensor list
        DataStore.rooms.get(sensor.getRoomId()).getSensorIds().add(sensor.getId());

        // Save sensor to store
        DataStore.sensors.put(sensor.getId(), sensor);
        return Response.status(Response.Status.CREATED).entity(sensor).build();
    }

    /**
 * GET /api/v1/sensors?type=CO2
 * Returns all sensors, optionally filtered by type.
 * If type query param is provided, only matching sensors are returned.
 * Uses @QueryParam which is the correct REST approach for filtering collections.
 */
@GET
@Path("/search")
public Response getSensorsByType(@QueryParam("type") String type) {
    Collection<Sensor> allSensors = DataStore.sensors.values();

    // If no type filter provided, return all sensors
    if (type == null || type.isEmpty()) {
        return Response.ok(allSensors).build();
    }

    // Filter sensors by type case-insensitively
    java.util.List<Sensor> filtered = allSensors.stream()
            .filter(s -> s.getType().equalsIgnoreCase(type))
            .collect(java.util.stream.Collectors.toList());

    return Response.ok(filtered).build();
}

/**
 * GET /api/v1/sensors/{sensorId}
 * Returns a single sensor by its ID.
 * Returns 404 if sensor does not exist.
 */
@GET
@Path("/{sensorId}")
public Response getSensorById(@PathParam("sensorId") String sensorId) {
    // Look up sensor in the store
    Sensor sensor = DataStore.sensors.get(sensorId);

    // Return 404 if not found
    if (sensor == null) {
        return Response.status(Response.Status.NOT_FOUND)
                .entity("{\"error\": \"Sensor not found: " + sensorId + "\"}")
                .build();
    }

    return Response.ok(sensor).build();
}

/**
 * Sub-resource locator for /api/v1/sensors/{sensorId}/readings
 * Delegates all reading-related requests to SensorReadingResource.
 * This pattern keeps the SensorResource clean and focused.
 */
@Path("/{sensorId}/readings")
public SensorReadingResource getReadingResource(@PathParam("sensorId") String sensorId) {
    return new SensorReadingResource(sensorId);
}

}