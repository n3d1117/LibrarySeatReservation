package auth;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PasswordHasherTest {

    @Test
    public void hashPasswordTest() {
        String password = "secret";
        String hashedSecret = "2bb80d537b1da3e38bd30361aa855686bde0eacd7162fef6a25fe97bf527a25b";
        assertEquals(hashedSecret, PasswordHasher.hashPassword(password));
    }

    @Test
    public void hashPasswordFailTest() {
        String password = "secret";
        assertNotEquals("wrong_hash", PasswordHasher.hashPassword(password));
    }
}
