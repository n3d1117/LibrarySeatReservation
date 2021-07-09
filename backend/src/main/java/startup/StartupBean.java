package startup;

import dao.LibraryDao;
import dao.UserDao;
import model.Library;
import model.ModelFactory;
import model.Role;
import model.User;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.Collections;

@Singleton
@Startup
public class StartupBean {

    @Inject
    private UserDao userDao;

    @Inject
    private LibraryDao libraryDao;

    @PostConstruct
    @Transactional
    public void init() {

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
}
