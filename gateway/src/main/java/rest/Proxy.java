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
import java.util.logging.Logger;

@Path("")
public class Proxy {

    private static final Logger LOGGER = Logger.getLogger(Proxy.class.getName());
    private final String apiUrl;

    public Proxy() throws IOException {
        apiUrl = ConfigProperties.getProperties().getProperty("API_URL");
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
        if (uri.getPath().matches("/libraries/\\d+")) { // todo parametrize
            if (ConcurrentUsersSocketHandler.maxUsersReached()) {
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
