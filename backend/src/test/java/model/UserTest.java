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
        u1.setUsername("Jon Snow");
        u1.setPassword("pass1");
        u1.setRoles(Arrays.asList(Role.BASIC, Role.ADMIN));

        User u2 = ModelFactory.initializeUser();
        u2.setUsername("Cersei Stark");
        u2.setPassword("pass2");
        u1.setRoles(Collections.singletonList(Role.BASIC));

        u1.copy(u2);

        assertEquals(u1.getUsername(), u2.getUsername());
        assertEquals(u1.getPassword(), u2.getPassword());
        assertNotEquals(u1.getUuid(), u2.getUuid());
    }
}
