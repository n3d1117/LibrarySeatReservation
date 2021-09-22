package model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a composite primary key (id, datetime) for the Reservation class
 */
public class ReservationCompositeKey implements Serializable {

    private Long id;
    private LocalDateTime datetime;

    public ReservationCompositeKey() {}

    public ReservationCompositeKey(Long id, LocalDateTime datetime) {
        this.id = id;
        this.datetime = datetime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReservationCompositeKey that = (ReservationCompositeKey) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, datetime);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDatetime() {
        return datetime;
    }

    public void setDatetime(LocalDateTime datetime) {
        this.datetime = datetime;
    }
}
