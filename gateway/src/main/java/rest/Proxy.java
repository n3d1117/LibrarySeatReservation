package rest;

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

    private Response redirect(UriInfo uri, @Context HttpServletRequest request) {
        String redirectUrl = apiUrl + uri.getPath();
        if (request.getQueryString() != null)
            redirectUrl += "?" + request.getQueryString();
        return Response
                .temporaryRedirect(URI.create(redirectUrl))
                .build();
    }
}