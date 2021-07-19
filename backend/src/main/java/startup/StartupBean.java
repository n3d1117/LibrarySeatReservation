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

        User regularUser = createUser("user@email.com", "User", "User", "pass");
        regularUser.setRoles(Collections.singletonList(Role.BASIC));
        userDao.save(regularUser);

        Library bandino = createLibrary("Biblioteca Villa Bandini", "Via del Paradiso, 5", 50);
        libraryDao.save(bandino);

        Library luzi = createLibrary("Biblioteca Mario Luzi", "Via Ugo Schiff, 8 (ang. via Gabriele D'Annunzio)", 70);
        libraryDao.save(luzi);

        Reservation r = createReservation(bandino, regularUser);
        reservationDao.save(r);

        Reservation r2 = createReservation(luzi, regularUser);
        reservationDao.save(r2);
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

    private Reservation createReservation(Library library, User user) {
        Reservation reservation = ModelFactory.initializeReservation();
        reservation.setLibrary(library);
        reservation.setUser(user);
        reservation.setDatetime(LocalDateTime.of(2021, Month.JULY, 18, 16, 41));
        return reservation;
    }
}
