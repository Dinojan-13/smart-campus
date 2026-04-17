package com.smartcampus.exception;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Global catch-all exception mapper for any unexpected runtime errors.
 * Intercepts NullPointerException, IndexOutOfBoundsException and any other
 * unhandled Throwable — returns HTTP 500 with a safe generic message.
 * This prevents raw Java stack traces from being exposed to API consumers.
 */
@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable e) {
        Map<String, String> error = new HashMap<>();
        error.put("status", "500 Internal Server Error");
        error.put("error", "Unexpected Server Error");
        error.put("message", "An internal error occurred. Please contact the administrator.");
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .type(MediaType.APPLICATION_JSON)
                .entity(error)
                .build();
    }
}