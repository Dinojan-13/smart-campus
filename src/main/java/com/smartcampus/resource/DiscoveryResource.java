package com.smartcampus.resource;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Root discovery endpoint for the Smart Campus API.
 * Returns API metadata and links to primary resource collections.
 * This follows the HATEOAS principle — clients can navigate the API from this entry point.
 */
@Path("/")
public class DiscoveryResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response discover() {

        // Build the main response body
        Map<String, Object> response = new HashMap<>();
        response.put("api", "Smart Campus API");
        response.put("version", "v1");
        response.put("contact", "admin@smartcampus.ac.lk");
        response.put("description", "Sensor and Room Management API for the Smart Campus initiative");

        // Links to primary resource collections (HATEOAS navigation)
        Map<String, String> links = new HashMap<>();
        links.put("rooms", "/api/v1/rooms");
        links.put("sensors", "/api/v1/sensors");
        response.put("resources", links);

        return Response.ok(response).build();
    }
}