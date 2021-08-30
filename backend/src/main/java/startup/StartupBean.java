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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Singleton
@Startup
public class StartupBean {

    @Inject
    private UserDao userDao;

    @Inject
    private LibraryDao libraryDao;

    @Inject
    private ReservationDao reservationDao;

    @PostConstruct
    @Transactional
    public void init() {

        reservationDao.enableTimescalePostgresExtensionIfNeeded();
        reservationDao.setupHypertable();

        User adminUser = createUser("admin@email.com", "Admin", "Admin", "password");
        adminUser.setRoles(Arrays.asList(Role.BASIC, Role.ADMIN));
        userDao.save(adminUser);

        User regularUser = createUser("user@email.com", "Nome", "Cognome", "pass");
        regularUser.setRoles(Collections.singletonList(Role.BASIC));
        userDao.save(regularUser);

        List<Library> libraries = Arrays.asList(
                createLibrary("Biblioteca Villa Bandini", "Via del Paradiso, 5, Firenze", 50),
                createLibrary("Biblioteca Mario Luzi", "Via Ugo Schiff, 8, Firenze", 70),
                createLibrary("Biblioteca Mario Rossi", "Via delle Molina, 7, San Mauro a Signa", 10),
                createLibrary("Biblioteca Mario Rossi", "Via delle Molina, 7, San Mauro a Signa", 10),
                createLibrary("Biblioteca Mario Rossi", "Via delle Molina, 7, San Mauro a Signa", 10),
                createLibrary("Biblioteca Mario Rossi", "Via delle Molina, 7, San Mauro a Signa", 10),
                createLibrary("Biblioteca Mario Rossi", "Via delle Molina, 7, San Mauro a Signa", 10),
                createLibrary("Biblioteca Mario Rossi", "Via delle Molina, 7, San Mauro a Signa", 10),
                createLibrary("Biblioteca Mario Luzi", "Via Ugo Schiff, 8, Firenze", 70)
        );
        libraries.forEach(library -> libraryDao.save(library));


        Random random = new Random();

        // for every library
        for (Library library: libraries) {
            // for every month (just aug-oct)
            for (int i=8; i<11; i++) {
                // for every day
                for (int j = 1; j< Month.of(i).length(false) + 1; j++) {
                    // for every time slot
                    for (int hour : Arrays.asList(8, 13)) {
                        int fillAmount = random.nextBoolean() ? library.getCapacity() : ThreadLocalRandom.current().nextInt(1, library.getCapacity());
                        // fill reservations
                        for (int z=0; z<fillAmount; z++) {
                            LocalDateTime date = LocalDateTime.of(2021, i, j, hour, 0);
                            reservationDao.saveSkippingCapacityCheck(createReservation(library, regularUser, date));
                        }
                    }
                }
            }
        }
    }

    private User createUser(String email, String name, String surname, String password) {
        User user = ModelFactory.initializeUser();
        user.setEmail(email);
        user.setName(name);
        user.setSurname(surname);
        user.setPassword(password);
        user.setRoles(Arrays.asList(Role.BASIC, Role.ADMIN));
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
