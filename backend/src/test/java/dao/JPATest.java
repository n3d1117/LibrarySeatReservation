package dao;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public abstract class JPATest {

    private static EntityManagerFactory entityManagerFactory;
    protected EntityManager entityManager;

    @BeforeAll
    public static void setupEntityManager() {
        entityManagerFactory = Persistence.createEntityManagerFactory("test");
    }

    @BeforeEach
    public void setup() throws IllegalAccessException {
        entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.createNativeQuery("TRUNCATE SCHEMA public AND COMMIT").executeUpdate();
        entityManager.getTransaction().commit();
        entityManager.getTransaction().begin();
        init();
        entityManager.getTransaction().commit();
        entityManager.clear();
        entityManager.getTransaction().begin();
    }

    @AfterEach
    public void close() {
        if (entityManager.getTransaction().isActive())
            entityManager.getTransaction().rollback();
        entityManager.close();
    }

    @AfterAll
    public static void tearDownDB() {
        entityManagerFactory.close();
    }

    protected abstract void init() throws IllegalAccessException;
}
