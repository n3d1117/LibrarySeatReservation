package mapper;

import dto.ReservationDto;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.*;

public class ReservationMapperTest {

    private ReservationMapper mapper;
    private Reservation reservation;
    private ReservationDto reservationDto;

    private User user;
    private Library library;

    @BeforeEach
    public void setUp() {
        mapper = new ReservationMapper();

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
        reservationDto = new ReservationDto();
        reservationDto.setId(1L);
        reservationDto.setUserId(1L);
        reservationDto.setLibraryId(1L);
        reservationDto.setLibraryName(library.getName());
        reservationDto.setUserEmail(user.getEmail());
        reservationDto.setUserName(user.getName() + " " + user.getSurname());
        reservationDto.setDatetime("2021-07-18 16:41:00");

        // Mock ids
        when(library.getId()).thenReturn(1L);
        when(user.getId()).thenReturn(1L);
        when(reservation.getId()).thenReturn(1L);
    }

    @Test
    public void testGenerateReservationDTO() {
        ReservationDto generated = mapper.generateReservationDTO(reservation);
        assertEquals(reservationDto.getId(), generated.getId());
        assertEquals(reservationDto.getDatetime(), generated.getDatetime());
        assertEquals(reservationDto.getLibraryId(), generated.getLibraryId());
        assertEquals(reservationDto.getLibraryName(), generated.getLibraryName());
        assertEquals(reservationDto.getUserEmail(), generated.getUserEmail());
        assertEquals(reservationDto.getUserId(), generated.getUserId());
        assertEquals(reservationDto.getUserName(), generated.getUserName());
    }

    @Test
    public void testGenerateWrongReservationDTO() {
        reservation.setDatetime(LocalDateTime.now());

        user.setName("another name");
        reservation.setUser(user);

        library.setName("another name");
        reservation.setLibrary(library);

        ReservationDto generated = mapper.generateReservationDTO(reservation);
        assertNotEquals(reservationDto.getDatetime(), generated.getDatetime());
        assertNotEquals(reservationDto.getUserName(), generated.getUserName());
    }

    @Test
    public void testGenerateReservationFromDTO() {
        Reservation generated = mapper.generateReservationFromDTO(reservationDto, user, library);
        assertEquals(reservation.getDatetime(), generated.getDatetime());
        assertEquals(reservation.getUser(), generated.getUser());
        assertEquals(reservation.getLibrary(), generated.getLibrary());
    }

    @Test
    public void testGenerateWrongReservationFromDTO() {
        reservationDto.setDatetime("2021-07-19 16:41:00");

        Reservation generated = mapper.generateReservationFromDTO(reservationDto, user, library);
        assertNotEquals(reservation.getDatetime(), generated.getDatetime());
    }
}
