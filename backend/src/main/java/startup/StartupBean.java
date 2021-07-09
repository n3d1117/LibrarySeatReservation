package startup;

import dao.UserDao;
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

    @PostConstruct
    @Transactional
    public void init() {

        User adminUser = ModelFactory.initializeUser();
        adminUser.setEmail("admin@email.com");
        adminUser.setName("Admin");
        adminUser.setSurname("Admin");
        adminUser.setPassword("password");
        adminUser.setRoles(Arrays.asList(Role.BASIC, Role.ADMIN));
        userDao.save(adminUser);

        User regularUser = ModelFactory.initializeUser();
        regularUser.setEmail("user@email.com");
        regularUser.setName("User");
        regularUser.setSurname("User");
        regularUser.setPassword("password");
        regularUser.setRoles(Collections.singletonList(Role.BASIC));
        userDao.save(regularUser);
    }
}
