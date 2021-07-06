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
        l1.setCapacity(50);

        Library l2 = ModelFactory.initializeLibrary();
        l2.setName("Another Library");
        l2.setAddress("Another street");
        l2.setCapacity(70);

        l1.copy(l2);

        assertEquals(l1.getName(), l2.getName());
        assertEquals(l1.getAddress(), l2.getAddress());
        assertEquals(l1.getCapacity(), l2.getCapacity());
        assertNotEquals(l1.getUuid(), l2.getUuid());
    }
}
