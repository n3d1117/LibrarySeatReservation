package controller;

import com.google.gson.Gson;
import dao.LibraryDao;
import dao.ReservationDao;
import dao.UserDao;
import dto.ReservationDto;
import mapper.ReservationMapper;
import model.Library;
import model.Reservation;
import model.User;

import javax.inject.Inject;
import java.util.List;

public class ReservationController {

    @Inject
    private ReservationMapper reservationMapper;

    @Inject
    private ReservationDao reservationDao;

    @Inject
    private UserDao userDao;

    @Inject
    private LibraryDao libraryDao;

    public String all() {
        List<ReservationDto> reservationDtoList = reservationDao.all();
        return new Gson().toJson(reservationDtoList);
    }

    public String find(Long id) {
        ReservationDto reservationDto = reservationDao.findById(id);
        return new Gson().toJson(reservationDto);
    }

    public String findByUser(Long userId) {
        List<ReservationDto> reservationDtoList = reservationDao.findByUserId(userId);
        return new Gson().toJson(reservationDtoList);
    }

    public String findByLibrary(Long libraryId) {
        List<ReservationDto> reservationDtoList = reservationDao.findByLibraryId(libraryId);
        return new Gson().toJson(reservationDtoList);
    }

    public String add(String json) {
        Gson gson = new Gson();
        ReservationDto reservationDto = gson.fromJson(json, ReservationDto.class);

        User user = userDao.findById(reservationDto.getUserId());
        Library library = libraryDao.findById(reservationDto.getLibraryId());
        Reservation newReservation = reservationMapper.generateReservationFromDTO(reservationDto, user, library);

        reservationDao.save(newReservation);

        if (newReservation.getId() != null)
            reservationDto.setId(newReservation.getId());
        return gson.toJson(reservationDto);
    }

    public void delete(Long id) {
        reservationDao.delete(id);
    }
}