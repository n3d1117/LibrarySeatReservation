package rest;

import auth.GatewayAuthorizationRequired;
import auth.Secured;
import com.google.gson.Gson;
import controller.ReservationController;
import dao.ReservationDao;
import dao.UserDao;
import dto.ReservationDto;
import model.Role;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceException;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("/reservations")
public class ReservationRestServices {

    private static final Logger LOGGER = Logger.getLogger(ReservationRestServices.class.getName());

    @Inject
    UserDao userDao;

    @Inject
    ReservationDao reservationDao;

    @Inject
    ReservationController reservationController;

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    public Response list() {
        try {
            LOGGER.log(Level.INFO, "Listing all reservations...");
            String reservationsJson = reservationController.all();
            return Response
                    .ok(reservationsJson, MediaType.APPLICATION_JSON)
                    .build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
            throw new InternalServerErrorException(e.getLocalizedMessage());
        }
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    public Response listById(@PathParam("id") Long id, @Context SecurityContext securityContext) {
        try {

            // Make sure a user can request only his/her own reservations
            if (!securityContext.isUserInRole(Role.ADMIN.toString())) {
                String email = securityContext.getUserPrincipal().getName();
                if (!reservationDao.findById(id).getUserEmail().equals(email)) {
                    return unauthorized();
                }
            }

            LOGGER.log(Level.INFO, String.format("Listing reservation with id %s", id));
            String reservationJson = reservationController.find(id);
            return Response
                    .ok(reservationJson, MediaType.APPLICATION_JSON)
                    .build();
        } catch (EntityNotFoundException e) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity(String.format("Prenotazione con id %s non trovata", id))
                    .build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
            throw new InternalServerErrorException(e.getLocalizedMessage());
        }
    }

    @GET
    @Path("/user/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    public Response listByUserId(@PathParam("id") Long id, @Context SecurityContext securityContext) {
        try {

            // Make sure a user can request only his/her own reservations
            if (!securityContext.isUserInRole(Role.ADMIN.toString())) {
                String email = securityContext.getUserPrincipal().getName();
                if (!userDao.findById(id).getEmail().equals(email)) {
                    return unauthorized();
                }
            }

            LOGGER.log(Level.INFO, String.format("Listing reservations for user with id %s", id));
            String reservationsJson = reservationController.findByUser(id);
            return Response
                    .ok(reservationsJson, MediaType.APPLICATION_JSON)
                    .build();
        } catch (EntityNotFoundException e) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity(String.format("Prenotazioni per utente con id %s non trovate", id))
                    .build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
            throw new InternalServerErrorException(e.getLocalizedMessage());
        }
    }

    @GET
    @Path("/library/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @RolesAllowed("ADMIN")
    public Response listByLibraryId(@PathParam("id") Long id) {
        try {
            LOGGER.log(Level.INFO, String.format("Listing reservations for library with id %s", id));
            String reservationsJson = reservationController.findByLibrary(id);
            return Response
                    .ok(reservationsJson, MediaType.APPLICATION_JSON)
                    .build();
        } catch (EntityNotFoundException e) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity(String.format("Prenotazioni per biblioteca con id %s non trovate", id))
                    .build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
            throw new InternalServerErrorException(e.getLocalizedMessage());
        }
    }

    @GET
    @Path("/library/{id}/{year}/{month}/{day}")
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    public Response listByLibraryIdAndDate(
            @PathParam("id") Long id,
            @PathParam("year") Integer year,
            @PathParam("month") Integer month,
            @PathParam("day") Integer day
    ) {
        try {
            LOGGER.log(Level.INFO, String.format("Listing reservations for library with id %s for %s/%s/%s", id, year, month, day));
            String reservationsJson = reservationController.findByLibraryAndDate(id, year, month, day);
            return Response
                    .ok(reservationsJson, MediaType.APPLICATION_JSON)
                    .build();
        } catch (EntityNotFoundException e) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity(String.format("Prenotazioni per biblioteca con id %s per %s/%s/%s non trovate", id, year, month, day))
                    .build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
            throw new InternalServerErrorException(e.getLocalizedMessage());
        }
    }

    @GET
    @Path("/stats/library/{id}/{year}/{month}/")
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    public Response listAggregationsByLibraryIdAndMonth(
            @PathParam("id") Long id,
            @PathParam("year") Integer year,
            @PathParam("month") Integer month
    ) {
        try {
            LOGGER.log(Level.INFO, String.format("Listing aggregate reservations for library with id %s for %s/%s", id, year, month));
            String reservationsAggregateJson = reservationController.dailyAggregateByMonth(id, year, month);
            return Response
                    .ok(reservationsAggregateJson, MediaType.APPLICATION_JSON)
                    .build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
            throw new InternalServerErrorException(e.getLocalizedMessage());
        }
    }

    @POST
    @Path("/add")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @GatewayAuthorizationRequired
    public Response add(String json, @Context SecurityContext securityContext) {
        try {

            // Make sure a user can't add reservations on other users' behalf
            if (!securityContext.isUserInRole(Role.ADMIN.toString())) {
                ReservationDto reservationDto = new Gson().fromJson(json, ReservationDto.class);
                String email = securityContext.getUserPrincipal().getName();
                if (!userDao.findByEmail(email).getId().equals(reservationDto.getUserId())) {
                    return unauthorized();
                }
            }

            LOGGER.log(Level.INFO, String.format("Adding new reservation: %s", json));
            String addedReservationJson = reservationController.add(json);
            return Response
                    .ok(addedReservationJson, MediaType.APPLICATION_JSON)
                    .build();
        } catch (EntityNotFoundException e) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity("Utente o libreria non esistenti")
                    .build();
        } catch (PersistenceException e) {
            return Response
                    .status(Response.Status.NOT_ACCEPTABLE)
                    .entity(e.getLocalizedMessage())
                    .build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
            throw new InternalServerErrorException(e.getLocalizedMessage());
        }
    }

    @DELETE
    @Path("/delete/{id}")
    @Secured
    public Response delete(@PathParam("id") Long id, @Context SecurityContext securityContext) {
        try {

            // Make sure a user can delete only his/her own reservations
            if (!securityContext.isUserInRole(Role.ADMIN.toString())) {
                String email = securityContext.getUserPrincipal().getName();
                if (!reservationDao.findById(id).getUserEmail().equals(email)) {
                    return unauthorized();
                }
            }

            LOGGER.log(Level.INFO, String.format("Deleting reservation with id %s", id));
            reservationController.delete(id);
            return Response.ok().build();
        } catch (IllegalArgumentException e) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity(String.format("Prenotazione con id %s non trovata", id))
                    .build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
            throw new InternalServerErrorException(e.getLocalizedMessage());
        }
    }

    /**
     * @return HTTP 401 Unauthorized Response
     */
    private Response unauthorized() {
        return Response
                .status(Response.Status.UNAUTHORIZED)
                .entity("Unauthorized")
                .type("text/plain")
                .build();
    }
}
