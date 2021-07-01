package dao;

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
        user.setUsername("username");
        user.setPassword("secret");
        user.setRoles(Arrays.asList(Role.BASIC, Role.ADMIN));
        entityManager.persist(user);
        dao = new UserDao();
        FieldUtils.writeField(dao, "entityManager", entityManager, true);
    }

    @Test
    public void testFindAll() {
        User anotherUser = ModelFactory.initializeUser();
        user.setUsername("another username");
        user.setPassword("another secret");
        entityManager.persist(anotherUser);

        List<User> allEntities = dao.all();

        System.out.println(allEntities);
        assertEquals(allEntities, Arrays.asList(user, anotherUser));
    }

    @Test
    public void testFindById() {
        User result = dao.findById(user.getId());

        assertEquals(user.getId(), result.getId());
        assertEquals(user.getUuid(), result.getUuid());
        assertEquals(user.getUsername(), result.getUsername());
        assertEquals(user.getPassword(), result.getPassword());
        assertIterableEquals(user.getRoles(), result.getRoles());
    }

    @Test
    public void testFindByIdThrowsWhenUserNotFound() {
        assertThrows(EntityNotFoundException.class, () -> dao.findById(2L));
    }

    @Test
    public void testFindByUsername() {
        User result = dao.findByUsername(user.getUsername());

        assertEquals(user.getId(), result.getId());
        assertEquals(user.getUuid(), result.getUuid());
        assertEquals(user.getUsername(), result.getUsername());
        assertEquals(user.getPassword(), result.getPassword());
        assertIterableEquals(user.getRoles(), result.getRoles());
    }

    @Test
    public void testFindByUsernameThrowsWhenUserNotFound() {
        assertThrows(EntityNotFoundException.class, () -> dao.findByUsername("anything"));
    }

    @Test
    public void testSave() {
        User entityToPersist = ModelFactory.initializeUser();
        entityToPersist.setUsername("another username");
        entityToPersist.setPassword("password");

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
        user.setUsername("another username");
        user.setPassword("another secret");
        entityManager.persist(anotherUser);

        dao.delete(user.getId());

        List<User> allUsers = entityManager.createQuery("FROM User", User.class).getResultList();
        assertEquals(allUsers, Collections.singletonList(anotherUser));
    }

    @Test
    public void testVerify() {
        assertTrue(dao.verify(user.getUsername(), user.getPassword()));
        assertFalse(dao.verify("another", user.getPassword()));
        assertFalse(dao.verify(user.getUsername(), "another"));
        assertFalse(dao.verify("another", "another"));
    }

}
