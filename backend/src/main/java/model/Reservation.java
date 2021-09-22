package model;

import dto.ReservationsDailyAggregateDto;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * This annotation (SqlResultSetMapping) belongs to the ReservationsDailyAggregateDto class,
 * but apparently the entity manager doesn't see result mappings unless they're annotated inside
 * an @Entity class (otherwise, you get a "MappingException: Unknown SqlResultSetMapping" exception).
 * This is why this result mapping appears here.
 * See also https://stackoverflow.com/questions/26831893/createnativequery-mapping-to-pojo-non-entity
 */
@SqlResultSetMapping(name = "ReservationsDailyAggregateResult", classes = {
        @ConstructorResult(targetClass = ReservationsDailyAggregateDto.class,
                columns = {
                        @ColumnResult(name = "date", type = LocalDateTime.class),
                        @ColumnResult(name = "countMorning", type = Integer.class),
                        @ColumnResult(name = "countAfternoon", type = Integer.class)
                })
})

/**
 * The Reservation class cannot extend BaseEntity, because TimescaleDB requires that the "temporal"
 * column (datetime) must also be a primary key, and it seems there's no way to use a composite primary
 * key in JPA while extending a superclass with an @Id already set.
 * For this reason, the Reservation class must reimplement all methods in BaseEntity (equals(), hashCode() and copy())
 * See also https://stackoverflow.com/questions/24696657/creating-jpa-entity-with-composite-primary-key-with-id-from-mappedsuperclass
 */
@Entity
@Table(name = "reservations")
@IdClass(ReservationCompositeKey.class) // defines a composite primar key (id, datetime) for this class
public class Reservation {

    /**
     * The id (part 1/2 of the composite primary key).
     * Cannot be generated using GenerationType.IDENTITY because JPA doesn't seem to support it
     * for classes with a composite primary key (see https://stackoverflow.com/a/68217510).
     * For this reason @SequenceGenerator is used instead.
     */
    @Id
    @SequenceGenerator(name="seq", sequenceName="db_reservations_seq", allocationSize=1)
    @GeneratedValue(generator = "seq")
    @Column(updatable = false, nullable = false)
    private Long id;

    /**
     * The UUID used for equals() and hashCode() methods
     */
    @Column(nullable = false)
    private String uuid;

    /**
     * The datetime (part 2/2 of the composite primary key).
     * Acts as the "temporal" column for the Timescale database.
     */
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
