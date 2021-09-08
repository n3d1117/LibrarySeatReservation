package startup;

import dao.LibraryDao;
import dao.ReservationDao;
import dao.UserDao;
import model.*;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

@Singleton
@Startup
public class StartupBean {

    @Inject
    private UserDao userDao;

    @Inject
    private LibraryDao libraryDao;

    @Inject
    private ReservationDao reservationDao;

    private static final Logger LOGGER = Logger.getLogger(StartupBean.class.getName());

    @PostConstruct
    @Transactional
    public void init() {

        reservationDao.enableTimescalePostgresExtensionIfNeeded();
        reservationDao.setupHypertable();

        LOGGER.info("Populating database...");

        List<User> users = new ArrayList<>();
        users.add(createUser("admin@email.com", "Admin", "Admin", "password", true));

        for (int i=0; i<10000; i++) {
            users.add(
                    createUser("user" + i + "@email.com", "Utente", "" + i, "pass", false)
            );
        }
        users.forEach(user -> userDao.save(user));

        List<Library> libraries = Arrays.asList(
                createLibrary("Biblioteca Villa Bandini", "Via del Paradiso, 5, Firenze", 50),
                createLibrary("Biblioteca Mario Luzi", "Via Ugo Schiff, 8, Firenze", 70),
                createLibrary("Biblioteca delle Oblate", "Via dell’Oriuolo, 24, Firenze", 60),
                createLibrary("Biblioteca Palagio di Parte Guelfa", "Piazza della Parte Guelfa, Firenze", 30),
                createLibrary("Biblioteca Pietro Thouar", "Piazza Torquato Tasso 3, Firenze", 40),
                createLibrary("Biblioteca Fabrizio De André", "Via delle Carra, 2, Firenze", 70),
                createLibrary("Biblioteca dei ragazzi", "Via Tripoli, 34, Firenze", 20),
                createLibrary("Biblioteca Dino Pieraccioni", "Via Nicolodi, 2, Firenze", 60),
                createLibrary("Biblioteca del Galluzzo", "Via Senese, 206, Firenze", 70),
                createLibrary("BiblioteCaNova Isolotto", "Via Chiusi, 50142 Firenze", 30),
                createLibrary("Biblioteca Filippo Buonarroti", "Viale Guidoni, 188, Firenze", 50),
                createLibrary("Biblioteca Orticoltura", "Via Vittorio Emanuele II, 4, Firenze", 40),
                createLibrary("Biblioteca ISIS Leonardo da Vinci", "Via del Terzolle, 91, Firenze", 60)
        );
        libraries.forEach(library -> libraryDao.save(library));


        Random random = new Random();

        // for every library
        for (Library library: libraries) {
            // for every month (just aug-nov)
            for (int i=8; i<12; i++) {
                // for every day
                for (int j = 1; j< Month.of(i).length(false) + 1; j++) {
                    // for every time slot
                    for (int hour : Arrays.asList(8, 13)) {
                        int fillAmount = random.nextBoolean() ? library.getCapacity() : ThreadLocalRandom.current().nextInt(1, library.getCapacity());
                        // fill reservations
                        for (int z=0; z<fillAmount; z++) {
                            LocalDateTime date = LocalDateTime.of(2021, i, j, hour, 0);
                            User randomUser = users.get(ThreadLocalRandom.current().nextInt(1, users.size()-1));
                            reservationDao.saveSkippingCapacityCheck(createReservation(library, randomUser, date));
                        }
                    }
                }
            }
        }

        LOGGER.info("Finished populating database!");
    }

    private User createUser(String email, String name, String surname, String password, Boolean isAdmin) {
        User user = ModelFactory.initializeUser();
        user.setEmail(email);
        user.setName(name);
        user.setSurname(surname);
        user.setPassword(password);
        user.setRoles(isAdmin ? Arrays.asList(Role.BASIC, Role.ADMIN) : Collections.singletonList(Role.BASIC));
        return user;
    }

    private Library createLibrary(String name, String address, Integer capacity) {
        Library library = ModelFactory.initializeLibrary();
        library.setName(name);
        library.setAddress(address);
        library.setCapacity(capacity);
        return library;
    }

    private Reservation createReservation(Library library, User user, LocalDateTime dateTime) {
        Reservation reservation = ModelFactory.initializeReservation();
        reservation.setLibrary(library);
        reservation.setUser(user);
        reservation.setDatetime(dateTime);
        return reservation;
    }
}
