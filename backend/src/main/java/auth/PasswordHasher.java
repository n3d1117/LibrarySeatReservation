package auth;

import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;

public class PasswordHasher {

    /**
     * Hashes the given password with SHA-256
     * @param password: the password to hash
     * @return the hashed string
     */
    static public String hashPassword(String password) {
        return Hashing.sha256()
                .hashString(password, StandardCharsets.UTF_8)
                .toString();
    }
}
