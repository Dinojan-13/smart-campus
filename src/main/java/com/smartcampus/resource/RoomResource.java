package com.smartcampus.resource;

import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
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
}