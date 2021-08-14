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

    // Usa il time_bucket di TimescaleDB per aggregare il numero di prenotazioni per ogni giorno del mese specificato
    // NB: Utilizza l'SqlResultSetMapping definito nella classe Reservation
    public List<ReservationsDailyAggregateDto> dailyAggregateByLibraryIdAndMonth(Long libraryId, int year, int month) {
        return (List<ReservationsDailyAggregateDto>) entityManager
                .createNativeQuery("SELECT time_bucket('1 day', datetime) AS date, COUNT(*) AS count " +
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

    // Only used in StartupBean for faster insertion
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

    public void enableTimescalePostgresExtensionIfNeeded() {
        LOGGER.info("Enabling timescale extension...");
        entityManager.createNativeQuery("CREATE EXTENSION IF NOT EXISTS timescaledb;").executeUpdate();
    }

    public void setupHypertable() {
        LOGGER.info("Setting up hypertable...");

        String result = entityManager.createNativeQuery(
                "SELECT create_hypertable('reservations', 'datetime')"
        ).getSingleResult().toString();

        LOGGER.info(String.format("Result: %s", result));
    }

}
