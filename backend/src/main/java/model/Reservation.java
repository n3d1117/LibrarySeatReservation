package model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

/*
Due problemi:
- TimescaleDB richiede che la colonna "temporale" della tabella (datetime) sia anche chiave primaria
- JPA non supporta @GeneratedValue sulle classi con chiave primaria composita (@IdClass)
Per questo Reservation non può estendere BaseEntity e c'è bisogno di usare @SequenceGenerator
Vedi https://stackoverflow.com/a/68217510
 */

@Entity
@Table(name = "reservations")
@IdClass(ReservationCompositeKey.class)
public class Reservation {

    @Id
    @SequenceGenerator(name="seq", sequenceName="sample_reservations_seq", allocationSize=1)
    @GeneratedValue(generator = "seq")
    @Column(updatable = false, nullable = false)
    private Long id;

    @Column(nullable = false)
    private String uuid;

    @Id
    @Column(columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDateTime datetime;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Library library;

    protected Reservation() { }

    public Reservation(String uuid) {
        if (uuid == null)
            throw new IllegalArgumentException( "UUID cannot be null!" );
        this.uuid = uuid;
    }

    public void copy(Reservation that) {
        this.datetime = that.getDatetime();
        this.user = that.getUser();
        this.library = that.getLibrary();
    }

    public Long getId() {
        return id;
    }

    public String getUuid() {
        return uuid;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Library getLibrary() {
        return library;
    }

    public void setLibrary(Library library) {
        this.library = library;
    }

    public LocalDateTime getDatetime() {
        return datetime;
    }

    public void setDatetime(LocalDateTime timestamp) {
        this.datetime = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reservation that = (Reservation) o;
        return uuid.equals(that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", uuid='" + uuid + '\'' +
                ", datetime=" + datetime +
                ", user=" + user +
                ", library=" + library +
                '}';
    }
}
