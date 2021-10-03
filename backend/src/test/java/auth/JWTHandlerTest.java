package auth;

import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JWTHandlerTest {

    private static final String FAKE_JWT = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";

    @Test
    public void createTokenTest() {
        String subject = "some subject";
        String jwt = JWTHandler.createToken(subject);

        assertFalse(jwt.isEmpty());
    }

    @Test
    public void createDifferentTokensTest() {
        String subject = "some subject";
        String jwt = JWTHandler.createToken(subject);

        String subject2 = "another subject";
        String jwt2 = JWTHandler.createToken(subject2);

        assertNotEquals(jwt, jwt2);
    }

    @Test
    public void getSubjectTest() {
        String subject = "some subject";
        String jwt = JWTHandler.createToken(subject);

        assertEquals(subject, JWTHandler.getSubject(jwt));
        assertThrows(SignatureException.class, () -> JWTHandler.getSubject(FAKE_JWT));
    }

    @Test
    public void validateTest() {
        String subject = "some subject";
        String jwt = JWTHandler.createToken(subject);

        assertTrue(JWTHandler.validate(jwt));
        assertFalse(JWTHandler.validate(FAKE_JWT));
    }

}
