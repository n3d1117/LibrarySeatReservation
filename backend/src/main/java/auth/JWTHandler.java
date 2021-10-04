package auth;

import config.ConfigProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Logger;

public class JWTHandler {

    private static final String ISSUER = "LSR";
    private static final String GATEWAY_SHARED_ISSUER = "GATEWAY-LSR";
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
                .signWith(backendSecretKey())
                .setId(UUID.randomUUID().toString())
                .setExpiration(sixHoursFromNow())
                .compact();
    }

    /**
     * @param jwt the JWT token as string
     * @return the 'subject' field of the JWT token
     */
    public static String getSubject(String jwt) {
        return Jwts
                .parserBuilder()
                .setSigningKey(backendSecretKey())
                .build()
                .parseClaimsJws(jwt)
                .getBody()
                .getSubject();
    }

    /**
     * @param jwt the JWT token as string
     * @return true if the token has the specified issuer or is expired, false otherwise
     */
    public static Boolean validate(String jwt) {
        try {
            Jws<Claims> jws = Jwts
                    .parserBuilder()
                    .setSigningKey(backendSecretKey())
                    .build()
                    .parseClaimsJws(jwt);
            return jws.getBody().getIssuer().equals(ISSUER);
        } catch (Exception e) {
            LOGGER.warning(e.getMessage());
            return false;
        }
    }

    /**
     * @param jwt the JWT token as string to be valiate with Gateway shared key
     * @return true if the token has the specified issuer and is not expired, false otherwise
     */
    public static Boolean validateWithGateway(String jwt) {
        try {
            Jws<Claims> jws = Jwts
                    .parserBuilder()
                    .setSigningKey(gatewaySharedSecretKey())
                    .build()
                    .parseClaimsJws(jwt);
            return jws.getBody().getIssuer().equals(GATEWAY_SHARED_ISSUER);
        } catch (Exception e) {
            LOGGER.warning(e.getMessage());
            return false;
        }
    }

    /**
     * @return The SecretKey generated with HMAC-SHA from Base64 encoded data
     */
    private static SecretKey backendSecretKey() {

        // Fallback to empty string in case the config.properties file is missing BACKEND_AUTH_JWT_BASE64_KEY property
        String key = "";

        try {
            key = ConfigProperties.getProperties().getProperty("BACKEND_AUTH_JWT_BASE64_KEY");
        } catch (IOException e) {
            LOGGER.warning(e.getMessage());
        }
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(key));
    }

    /**
     * @return The Gateway shared SecretKey generated with HMAC-SHA from Base64 encoded data
     */
    private static SecretKey gatewaySharedSecretKey() {

        // Fallback to empty string in case the config.properties file is missing GATEWAY_SHARED_JWT_BASE64_KEY property
        String key = "";

        try {
            key = ConfigProperties.getProperties().getProperty("GATEWAY_SHARED_JWT_BASE64_KEY");
        } catch (IOException e) {
            LOGGER.warning(e.getMessage());
        }
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(key));
    }

    /**
     * @return Date object six hours in the future
     */
    private static Date sixHoursFromNow() {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.HOUR, 6);
        return now.getTime();
    }
}

