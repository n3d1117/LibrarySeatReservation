package auth;

import config.ConfigProperties;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.logging.Logger;

public class JWTHandler {

    private static final String ISSUER = "LSR";
    private static final Logger LOGGER = Logger.getLogger(JWTHandler.class.getName());

    /**
     * Create a JWT token with the private key, the specified subject and issuer
     * @param subject the token's subject
     * @return the JWT token as string
     */
    public static String createToken(String subject) {
        return Jwts
                .builder()
                .setIssuer(ISSUER)
                .setSubject(subject)
                .signWith(secretKey())
                .compact();
    }

    /**
     * @param jwt the JWT token as string
     * @return the 'subject' field of the JWT token
     */
    public static String getSubject(String jwt) {
        return Jwts
                .parserBuilder()
                .setSigningKey(secretKey())
                .build()
                .parseClaimsJws(jwt)
                .getBody()
                .getSubject();
    }

    /**
     * @param jwt the JWT token as string
     * @return true if the token has the specified issuer, false otherwise
     */
    public static Boolean validate(String jwt) {
        try {
            return Jwts
                    .parserBuilder()
                    .setSigningKey(secretKey())
                    .build()
                    .parseClaimsJws(jwt)
                    .getBody()
                    .getIssuer()
                    .equals(ISSUER);
        } catch (Exception e) {
            LOGGER.warning(e.getMessage());
            return false;
        }
    }

    /**
     * @return The SecretKey generated with HMAC-SHA from Base64 encoded data
     */
    private static SecretKey secretKey() {

        // Fallback to empty string in case the config.properties file is missing AUTH_JWT_BASE64_KEY property
        String key = "";

        try {
            key = ConfigProperties.getProperties().getProperty("AUTH_JWT_BASE64_KEY");
        } catch (IOException e) {
            LOGGER.warning(e.getMessage());
        }
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(key));
    }
}

