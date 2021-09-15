package dto;

public class AdminNotificationDto {

    public AdminNotificationDto(UserAction action, Long reservationId, Long libraryId, String date) {
        this.action = action;
        this.reservationId = reservationId;
        this.libraryId = libraryId;
        this.date = date;
        if (this.action == UserAction.ADD) {
            this.notificationMessage = "Prenotazione "+this.reservationId+" effettuata per il giorno "+this.date;
        } else if (this.action == UserAction.DELETE) {
            this.notificationMessage = "Prenotazione "+this.reservationId+" cancellata dal giorno "+this.date;
        }
    }

    public enum UserAction {
        ADD("ADD"),
        DELETE("DELETE");

        private final String text;

        UserAction(final String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }

    private final UserAction action;
    private final Long reservationId;
    private final Long libraryId;
    private final String date;
    private String notificationMessage;

    public UserAction getAction() {
        return action;
    }

    public Long getReservationId() {
        return reservationId;
    }

    public Long getLibraryId() {
        return libraryId;
    }

    public String getDate() {
        return date;
    }

    public String getNotificationMessage() {
        return notificationMessage;
    }

    public void setNotificationMessage(String notificationMessage) {
        this.notificationMessage = notificationMessage;
    }
}
