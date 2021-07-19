package mapper;

import dto.ReservationDto;
import model.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReservationMapper {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public ReservationDto generateReservationDTO(Reservation reservation) {
        ReservationDto reservationDto = new ReservationDto();
        reservationDto.setId(reservation.getId());
        reservationDto.setDatetime(dateToString(reservation.getDatetime()));
        reservationDto.setLibraryId(reservation.getLibrary().getId());
        reservationDto.setUserId(reservation.getUser().getId());
        reservationDto.setUserName(reservation.getUser().getName() + " " + reservation.getUser().getSurname());
        reservationDto.setUserEmail(reservation.getUser().getEmail());
        return reservationDto;
    }

    public Reservation generateReservationFromDTO(ReservationDto reservationDto, User user, Library library) {
        Reservation reservation = ModelFactory.initializeReservation();
        reservation.setDatetime(stringToDate(reservationDto.getDatetime()));
        reservation.setUser(user);
        reservation.setLibrary(library);
        return reservation;
    }

    public static String dateToString(LocalDateTime date) {
        return date.format(formatter);
    }

    public static LocalDateTime stringToDate(String dateString) {
        return LocalDateTime.parse(dateString, formatter);
    }

}
