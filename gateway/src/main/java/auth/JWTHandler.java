package auth;

import config.ConfigProperties;
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

    private static final String GATEWAY_SHARED_ISSUER = "GATEWAY-LSR";
    private static final Logger LOGGER = Logger.getLogger(JWTHandler.class.getName());

    /**
     * Create a JWT token with the private key, the specified subject and issuer
     *
     * @return the JWT token as string
     */
    public static String createGatewaySharedToken() {
        return Jwts
                .builder()
                .setIssuer(GATEWAY_SHARED_ISSUER)
                .signWith(gatewaySharedSecretKey())
                .setId(UUID.randomUUID().toString())
                .setExpiration(twoMinutesFromNow())
                .compact();
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
     * @return Date object two minutes in the future
     */
    private static Date twoMinutesFromNow() {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.MINUTE, 2);
        return now.getTime();
    }
}
