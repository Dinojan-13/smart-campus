package com.smartcampus.resource;

import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.smartcampus.model.Room;
import com.smartcampus.store.DataStore;

/**
 * Handles all HTTP requests for the /api/v1/rooms endpoint.
 * Supports listing, creating, fetching and deleting rooms.
 */
@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {

    /**
     * GET /api/v1/rooms
     * Returns a list of all rooms currently in the system.
     */
    @GET
    public Response getAllRooms() {
        Collection<Room> rooms = DataStore.rooms.values();
        return Response.ok(rooms).build();
    }

    /**
 * POST /api/v1/rooms
 * Creates a new room and adds it to the in-memory store.
 * Returns 400 if ID is missing, 409 if room already exists.
 */
@POST
public Response createRoom(Room room) {
    // Validate that room ID is provided
    if (room.getId() == null || room.getId().isEmpty()) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\": \"Room ID is required\"}")
                .build();
    }

    // Check if room with same ID already exists
    if (DataStore.rooms.containsKey(room.getId())) {
        return Response.status(Response.Status.CONFLICT)
                .entity("{\"error\": \"Room with this ID already exists\"}")
                .build();
    }

    // Store the new room
    DataStore.rooms.put(room.getId(), room);
    return Response.status(Response.Status.CREATED).entity(room).build();
}

/**
 * GET /api/v1/rooms/{roomId}
 * Returns a single room by its ID.
 * Returns 404 if the room does not exist.
 */
@GET
@Path("/{roomId}")
public Response getRoomById(@PathParam("roomId") String roomId) {
    // Look up room in the store
    Room room = DataStore.rooms.get(roomId);

    // Return 404 if not found
    if (room == null) {
        return Response.status(Response.Status.NOT_FOUND)
                .entity("{\"error\": \"Room not found: " + roomId + "\"}")
                .build();
    }

    return Response.ok(room).build();
}
}