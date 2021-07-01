package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class BaseEntityTest {

    private FakeBaseEntity e1, e2, e3;

    @BeforeEach
    public void setUp() {
        String uuid1 = UUID.randomUUID().toString();
        String uuid2 = UUID.randomUUID().toString();
        e1 = new FakeBaseEntity(uuid1);
        e2 = new FakeBaseEntity(uuid2);
        e3 = new FakeBaseEntity(uuid1);
    }

    @Test
    public void testNullUUID() {
        assertThrows(IllegalArgumentException.class, () -> new FakeBaseEntity(null));
    }

    @Test
    public void testEquals() {
        assertEquals(e1, e1);
        assertEquals(e1, e3);
        assertNotEquals(e1, e2);
    }

    @Test
    public void testHashCode() {
        assertEquals(e1.hashCode(), e1.hashCode());
        assertEquals(e1.hashCode(), e3.hashCode());
        assertNotEquals(e1.hashCode(), e2.hashCode());
    }

    static class FakeBaseEntity extends BaseEntity {

        @Override
        public void copy(BaseEntity a) { }

        public FakeBaseEntity(String uuid) {
            super(uuid);
        }
    }

}