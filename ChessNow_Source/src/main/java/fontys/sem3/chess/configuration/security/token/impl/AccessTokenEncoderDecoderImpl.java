package fontys.sem3.chess.configuration.security.token.impl;

import fontys.sem3.chess.configuration.security.token.AccessToken;
import fontys.sem3.chess.configuration.security.token.AccessTokenDecoder;
import fontys.sem3.chess.configuration.security.token.AccessTokenEncoder;
import fontys.sem3.chess.configuration.security.token.exception.InvalidAccessTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class AccessTokenEncoderDecoderImpl implements AccessTokenEncoder, AccessTokenDecoder {
    private final Key key;

    public AccessTokenEncoderDecoderImpl(@Value("${jwt.secret}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public String encode(AccessToken accessToken) {
        Map<String, Object> claimsMap = new HashMap<>();
        if (!accessToken.getRole().isEmpty()) {
            claimsMap.put("role", accessToken.getRole());
        }
        if (accessToken instanceof AccessTokenImpl) {
            AccessTokenImpl customToken = (AccessTokenImpl) accessToken;
            claimsMap.put("username", customToken.getUsername());
            claimsMap.put("rating", customToken.getRating());
            claimsMap.put("email", customToken.getEmail());
        }

        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(accessToken.getSubject())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(30, ChronoUnit.MINUTES)))
                .addClaims(claimsMap)
                .signWith(key)
                .compact();
    }

    @Override
    public AccessToken decode(String accessTokenEncoded) {
        try {
            Jwt<?, Claims> jwt = Jwts.parserBuilder().setSigningKey(key).build()
                    .parseClaimsJws(accessTokenEncoded);
            Claims claims = jwt.getBody();

            String role = claims.get("role", String.class);
            String username = claims.get("username", String.class);
            Long rating = claims.get("rating", Long.class);
            String email = claims.get("email", String.class);

            return new AccessTokenImpl(claims.getSubject(), role, username, rating, email);
        } catch (JwtException e) {
            throw new InvalidAccessTokenException(e.getMessage());
        }
    }
}
