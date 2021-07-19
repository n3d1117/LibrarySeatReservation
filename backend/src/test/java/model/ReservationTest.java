package model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class ReservationTest {

    @Test
    public void testCopy() {
        Reservation r = ModelFactory.initializeReservation();
        r.setUser(exampleUser1());
        r.setLibrary(exampleLibrary1());
        r.setDatetime(LocalDateTime.now());

        Reservation r2 = ModelFactory.initializeReservation();
        r2.setUser(exampleUser2());
        r2.setLibrary(exampleLibrary2());
        r2.setDatetime(LocalDateTime.now());

        r.copy(r2);

        assertEquals(r.getUser(), r2.getUser());
        assertEquals(r.getLibrary(), r2.getLibrary());
        assertEquals(r.getDatetime(), r2.getDatetime());
        assertNotEquals(r.getUuid(), r2.getUuid());
    }

    private User exampleUser1() {
        User u = ModelFactory.initializeUser();
        u.setEmail("jon@snow.com");
        u.setName("Jon");
        u.setSurname("Snow");
        u.setPassword("pass1");
        u.setRoles(Arrays.asList(Role.BASIC, Role.ADMIN));
        return u;
    }

    private User exampleUser2() {
        User u2 = ModelFactory.initializeUser();
        u2.setEmail("cersei@stark.com");
        u2.setName("Cersei");
        u2.setSurname("Stark");
        u2.setPassword("pass2");
        u2.setRoles(Collections.singletonList(Role.BASIC));
        return u2;
    }

    private Library exampleLibrary1() {
        Library l = ModelFactory.initializeLibrary();
        l.setName("Library");
        l.setAddress("Regents street");
        l.setCapacity(50);
        return l;
    }

    private Library exampleLibrary2() {
        Library l2 = ModelFactory.initializeLibrary();
        l2.setName("Another Library");
        l2.setAddress("Another street");
        l2.setCapacity(70);
        return l2;
    }
}
