package model;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class UserTest {

    @Test
    public void testCopy() {
        User u1 = ModelFactory.initializeUser();
        u1.setEmail("jon@snow.com");
        u1.setName("Jon");
        u1.setSurname("Snow");
        u1.setPassword("pass1");
        u1.setRoles(Arrays.asList(Role.BASIC, Role.ADMIN));

        User u2 = ModelFactory.initializeUser();
        u2.setEmail("cersei@stark.com");
        u2.setName("Cersei");
        u2.setSurname("Stark");
        u2.setPassword("pass2");
        u1.setRoles(Collections.singletonList(Role.BASIC));

        u1.copy(u2);

        assertEquals(u1.getEmail(), u2.getEmail());
        assertEquals(u1.getPassword(), u2.getPassword());
        assertNotEquals(u1.getUuid(), u2.getUuid());
    }
}
