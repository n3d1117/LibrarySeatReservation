package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class LibraryTest {

    @Test
    public void testCopy() {
        Library l1 = ModelFactory.initializeLibrary();
        l1.setName("Library");
        l1.setAddress("Regents street");
        l1.setLatitude(11.1);
        l1.setLongitude(11.2);

        Library l2 = ModelFactory.initializeLibrary();
        l2.setName("Another Library");
        l2.setAddress("Another street");
        l2.setLatitude(15.1);
        l2.setLongitude(14.2);

        l1.copy(l2);

        assertEquals(l1.getName(), l2.getName());
        assertEquals(l1.getAddress(), l2.getAddress());
        assertEquals(l1.getLatitude(), l2.getLatitude());
        assertEquals(l1.getLongitude(), l2.getLongitude());
        assertNotEquals(l1.getUuid(), l2.getUuid());
    }
}
