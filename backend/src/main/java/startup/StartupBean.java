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

/**
 * The startup bean, used to populate users, libraries and fake reservations for demo purposes.
 * Also enables the TimescaleDB extension and sets up the hypertable.
 */
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

        // Add users
        List<User> users = new ArrayList<>();
        users.add(createUser("admin@email.com", "Utente", "Admin", "password", true));
        for (int i=0; i<10000; i++) {
            users.add(createUser("user" + i + "@email.com", "Utente", "" + i, "pass", false));
        }
        users.forEach(user -> userDao.save(user));

        // Add libraries
        List<Library> libraries = Arrays.asList(
                createLibrary("Biblioteca Villa Bandini","biblioteca_villa_bandini.png" ,"Via del Paradiso, 5, Firenze", 50),
                createLibrary("Biblioteca Mario Luzi", "biblioteca_mario_luzi.png","Via Ugo Schiff, 8, Firenze", 70),
                createLibrary("Biblioteca delle Oblate", "biblioteca_delle_oblate.png","Via dell’Oriuolo, 24, Firenze", 60),
                createLibrary("Biblioteca Palagio di Parte Guelfa", "biblioteca_palagio_di_parte_guelfa.png","Piazza della Parte Guelfa, Firenze", 30),
                createLibrary("Biblioteca Pietro Thouar", "biblioteca_pietro_thouar.png","Piazza Torquato Tasso 3, Firenze", 40),
                createLibrary("Biblioteca Fabrizio De André", "biblioteca_fabrizio_de_andrè.png","Via delle Carra, 2, Firenze", 70),
                createLibrary("Biblioteca dei ragazzi", "biblioteca_dei_ragazzi.png","Via Tripoli, 34, Firenze", 20),
                createLibrary("Biblioteca Dino Pieraccioni", "biblioteca_dino_pieraccioni.png","Via Nicolodi, 2, Firenze", 60),
                createLibrary("Biblioteca del Galluzzo", "biblioteca_del_galluzzo.png","Via Senese, 206, Firenze", 70),
                createLibrary("BiblioteCaNova Isolotto", "bibliotecanova_isolotto.png","Via Chiusi, 50142 Firenze", 30),
                createLibrary("Biblioteca Filippo Buonarroti", "biblioteca_filippo_buonarroti.png","Viale Guidoni, 188, Firenze", 50),
                createLibrary("Biblioteca Orticoltura", "biblioteca_orticoltura.png","Via Vittorio Emanuele II, 4, Firenze", 40),
                createLibrary("Biblioteca ISIS Leonardo da Vinci", "biblioteca_isis_leonardo_da_vinci.png","Via del Terzolle, 91, Firenze", 60)
        );
        libraries.forEach(library -> libraryDao.save(library));

        // Add reservations
        Random random = new Random();

        int currentMonth = Calendar.getInstance().get(Calendar.MONTH)+1 ; // start from 1
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        // for every library
        for (Library library: libraries) {
            int month = currentMonth;
            int year = currentYear;
            // for every month
            for (int i=0; i<3; i++) {
                // for every day
                for (int j = 1; j< Month.of(month).length(false) + 1; j++) {
                    // for every time slot
                    for (int hour : Arrays.asList(8, 13)) {
                        int fillAmount = random.nextBoolean() ? library.getCapacity() : ThreadLocalRandom.current().nextInt(1, library.getCapacity());
                        // fill reservations
                        for (int z=0; z<fillAmount; z++) {
                            LocalDateTime date = LocalDateTime.of(year, month, j, hour, 0);
                            User randomUser = users.get(ThreadLocalRandom.current().nextInt(1, users.size()-1)); // start from 1 to skip admin
                            reservationDao.saveSkippingCapacityCheck(createReservation(library, randomUser, date));
                        }
                    }
                }
                if (month==12) {
                    year++;
                }
                month = month%12 +1;
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

    private Library createLibrary(String name, String imgFilename, String address, Integer capacity) {
        Library library = ModelFactory.initializeLibrary();
        library.setName(name);
        library.setImgFilename(imgFilename);
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
