package dao;

import dto.ReservationDto;
import model.Reservation;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;
import java.util.logging.Logger;

public class ReservationDao {

    @PersistenceContext(unitName = "default")
    protected EntityManager entityManager;

    private static final Logger LOGGER = Logger.getLogger(ReservationDao.class.getName());

    public List<ReservationDto> all() {
        return entityManager.createQuery(
                "SELECT NEW dto.ReservationDto(r.id, r.user.id, CONCAT(r.user.name, ' ', r.user.surname), r.user.email, r.library.id,  r.datetime) " +
                        "FROM Reservation r", ReservationDto.class).getResultList();
    }

    public ReservationDto findById(Long id) {
        try {
            return entityManager
                    .createQuery("SELECT NEW dto.ReservationDto(r.id, r.user.id, CONCAT(r.user.name, ' ', r.user.surname), r.user.email, r.library.id,  r.datetime) " +
                            "FROM Reservation r WHERE r.id = :id", ReservationDto.class)
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (Exception e) {
            throw new EntityNotFoundException();
        }
    }

    public List<ReservationDto> findByUserId(Long id) {
        return entityManager
                .createQuery("SELECT NEW dto.ReservationDto(r.id, r.user.id, CONCAT(r.user.name, ' ', r.user.surname), r.user.email, r.library.id,  r.datetime) " +
                        "FROM Reservation r WHERE r.user.id = :id", ReservationDto.class)
                .setParameter("id", id)
                .getResultList();
    }

    public List<ReservationDto> findByLibraryId(Long id) {
        return entityManager
                .createQuery("SELECT NEW dto.ReservationDto(r.id, r.user.id, CONCAT(r.user.name, ' ', r.user.surname), r.user.email, r.library.id,  r.datetime) " +
                        "FROM Reservation r WHERE r.library.id = :id", ReservationDto.class)
                .setParameter("id", id)
                .getResultList();
    }

    @Transactional
    public void save(Reservation entity) {
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
