package dto;

import mapper.ReservationMapper;

import java.time.LocalDateTime;
import java.util.Objects;

public class ReservationsDailyAggregateDto {

    private final String date;
    private final Integer countMorning;
    private final Integer countAfternoon;

    public ReservationsDailyAggregateDto(LocalDateTime date, Integer countMorning, Integer countAfternoon) {
        this.date = ReservationMapper.dateToString(date);
        this.countMorning = countMorning;
        this.countAfternoon = countAfternoon;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReservationsDailyAggregateDto that = (ReservationsDailyAggregateDto) o;
        return Objects.equals(date, that.date) && Objects.equals(countMorning, that.countMorning) && Objects.equals(countAfternoon, that.countAfternoon);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, countMorning, countAfternoon);
    }

    @Override
    public String toString() {
        return "ReservationsDailyAggregateDto{" +
                "datetime='" + date + '\'' +
                ", countMorning=" + countMorning +
                ", countAfternoon=" + countAfternoon +
                '}';
    }
}
