package controller;

import com.google.gson.Gson;
import dao.LibraryDao;
import dao.ReservationDao;
import dao.UserDao;
import dto.ReservationDto;
import dto.ReservationsDailyAggregateDto;
import mapper.ReservationMapper;
import model.*;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class ReservationControllerTest {

    private ReservationController controller;

    private ReservationDao dao;
    private ReservationDto dto;
    private Reservation reservation;

    private User user;
    private Library library;

    @BeforeEach
    public void setup() throws IllegalAccessException {
        controller = new ReservationController();

        dao = mock(ReservationDao.class);

        // Set up library
        library = spy(ModelFactory.initializeLibrary());
        library.setName("name");
        library.setAddress("address");
        library.setCapacity(50);

        // Set up user
        user = spy(ModelFactory.initializeUser());
        user.setEmail("some@email.com");
        user.setName("name");
        user.setSurname("surname");
        user.setPassword("password");
        user.setRoles(Collections.singletonList(Role.BASIC));

        // Set up reservation
        reservation = spy(ModelFactory.initializeReservation());
        reservation.setLibrary(library);
        reservation.setUser(user);
        reservation.setDatetime(LocalDateTime.of(2021, Month.JULY, 18, 16, 41));

        // Set up reservation DTO
        dto = new ReservationDto();
        dto.setId(1L);
        dto.setUserId(1L);
        dto.setLibraryId(1L);
        dto.setUserEmail(user.getEmail());
        dto.setUserName(user.getName() + " " + user.getSurname());
        dto.setDatetime("2021-07-18 16:41:00");

        // Mock ids
        when(library.getId()).thenReturn(1L);
        when(user.getId()).thenReturn(1L);
        when(reservation.getId()).thenReturn(1L);

        FieldUtils.writeField(controller, "reservationMapper", new ReservationMapper(), true);
        FieldUtils.writeField(controller, "reservationDao", dao, true);

        UserDao userDao = mock(UserDao.class);
        LibraryDao libraryDao = mock(LibraryDao.class);
        when(userDao.findById(1L)).thenReturn(user);
        when(libraryDao.findById(1L)).thenReturn(library);
        FieldUtils.writeField(controller, "userDao", userDao, true);
        FieldUtils.writeField(controller, "libraryDao", libraryDao, true);
    }

    @Test
    public void testRetrieveAllReservations() {
        Reservation anotherReservation = spy(ModelFactory.initializeReservation());
        anotherReservation.setDatetime(LocalDateTime.of(2021, Month.JULY, 18, 16, 41));
        anotherReservation.setUser(user);
        anotherReservation.setLibrary(library);

        when(anotherReservation.getId()).thenReturn(2L);

        ReservationDto anotherDto = new ReservationDto();
        anotherDto.setId(2L);
        anotherDto.setUserId(1L);
        anotherDto.setLibraryId(1L);
        anotherDto.setUserEmail(user.getEmail());
        anotherDto.setUserName(user.getName() + " " + user.getSurname());
        anotherDto.setDatetime("2021-07-18 16:41:00");

        when(dao.all()).thenReturn(Arrays.asList(dto, anotherDto));
        assertEquals(controller.all(), new Gson().toJson(Arrays.asList(dto, anotherDto)));
    }

    @Test
    public void testFindById() {
        when(dao.findById(1L)).thenReturn(dto);
        assertEquals(controller.find(1L), new Gson().toJson(dto));
    }

    @Test
    public void testFindByUserId() {
        when(dao.findByUserId(1L)).thenReturn(Collections.singletonList(dto));
        assertEquals(controller.findByUser(1L), new Gson().toJson(Collections.singletonList(dto)));
    }

    @Test
    public void testFindByLibraryId() {
        when(dao.findByLibraryId(1L)).thenReturn(Collections.singletonList(dto));
        assertEquals(controller.findByLibrary(1L), new Gson().toJson(Collections.singletonList(dto)));
    }

    @Test
    public void testFindByLibraryAndDate() {
        int year = reservation.getDatetime().getYear();
        int month = reservation.getDatetime().getMonth().getValue();
        int day = reservation.getDatetime().getDayOfMonth();
        when(dao.findByLibraryIdAndDate(1L, year, month, day)).thenReturn(Collections.singletonList(dto));
        assertEquals(controller.findByLibraryAndDate(1L, year, month, day), new Gson().toJson(Collections.singletonList(dto)));
    }

    @Test
    public void testAggregateByLibraryIdAndMonth() {
        List<ReservationsDailyAggregateDto> stats = Collections.singletonList(
                new ReservationsDailyAggregateDto(reservation.getDatetime(), 1)
        );
        when(dao.dailyAggregateByLibraryIdAndMonth(library.getId(), reservation.getDatetime().getYear(), reservation.getDatetime().getMonth().getValue()))
                .thenReturn(stats);
        String result = controller.dailyAggregateByMonth(
                library.getId(), reservation.getDatetime().getYear(), reservation.getDatetime().getMonth().getValue()
        );
        assertEquals(result, new Gson().toJson(stats));
    }

    @Test
    public void testAddReservation() {
        String dtoJson = new Gson().toJson(dto);

        assertEquals(controller.add(dtoJson), dtoJson);

        ArgumentCaptor<Reservation> argument = ArgumentCaptor.forClass(Reservation.class);
        verify(dao).save(argument.capture());
        assertEquals(reservation.getDatetime(), argument.getValue().getDatetime());
        assertEquals(reservation.getUser(), argument.getValue().getUser());
        assertEquals(reservation.getLibrary(), argument.getValue().getLibrary());
    }

    @Test
    public void testDeleteReservation() {
        controller.delete(dto.getId());
        verify(dao).delete(dto.getId());
    }
}
