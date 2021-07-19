package dto;

import mapper.ReservationMapper;

import java.time.LocalDateTime;
import java.util.Objects;

public class ReservationDto {

    private long id;
    private long userId;
    private String userName;
    private String userEmail;
    private long libraryId;
    private String datetime;

    public ReservationDto() { }

    public ReservationDto(long id, long userId, String userName, String userEmail, long libraryId, LocalDateTime datetime) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.libraryId = libraryId;
        this.datetime = ReservationMapper.dateToString(datetime);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public long getLibraryId() {
        return libraryId;
    }

    public void setLibraryId(long libraryId) {
        this.libraryId = libraryId;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    @Override
    public String toString() {
        return "ReservationDto{" +
                "id=" + id +
                ", userId=" + userId +
                ", userName='" + userName + '\'' +
                ", userEmail='" + userEmail + '\'' +
                ", libraryId=" + libraryId +
                ", datetime='" + datetime + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReservationDto that = (ReservationDto) o;
        return id == that.id && userId == that.userId && libraryId == that.libraryId && Objects.equals(userName, that.userName) && Objects.equals(userEmail, that.userEmail) && Objects.equals(datetime, that.datetime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
