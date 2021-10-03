package rest;

import concurrent_users.ConcurrentUsersSocketHandler;
import config.ConfigProperties;
import queue.QueueSocketHandler;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.net.URI;

@Path("")
public class Proxy {

    private final String apiUrl;
    private final String apiQueueRegex;

    public Proxy() throws IOException {
        apiUrl = ConfigProperties.getProperties().getProperty("API_URL");
        apiQueueRegex = ConfigProperties.getProperties().getProperty("API_QUEUE_REGEX");
    }

    @POST
    @Path("/{s:.*}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response post(@Context UriInfo uri, @Context HttpServletRequest request) {
        return redirect(uri, request);
    }

    @GET
    @Path("/{s:.*}")
    public Response get(@Context UriInfo uri, @Context HttpServletRequest request) {

        // Only queue on API call specified in configuration file
        if (uri.getPath().matches(apiQueueRegex)) {

            // Check if max queue size is reached
            if (QueueSocketHandler.maxQueueSizeReached()) {
                // Returns HTTP 503: Service Unavailable
                return Response
                        .status(Response.Status.SERVICE_UNAVAILABLE)
                        .build();
            }
            // Check if max concurrent users number is reached
            if (ConcurrentUsersSocketHandler.maxUsersReached()) {
                // Returns HTTP 429: Too Many Requests
                return Response
                        .status(Response.Status.TOO_MANY_REQUESTS)
                        .build();
            }
        }
        return redirect(uri, request);
    }

    @PUT
    @Path("/{s:.*}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response put(@Context UriInfo uri, @Context HttpServletRequest request) {
        return redirect(uri, request);
    }

    @DELETE
    @Path("/{s:.*}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response delete(@Context UriInfo uri, @Context HttpServletRequest request) {
        return redirect(uri, request);
    }

    /**
     * Redirects the specified request with the same path and parameters to a different URL
     *
     * @param uri     the UriInfo object containing the request path
     * @param request the request object containing full query string
     * @return a HTTP 307 Response (Temporary Redirect) object pointing to the real API url
     */
    private Response redirect(UriInfo uri, HttpServletRequest request) {
        String redirectUrl = apiUrl + uri.getPath();

        // If there are query params in the request, append them
        if (request.getQueryString() != null)
            redirectUrl += "?" + request.getQueryString();

        // Apparently you can't set custom HTTP headers for a redirect (https://stackoverflow.com/a/41218304)
        // So we append the JWT token, if any, directly to the redirect url's query string
        if (request.getHeader("Authorization") != null) {
            String token = request.getHeader("Authorization").substring("Bearer".length()).trim();
            redirectUrl = UriBuilder.fromUri(redirectUrl).queryParam("jwt", token).build().toString();
        }

        return Response
                .temporaryRedirect(URI.create(redirectUrl))
                .build();
    }
}
