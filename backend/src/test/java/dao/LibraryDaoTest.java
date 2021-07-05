package dao;

import model.Library;
import model.ModelFactory;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityNotFoundException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LibraryDaoTest extends JPATest {

    private LibraryDao dao;
    private Library library;

    @Override
    protected void init() throws IllegalAccessException {
        library = ModelFactory.initializeLibrary();
        library.setName("Name");
        library.setAddress("Address");
        library.setLatitude(11.1);
        library.setLongitude(11.2);
        entityManager.persist(library);
        dao = new LibraryDao();
        FieldUtils.writeField(dao, "entityManager", entityManager, true);
    }

    @Test
    public void testFindAll() {
        Library anotherLibrary = ModelFactory.initializeLibrary();
        anotherLibrary.setName("Another Name");
        anotherLibrary.setAddress("Another Address");
        anotherLibrary.setLatitude(12.1);
        anotherLibrary.setLongitude(13.2);
        entityManager.persist(anotherLibrary);

        List<Library> allEntities = dao.all();

        assertEquals(allEntities, Arrays.asList(library, anotherLibrary));
    }

    @Test
    public void testFindById() {
        Library result = dao.findById(library.getId());

        assertEquals(library.getId(), result.getId());
        assertEquals(library.getUuid(), result.getUuid());
        assertEquals(library.getName(), result.getName());
        assertEquals(library.getLongitude(), result.getLongitude());
        assertEquals(library.getLatitude(), result.getLatitude());
    }

    @Test
    public void testFindByIdThrowsWhenlibraryNotFound() {
        assertThrows(EntityNotFoundException.class, () -> dao.findById(2L));
    }

    @Test
    public void testSave() {
        Library entityToPersist = ModelFactory.initializeLibrary();
        entityToPersist.setName("Another name");
        entityToPersist.setAddress("Another address");
        entityToPersist.setLongitude(13.2);
        entityToPersist.setLatitude(1.6);

        dao.save(entityToPersist);

        Library retrieved = entityManager.createQuery("FROM Library WHERE uuid = :uuid", Library.class)
                .setParameter("uuid", entityToPersist.getUuid())
                .getSingleResult();

        assertEquals(entityToPersist, retrieved);
    }

    @Test
    public void testUpdate() {
        library.setAddress("Another address");

        dao.update(library, library.getId());

        Library retrieved = entityManager.createQuery("FROM Library WHERE uuid = :uuid", Library.class)
                .setParameter("uuid", library.getUuid())
                .getSingleResult();

        assertEquals(library, retrieved);
    }

    @Test
    public void testUpdateThrowsWhenLibraryNotFound() {
        assertThrows(EntityNotFoundException.class, () -> dao.update(library, 2L));
    }

    @Test
    public void testDelete() {
        Library anotherLibrary = ModelFactory.initializeLibrary();
        anotherLibrary.setName("Another name");
        anotherLibrary.setAddress("Another address");
        anotherLibrary.setLongitude(12.2);
        anotherLibrary.setLatitude(14.3);
        entityManager.persist(anotherLibrary);

        dao.delete(library.getId());

        List<Library> allLibraries = entityManager.createQuery("FROM Library", Library.class).getResultList();
        assertEquals(allLibraries, Collections.singletonList(anotherLibrary));
    }

}
