package rest;

import controller.UserController;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("/users")
public class UserRestServices {

    private static final Logger LOGGER = Logger.getLogger(UserRestServices.class.getName());

    @Inject
    UserController userController;

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("ADMIN")
    public Response list() {
        try {
            LOGGER.log(Level.INFO, "Listing all users...");
            String usersJson = userController.all();
            return Response
                    .ok(usersJson, MediaType.APPLICATION_JSON)
                    .build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
            throw new InternalServerErrorException(e.getLocalizedMessage());
        }
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("ADMIN")
    public Response listById(@PathParam("id") Long id) {
        try {
            LOGGER.log(Level.INFO, String.format("Listing user with id %s", id));
            String userJson = userController.find(id);
            return Response
                    .ok(userJson, MediaType.APPLICATION_JSON)
                    .build();
        } catch (EntityNotFoundException e) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity(String.format("User with id %s not found", id))
                    .build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
            throw new InternalServerErrorException(e.getLocalizedMessage());
        }
    }

    @POST
    @Path("/signup")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response signup(String json) {
        try {
            LOGGER.log(Level.INFO, String.format("Signing up new user: %s", json));
            String addedUserJson = userController.add(json);
            return Response
                    .ok(addedUserJson, MediaType.APPLICATION_JSON)
                    .build();
        } catch (PersistenceException e) {
            return Response
                    .status(Response.Status.NOT_ACCEPTABLE)
                    .entity("User with specified username already exists")
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
            LOGGER.log(Level.INFO, String.format("Deleting user with id %s", id));
            userController.delete(id);
            return Response.ok().build();
        } catch (IllegalArgumentException e) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity(String.format("User with id %s not found", id))
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
            LOGGER.log(Level.INFO, String.format("Updating user: %s", json));
            userController.update(json);
            return Response.ok().build();
        } catch (EntityNotFoundException e) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity("Specified user not found")
                    .build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
            throw new InternalServerErrorException(e.getLocalizedMessage());
        }
    }

}
