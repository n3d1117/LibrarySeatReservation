package rest;

import concurrent_users.ConcurrentUsersSocketHandler;
import config.ConfigProperties;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
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
    @Produces(MediaType.TEXT_PLAIN)
    public Response get(@Context UriInfo uri, @Context HttpServletRequest request) {
        // Only queue on API call specified in configuration file
        if (uri.getPath().matches(apiQueueRegex)) {
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

        // if there are query params in the request, append them
        if (request.getQueryString() != null)
            redirectUrl += "?" + request.getQueryString();

        return Response
                .temporaryRedirect(URI.create(redirectUrl))
                .build();
    }
}
