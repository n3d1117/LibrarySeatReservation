package dao;

import dto.ReservationDto;
import dto.ReservationsDailyAggregateDto;
import model.Reservation;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.transaction.Transactional;
import java.util.List;
import java.util.logging.Logger;

/**
 * The ReservationDao cannot extend BaseDao, since Reservation is not a BaseEntity
 * So all CRUD methods for the Reservation class have been implemented here.
 * This DAO never returns a Reservation object directly, but uses a ReservationDto instead.
 */
public class ReservationDao {

    @PersistenceContext(unitName = "default")
    protected EntityManager entityManager;

    private static final Logger LOGGER = Logger.getLogger(ReservationDao.class.getName());

    public List<ReservationDto> all() {
        return entityManager.createQuery(
                "SELECT NEW dto.ReservationDto(r.id, r.user.id, CONCAT(r.user.name, ' ', r.user.surname), r.user.email, r.library.id, r.library.name, r.datetime) " +
                        "FROM Reservation r", ReservationDto.class).getResultList();
    }

    public ReservationDto findById(Long id) {
        try {
            return entityManager
                    .createQuery("SELECT NEW dto.ReservationDto(r.id, r.user.id, CONCAT(r.user.name, ' ', r.user.surname), r.user.email, r.library.id, r.library.name, r.datetime) " +
                            "FROM Reservation r WHERE r.id = :id", ReservationDto.class)
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (Exception e) {
            throw new EntityNotFoundException();
        }
    }

    public List<ReservationDto> findByUserId(Long id) {
        return entityManager
                .createQuery("SELECT NEW dto.ReservationDto(r.id, r.user.id, CONCAT(r.user.name, ' ', r.user.surname), r.user.email, r.library.id, r.library.name, r.datetime) " +
                        "FROM Reservation r WHERE r.user.id = :id", ReservationDto.class)
                .setParameter("id", id)
                .getResultList();
    }

    public List<ReservationDto> findByUserEmail(String email) {
        return entityManager
                .createQuery("SELECT NEW dto.ReservationDto(r.id, r.user.id, CONCAT(r.user.name, ' ', r.user.surname), r.user.email, r.library.id, r.library.name, r.datetime) " +
                        "FROM Reservation r WHERE r.user.email = :email", ReservationDto.class)
                .setParameter("email", email)
                .getResultList();
    }

    public List<ReservationDto> findByLibraryId(Long id) {
        return entityManager
                .createQuery("SELECT NEW dto.ReservationDto(r.id, r.user.id, CONCAT(r.user.name, ' ', r.user.surname), r.user.email, r.library.id, r.library.name, r.datetime) " +
                        "FROM Reservation r WHERE r.library.id = :id", ReservationDto.class)
                .setParameter("id", id)
                .getResultList();
    }

    public List<ReservationDto> findByLibraryIdAndDate(Long libraryId, int year, int month, int day) {
        return entityManager
                .createQuery("SELECT NEW dto.ReservationDto(r.id, r.user.id, CONCAT(r.user.name, ' ', r.user.surname), r.user.email, r.library.id, r.library.name, r.datetime) " +
                        "FROM Reservation r WHERE r.library.id = :libraryId " +
                        "AND EXTRACT(DAY FROM datetime) = :day " +
                        "AND EXTRACT(MONTH FROM datetime) = :month " +
                        "AND EXTRACT(YEAR FROM datetime) = :year",
                        ReservationDto.class)
                .setParameter("libraryId", libraryId)
                .setParameter("day", day)
                .setParameter("month", month)
                .setParameter("year", year)
                .getResultList();
    }

    /**
     * Uses TimescaleDB's time_bucket feature (https://docs.timescale.com/api/latest/hyperfunctions/time_bucket/)
     * to aggregate number of reservations for every day of the specified month, organized by morning and afternoon.
     * Useful for a "calendar" view, where this API is called on every month change.
     *
     * NOTE: Uses SqlResultSetMapping (defined in Reservation class) to map query results directly
     * into ReservationsDailyAggregateDto objects. This is needed because the time_bucket is not supported
     * by JPA's TypedQuery, so a native query must be used instead.
     *
     * @param libraryId the id of the library
     * @param year the year to search
     * @param month the month to search
     * @return a list of ReservationsDailyAggregateDto objects, each representing a daily aggregate
     */
    public List<ReservationsDailyAggregateDto> dailyAggregateByLibraryIdAndMonth(Long libraryId, int year, int month) {
        return (List<ReservationsDailyAggregateDto>) entityManager
                .createNativeQuery("SELECT time_bucket('1 day', datetime) AS date, " +
                        "COUNT(case when EXTRACT(HOUR FROM datetime) = 8 then 1 end) AS countMorning, " +
                        "COUNT(case when EXTRACT(HOUR FROM datetime) = 13 then 1 end) AS countAfternoon " +
                        "FROM reservations " +
                        "WHERE library_id = :libraryId " +
                        "AND EXTRACT(MONTH FROM datetime) = :month " +
                        "AND EXTRACT(YEAR FROM datetime) = :year " +
                        "GROUP BY date " +
                        "ORDER BY date ASC",
                        "ReservationsDailyAggregateResult")
                .setParameter("libraryId", libraryId )
                .setParameter("month", month)
                .setParameter("year", year)
                .getResultList();
    }

    /**
     * Saves the reservation into the database, if the library is not already full for the specified time slot
     * @param entity the reservation to save
     * @throws PersistenceException if the library is already full for that time slot
     */
    @Transactional
    public void save(Reservation entity) {
        long count = (long)entityManager.createQuery(
                "SELECT count(r.id) FROM Reservation r WHERE r.library.id = :id AND r.datetime = :date")
                .setParameter("id", entity.getLibrary().getId())
                .setParameter("date", entity.getDatetime())
                .getSingleResult();
        if (count >= entity.getLibrary().getCapacity()) {
            LOGGER.info(String.format("Reached full capacity for library %s on %s", entity.getLibrary().getName(), entity.getDatetime()));
            throw new PersistenceException("Capacità piena");
        } else {
            entityManager.persist(entity);
        }
    }

    /**
     * Saves the reservation into the database without checking if the library is full for the specified time slot
     * NOTE: Only used in StartupBean for faster insertion of a controlled number of reservations
     * @param entity the reservation to save
     */
    @Transactional
    public void saveSkippingCapacityCheck(Reservation entity) {
        entityManager.persist(entity);
    }

    @Transactional
    public void delete(Long id) {
        Reservation toDelete = entityManager
                .createQuery("FROM Reservation WHERE id = :id", Reservation.class)
                .setParameter("id", id)
                .getSingleResult();
        entityManager.remove(toDelete);
    }

    /**
     * Enables the TimescaleDB PostgreSQL extension on the database (only needed once at startup)
     */
    public void enableTimescalePostgresExtensionIfNeeded() {
        LOGGER.info("Enabling timescale extension...");
        entityManager.createNativeQuery("CREATE EXTENSION IF NOT EXISTS timescaledb;").executeUpdate();
    }

    /**
     * Sets up the reservations as a Timescale hypertable (https://docs.timescale.com/api/latest/hypertable/)
     * using "datetime" as the temporal column.
     * NOTE: requires setting up a custom PostgreSQL dialect in the "hibernate.dialect" of the persistence.xml file
     * in order to parse the output correctly.
     */
    public void setupHypertable() {
        LOGGER.info("Setting up hypertable...");

        String result = entityManager.createNativeQuery(
                "SELECT create_hypertable('reservations', 'datetime')"
        ).getSingleResult().toString();

        LOGGER.info(String.format("Result: %s", result));
    }

}
