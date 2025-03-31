package ro.unibuc.hello.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class JwtTestUtil {

    // Cheia trebuie să fie IDENTICĂ cu cea din application.properties
    private static final String SECRET_KEY = "mysupersecretkeyforjwt256bitssecurityyyyyyyy";

    private static final SecretKey secret = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

    public static String generateTestToken(String email) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .signWith(secret, SignatureAlgorithm.HS256)
                .compact();
    }
}
