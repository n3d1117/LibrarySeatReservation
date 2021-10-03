package rest;

import auth.JWTHandler;
import auth.Secured;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import controller.UserController;
import model.User;

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
    @Secured
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
    @Secured
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
                    .entity(String.format("Utente con id %s non trovato", id))
                    .build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
            throw new InternalServerErrorException(e.getLocalizedMessage());
        }
    }

    @POST
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(@QueryParam("email") String email, @QueryParam("password") String password) {
        try {
            LOGGER.log(Level.INFO, String.format("Login user with email: %s", email));

            // Authenticate user
            String loggedInUserJson = userController.login(email, password);

            // Generate and add custom JWT token to response
            Gson gson = new Gson();
            JsonElement jsonElement = gson.toJsonTree(gson.fromJson(loggedInUserJson, User.class));
            String jwt = JWTHandler.createToken(email);
            jsonElement.getAsJsonObject().addProperty("jwt", jwt);
            String newJson = gson.toJson(jsonElement);

            return Response
                    .ok(newJson, MediaType.APPLICATION_JSON)
                    .build();
        } catch (EntityNotFoundException e) {
            return Response
                    .status(Response.Status.UNAUTHORIZED)
                    .entity("Credenziali errate")
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
                    .entity(e.getMessage())
                    .build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
            throw new InternalServerErrorException(e.getLocalizedMessage());
        }
    }

    @DELETE
    @Path("/delete/{id}")
    @RolesAllowed("ADMIN")
    @Secured
    public Response delete(@PathParam("id") Long id) {
        try {
            LOGGER.log(Level.INFO, String.format("Deleting user with id %s", id));
            userController.delete(id);
            return Response.ok().build();
        } catch (IllegalArgumentException e) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity(String.format("Utente con id %s non trovato", id))
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
    @Secured
    public Response update(String json) {
        try {
            LOGGER.log(Level.INFO, String.format("Updating user: %s", json));
            userController.update(json);
            return Response.ok().build();
        } catch (EntityNotFoundException e) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity("Utente non trovato")
                    .build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
            throw new InternalServerErrorException(e.getLocalizedMessage());
        }
    }

}
