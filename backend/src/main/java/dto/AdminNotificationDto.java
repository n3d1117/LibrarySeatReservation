package dto;

import com.google.gson.Gson;
import mapper.ReservationMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AdminNotificationDto {

    public enum UserAction {
        ADD,
        DELETE
    }

    private final UserAction action;
    private final Long reservationId;
    private final Long libraryId;
    private final String date;
    private String notificationMessage;

    public AdminNotificationDto(UserAction action, Long reservationId, Long libraryId, String date) {
        this.action = action;
        this.reservationId = reservationId;
        this.libraryId = libraryId;
        this.date = date;
        if (this.action == UserAction.ADD) {
            this.notificationMessage = "Nuova prenotazione #" + this.reservationId + " effettuata per il giorno " + parse(this.date);
        } else if (this.action == UserAction.DELETE) {
            this.notificationMessage = "La prenotazione #" + this.reservationId + " è stata cancellata dal giorno " + parse(this.date);
        }
    }

    public String toJson() {
        return new Gson().toJson(this);
    }

    private String parse(String date) {
        LocalDateTime dateTime = ReservationMapper.stringToDate(date);
        String day = dateTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        String time = dateTime.format(DateTimeFormatter.ofPattern("HH:mm"));
        return day + " alle " + time;
    }
}
