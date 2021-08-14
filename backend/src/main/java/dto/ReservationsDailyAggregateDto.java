package dto;

import mapper.ReservationMapper;

import java.time.LocalDateTime;
import java.util.Objects;

public class ReservationsDailyAggregateDto {

    private final String date;
    private final Integer count;

    public ReservationsDailyAggregateDto(LocalDateTime date, Integer count) {
        this.date = ReservationMapper.dateToString(date);
        this.count = count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReservationsDailyAggregateDto that = (ReservationsDailyAggregateDto) o;
        return Objects.equals(date, that.date) && Objects.equals(count, that.count);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, count);
    }

    @Override
    public String toString() {
        return "ReservationsDailyAggregateDto{" +
                "datetime='" + date + '\'' +
                ", count=" + count +
                '}';
    }
}
