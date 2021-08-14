package dao;

import dto.ReservationDto;
import dto.ReservationsDailyAggregateDto;
import mapper.ReservationMapper;
import model.*;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceException;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ReservationDaoTest extends JPATest {

    private ReservationMapper mapper;
    private ReservationDao dao;
    private Reservation reservation;

    private User user;
    private Library library;

    @Override
    protected void init() throws IllegalAccessException {
        user = ModelFactory.initializeUser();
        user.setEmail("some@email.com");
        user.setName("name");
        user.setSurname("surname");
        user.setPassword("password");
        user.setRoles(Collections.singletonList(Role.BASIC));

        library = ModelFactory.initializeLibrary();
        library.setName("name");
        library.setAddress("address");
        library.setCapacity(50);

        reservation = ModelFactory.initializeReservation();
        reservation.setUser(user);
        reservation.setLibrary(library);
        reservation.setDatetime(LocalDateTime.now());

        entityManager.persist(user);
        entityManager.persist(library);
        entityManager.persist(reservation);

        dao = new ReservationDao();
        FieldUtils.writeField(dao, "entityManager", entityManager, true);

        mapper = new ReservationMapper();
    }

    @Test
    public void testFindAll() {
        Reservation anotherReservation = ModelFactory.initializeReservation();
        anotherReservation.setUser(user);
        anotherReservation.setLibrary(library);
        anotherReservation.setDatetime(LocalDateTime.now());

        entityManager.persist(anotherReservation);

        List<ReservationDto> allEntities = dao.all();

        List<ReservationDto> expected = Stream.of(reservation, anotherReservation)
                .map(r -> mapper.generateReservationDTO(r))
                .collect(Collectors.toList());

        assertEquals(expected, allEntities);
    }

    @Test
    public void testFindById() {
        ReservationDto result = dao.findById(reservation.getId());

        assertEquals(reservation.getId(), result.getId());
        assertEquals(ReservationMapper.dateToString(reservation.getDatetime()), result.getDatetime());
        assertEquals(reservation.getUser().getId(), result.getUserId());
        assertEquals(reservation.getLibrary().getId(), result.getLibraryId());
        assertEquals(reservation.getLibrary().getName(), result.getLibraryName());
    }

    @Test
    public void testFindByIdThrowsWhenReservationNotFound() {
        assertThrows(EntityNotFoundException.class, () -> dao.findById(2L));
    }

    @Test
    public void testFindByUserId() {
        List<ReservationDto> results = dao.findByUserId(user.getId());
        assertEquals(results.size(), 1);
        ReservationDto result = results.get(0);
        assertEquals(reservation.getId(), result.getId());
        assertEquals(ReservationMapper.dateToString(reservation.getDatetime()), result.getDatetime());
        assertEquals(reservation.getUser().getId(), result.getUserId());
        assertEquals(reservation.getLibrary().getId(), result.getLibraryId());
        assertEquals(reservation.getLibrary().getName(), result.getLibraryName());
    }

    @Test
    public void testFindByUserIdReturnsEmptyListWhenUserNotFound() {
        List<ReservationDto> results = dao.findByUserId(2L);
        assertEquals(Collections.emptyList(), results);
    }

    @Test
    public void testFindByLibraryId() {
        List<ReservationDto> results = dao.findByLibraryId(library.getId());
        assertEquals(results.size(), 1);
        ReservationDto result = results.get(0);
        assertEquals(reservation.getId(), result.getId());
        assertEquals(ReservationMapper.dateToString(reservation.getDatetime()), result.getDatetime());
        assertEquals(reservation.getUser().getId(), result.getUserId());
        assertEquals(reservation.getLibrary().getId(), result.getLibraryId());
        assertEquals(reservation.getLibrary().getName(), result.getLibraryName());
    }

    @Test
    public void testFindByLibraryIdReturnsEmptyWhenLibraryNotFound() {
        List<ReservationDto> results = dao.findByLibraryId(2L);
        assertEquals(Collections.emptyList(), results);
    }

    @Test
    public void testFindByLibraryAndDate() {
        List<ReservationDto> results = dao.findByLibraryIdAndDate(
                library.getId(),
                reservation.getDatetime().getYear(),
                reservation.getDatetime().getMonth().getValue(),
                reservation.getDatetime().getDayOfMonth()
        );
        assertEquals(1, results.size());
        ReservationDto result = results.get(0);
        assertEquals(reservation.getId(), result.getId());
        assertEquals(ReservationMapper.dateToString(reservation.getDatetime()), result.getDatetime());
        assertEquals(reservation.getUser().getId(), result.getUserId());
        assertEquals(reservation.getLibrary().getId(), result.getLibraryId());
    }

    @Test
    public void testFindByLibraryAndDateReturnsEmptyWhenNotFound() {

        // check with wrong id
        LocalDateTime now = LocalDateTime.now();
        List<ReservationDto> results = dao.findByLibraryIdAndDate(
                2L, now.getMonth().getValue(), now.getYear(), now.getDayOfMonth()
        );
        assertEquals(Collections.emptyList(), results);

        // check with wrong date
        List<ReservationDto> results2 = dao.findByLibraryIdAndDate(
                library.getId(), Month.APRIL.getValue(), 1996, 12
        );
        assertEquals(Collections.emptyList(), results2);
    }

    // NB: il test di dailyAggregateByLibraryIdAndMonth non può essere eseguito perchè H2 non supporta time_bucket

    @Test
    public void testSave() {
        Reservation anotherReservation = ModelFactory.initializeReservation();
        anotherReservation.setUser(null);
        anotherReservation.setLibrary(library);
        anotherReservation.setDatetime(LocalDateTime.now());

        dao.save(anotherReservation);

        Reservation retrieved = entityManager.createQuery("FROM Reservation WHERE uuid = :uuid", Reservation.class)
                .setParameter("uuid", anotherReservation.getUuid())
                .getSingleResult();

        assertEquals(anotherReservation, retrieved);
    }

    @Test
    public void testSaveShouldThrowWhenCapacityIsFull() {
        library.setCapacity(1);
        Reservation anotherReservation = ModelFactory.initializeReservation();
        anotherReservation.setUser(null);
        anotherReservation.setLibrary(library);
        anotherReservation.setDatetime(reservation.getDatetime());

        assertThrows(PersistenceException.class, () -> dao.save(anotherReservation));
    }

    @Test
    public void testDelete() {
        Reservation anotherReservation = ModelFactory.initializeReservation();
        anotherReservation.setUser(null);
        anotherReservation.setLibrary(null);
        anotherReservation.setDatetime(LocalDateTime.now());
        entityManager.persist(anotherReservation);

        dao.delete(reservation.getId());

        List<Reservation> all = entityManager.createQuery("FROM Reservation", Reservation.class).getResultList();
        assertEquals(all, Collections.singletonList(anotherReservation));
    }

    @Test
    public void testCascadeDeleteUser() throws IllegalAccessException {
        UserDao userDao = new UserDao();
        FieldUtils.writeField(userDao, "entityManager", entityManager, true);

        userDao.delete(user.getId());

        List<Reservation> all = entityManager.createQuery("FROM Reservation", Reservation.class).getResultList();
        assertEquals(all, Collections.emptyList());
    }

    @Test
    public void testCascadeDeleteLibrary() throws IllegalAccessException {
        LibraryDao libraryDao = new LibraryDao();
        FieldUtils.writeField(libraryDao, "entityManager", entityManager, true);

        libraryDao.delete(library.getId());

        List<Reservation> all = entityManager.createQuery("FROM Reservation", Reservation.class).getResultList();
        assertEquals(all, Collections.emptyList());
    }

}
