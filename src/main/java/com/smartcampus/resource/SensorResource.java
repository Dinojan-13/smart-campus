package com.smartcampus.resource;

import com.smartcampus.model.Sensor;
import com.smartcampus.store.DataStore;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;

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
}