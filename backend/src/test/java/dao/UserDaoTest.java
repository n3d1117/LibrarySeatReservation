package dao;

import auth.PasswordHasher;
import model.ModelFactory;
import model.Role;
import model.User;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityNotFoundException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserDaoTest extends JPATest {

    private UserDao dao;
    private User user;

    @Override
    protected void init() throws IllegalAccessException {
        user = ModelFactory.initializeUser();
        user.setEmail("some@email.com");
        user.setName("name");
        user.setSurname("surname");
        user.setPassword(PasswordHasher.hashPassword("secret"));
        user.setRoles(Arrays.asList(Role.BASIC, Role.ADMIN));
        entityManager.persist(user);
        dao = new UserDao();
        FieldUtils.writeField(dao, "entityManager", entityManager, true);
    }

    @Test
    public void testFindAll() {
        User anotherUser = ModelFactory.initializeUser();
        anotherUser.setEmail("another@email.com");
        anotherUser.setName("another name");
        anotherUser.setSurname("another surname");
        anotherUser.setPassword("another secret");
        entityManager.persist(anotherUser);

        List<User> allEntities = dao.all();

        assertEquals(allEntities, Arrays.asList(user, anotherUser));
    }

    @Test
    public void testFindById() {
        User result = dao.findById(user.getId());

        assertEquals(user.getId(), result.getId());
        assertEquals(user.getUuid(), result.getUuid());
        assertEquals(user.getEmail(), result.getEmail());
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getSurname(), result.getSurname());
        assertEquals(user.getPassword(), result.getPassword());
        assertIterableEquals(user.getRoles(), result.getRoles());
    }

    @Test
    public void testFindByIdThrowsWhenUserNotFound() {
        assertThrows(EntityNotFoundException.class, () -> dao.findById(2L));
    }

    @Test
    public void testFindByEmail() {
        User result = dao.findByEmail(user.getEmail());

        assertEquals(user.getId(), result.getId());
        assertEquals(user.getUuid(), result.getUuid());
        assertEquals(user.getEmail(), result.getEmail());
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getSurname(), result.getSurname());
        assertEquals(user.getPassword(), result.getPassword());
        assertIterableEquals(user.getRoles(), result.getRoles());
    }

    @Test
    public void testFindByEmailThrowsWhenUserNotFound() {
        assertThrows(EntityNotFoundException.class, () -> dao.findByEmail("any@email.com"));
    }

    @Test
    public void testSave() {
        User entityToPersist = ModelFactory.initializeUser();
        entityToPersist.setEmail("another@email.com");
        entityToPersist.setName("name");
        entityToPersist.setSurname("surname");
        entityToPersist.setPassword(PasswordHasher.hashPassword("password"));

        dao.save(entityToPersist);

        User retrieved = entityManager.createQuery("FROM User WHERE uuid = :uuid", User.class)
                .setParameter("uuid", entityToPersist.getUuid())
                .getSingleResult();

        assertEquals(entityToPersist, retrieved);
    }

    @Test
    public void testUpdate() {
        user.setPassword("another password");

        dao.update(user, user.getId());

        User retrieved = entityManager.createQuery("FROM User WHERE uuid = :uuid", User.class)
                .setParameter("uuid", user.getUuid())
                .getSingleResult();

        assertEquals(user, retrieved);
    }

    @Test
    public void testUpdateThrowsWhenUserNotFound() {
        assertThrows(EntityNotFoundException.class, () -> dao.update(user, 2L));
    }

    @Test
    public void testDelete() {
        User anotherUser = ModelFactory.initializeUser();
        anotherUser.setEmail("another@email.com");
        anotherUser.setName("another name");
        anotherUser.setSurname("another surname");
        anotherUser.setPassword("another secret");
        entityManager.persist(anotherUser);

        dao.delete(user.getId());

        List<User> allUsers = entityManager.createQuery("FROM User", User.class).getResultList();
        assertEquals(allUsers, Collections.singletonList(anotherUser));
    }

    @Test
    public void testVerify() {
        assertTrue(dao.verify(user.getEmail(), user.getPassword()));
        assertFalse(dao.verify("another@email.com", user.getPassword()));
        assertFalse(dao.verify(user.getEmail(), "another"));
        assertFalse(dao.verify("another@email.com", "another"));
    }

    @Test
    public void testLogin() {
        assertEquals(user, dao.login(user.getEmail(), "secret"));
        assertThrows(EntityNotFoundException.class, () -> dao.login("another@email.com", "secret"));
        assertThrows(EntityNotFoundException.class, () -> dao.login(user.getEmail(), "another"));
        assertThrows(EntityNotFoundException.class, () -> dao.login("another@email.com", "another"));
    }

}
