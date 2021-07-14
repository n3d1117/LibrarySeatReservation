package rest;

import controller.LibraryController;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("/libraries")
public class LibraryRestServices {

    private static final Logger LOGGER = Logger.getLogger(LibraryRestServices.class.getName());

    @Inject
    LibraryController libraryController;

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response list() {
        try {
            LOGGER.log(Level.INFO, "Listing all libraries...");
            String librariesJson = libraryController.all();
            return Response
                    .ok(librariesJson, MediaType.APPLICATION_JSON)
                    .build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
            throw new InternalServerErrorException(e.getLocalizedMessage());
        }
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listById(@PathParam("id") Long id) {
        try {
            LOGGER.log(Level.INFO, String.format("Listing library with id %s", id));
            String libraryJson = libraryController.find(id);
            return Response
                    .ok(libraryJson, MediaType.APPLICATION_JSON)
                    .build();
        } catch (EntityNotFoundException e) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity(String.format("Biblioteca con id %s non trovata", id))
                    .build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
            throw new InternalServerErrorException(e.getLocalizedMessage());
        }
    }

    @POST
    @Path("/add")
    @RolesAllowed("ADMIN")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response add(String json) {
        try {
            LOGGER.log(Level.INFO, String.format("Adding new library: %s", json));
            String addedLibraryJson = libraryController.add(json);
            return Response
                    .ok(addedLibraryJson, MediaType.APPLICATION_JSON)
                    .build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
            throw new InternalServerErrorException(e.getLocalizedMessage());
        }
    }

    @DELETE
    @Path("/delete/{id}")
    @RolesAllowed("ADMIN")
    public Response delete(@PathParam("id") Long id) {
        try {
            LOGGER.log(Level.INFO, String.format("Deleting library with id %s", id));
            libraryController.delete(id);
            return Response.ok().build();
        } catch (IllegalArgumentException e) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity(String.format("Biblioteca con id %s non trovata", id))
                    .build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
            throw new InternalServerErrorException(e.getLocalizedMessage());
        }
    }

    @PUT
    @Path("/update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("ADMIN")
    public Response update(String json) {
        try {
            LOGGER.log(Level.INFO, String.format("Updating library: %s", json));
            libraryController.update(json);
            return Response.ok().build();
        } catch (EntityNotFoundException e) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity("Biblioteca non trovata")
                    .build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
            throw new InternalServerErrorException(e.getLocalizedMessage());
        }
    }

}
