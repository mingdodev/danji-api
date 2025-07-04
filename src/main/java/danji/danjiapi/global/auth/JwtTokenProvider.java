package danji.danjiapi.global.auth;

import danji.danjiapi.domain.auth.dto.JwtToken;
import danji.danjiapi.global.exception.CustomException;
import danji.danjiapi.global.exception.ErrorMessage;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtTokenProvider {
    private final SecretKey key;

    /**
     * Constructs a JwtTokenProvider by decoding the provided Base64-encoded secret key and initializing the cryptographic key used for signing JWT tokens.
     *
     * @param secretKey the Base64-encoded secret key from application configuration
     */
    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Generates a JWT access token and refresh token for the authenticated user.
     *
     * The access token includes the user's ID and role as claims and both tokens are valid for 24 hours.
     *
     * @param authentication the authentication object containing user details and authorities
     * @return a {@link JwtToken} containing the generated access and refresh tokens
     * @throws CustomException if the user's role cannot be determined from the authentication object
     */
    public JwtToken generateToken(Authentication authentication) {
        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();

        String role = authentication.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElseThrow(() -> new CustomException(ErrorMessage.AUTH_FORBIDDEN));

        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(86400);

        String accessToken = Jwts.builder()
                .subject(authentication.getName())
                .claim("userId", principal.getUserId())
                .claim("role", role)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(key)
                .compact();

        String refreshToken = Jwts.builder()
                .subject(authentication.getName())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(key)
                .compact();

        return JwtToken.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * Extracts authentication details from a JWT access token.
     *
     * Parses the provided JWT access token to retrieve user ID and role claims, constructs a {@link CustomUserDetails} principal, and returns a {@link UsernamePasswordAuthenticationToken} containing the principal and authorities.
     *
     * @param accessToken the JWT access token to extract authentication from
     * @return an {@link Authentication} object representing the authenticated user
     * @throws CustomException if the token does not contain a role claim
     */
    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);

        if (claims.get("role") == null) {
            throw new CustomException(ErrorMessage.AUTH_FORBIDDEN);
        }

        Long userId = claims.get("userId", Integer.class).longValue();
        String role = claims.get("role", String.class);
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);
        List<GrantedAuthority> authorities = List.of(authority);

        CustomUserDetails principal = new CustomUserDetails(
                userId, claims.getSubject(), "", authorities
        );
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    /**
     * Validates the given JWT token for integrity, expiration, and supported format.
     *
     * @param token the JWT token to validate
     * @return {@code true} if the token is valid; {@code false} if it is invalid, expired, unsupported, or has empty claims
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.warn("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.warn("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.warn("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.warn("JWT claims string is empty.", e);
        }
        return false;
    }

    /**
     * Parses and returns the claims from the given JWT token.
     * <p>
     * If the token is expired, returns the claims from the expired token.
     *
     * @param token the JWT token to parse
     * @return the claims contained in the token
     */
    private Claims parseClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}
