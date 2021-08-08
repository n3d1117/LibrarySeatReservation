package rest;

import controller.ReservationController;

import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.Month;
import java.time.Year;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("/reservations")
public class ReservationRestServices {

    private static final Logger LOGGER = Logger.getLogger(ReservationRestServices.class.getName());

    @Inject
    ReservationController reservationController;

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
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
    public Response listById(@PathParam("id") Long id) {
        try {
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
    public Response listByUserId(@PathParam("id") Long id) {
        try {
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
    @Path("/library/{id}/{year}/{month}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listByLibraryIdAndDate(@PathParam("id") Long id, @PathParam("month") Integer month, @PathParam("year") Integer year) {
        try {
            LOGGER.log(Level.INFO, String.format("Listing reservations for library with id %s for %s/%s", id, month, year));
            String reservationsJson = reservationController.findByLibraryAndDate(id, Month.of(month), Year.of(year));
            return Response
                    .ok(reservationsJson, MediaType.APPLICATION_JSON)
                    .build();
        } catch (EntityNotFoundException e) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity(String.format("Prenotazioni per biblioteca con id %s per %s/%s non trovate", id, month, year))
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
    public Response add(String json) {
        try {
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
    public Response delete(@PathParam("id") Long id) {
        try {
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
}
