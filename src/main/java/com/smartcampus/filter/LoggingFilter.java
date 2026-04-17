package com.smartcampus.filter;

import java.io.IOException;
import java.util.logging.Logger;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

/**
 * Logging filter that intercepts every incoming request and outgoing response.
 * Implements both ContainerRequestFilter and ContainerResponseFilter.
 * Registered automatically by Jersey via the @Provider annotation.
 * This is the correct approach for cross-cutting concerns like logging —
 * keeps resource methods clean without repeating Logger.info() everywhere.
 */
@Provider
public class LoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {

    // Standard Java logger — no external dependencies needed
    private static final Logger LOGGER = Logger.getLogger(LoggingFilter.class.getName());

    /**
     * Fires before the request reaches the resource method.
     * Logs the HTTP method and full request URI.
     */
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        LOGGER.info("Incoming Request: ["
                + requestContext.getMethod() + "] "
                + requestContext.getUriInfo().getRequestUri());
    }

    /**
     * Fires after the resource method returns a response.
     * Logs the HTTP status code alongside the original request details.
     */
    @Override
    public void filter(ContainerRequestContext requestContext,
                       ContainerResponseContext responseContext) throws IOException {
        LOGGER.info("Outgoing Response: Status " + responseContext.getStatus()
                + " for [" + requestContext.getMethod() + "] "
                + requestContext.getUriInfo().getRequestUri());
    }
}