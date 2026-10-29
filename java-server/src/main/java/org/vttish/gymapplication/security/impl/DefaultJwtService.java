package org.vttish.gymapplication.security.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.vttish.gymapplication.exception.UnauthenticatedException;
import org.vttish.gymapplication.security.JwtService;
import org.vttish.gymapplication.security.model.AuthenticatedToken;
import org.vttish.gymapplication.security.model.JwtMetadata;
import org.vttish.gymapplication.security.model.Role;
import org.vttish.gymapplication.security.model.UserPrincipal;

@Service
public class DefaultJwtService implements JwtService {
  private final long expirationMs;
  private final SecretKey signingKey;
  private final JwtParser parser;

  public DefaultJwtService(
      @Value("${jwt.secret}") String secret,
      @Value("${jwt.expiration-ms}") long expirationMs
  ) {
    this.expirationMs = expirationMs;
    this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    this.parser = Jwts.parser().verifyWith(signingKey).build();
  }

  @Override
  public String generateToken(UserPrincipal principal) {
    Instant now = Instant.now();
    Instant validity = now.plusMillis(expirationMs);

    return Jwts.builder()
        .subject(principal.username())
        .claim("role", principal.role().name())
        .issuedAt(java.util.Date.from(now))
        .expiration(java.util.Date.from(validity))
        .id(UUID.randomUUID().toString())
        .signWith(signingKey)
        .compact();
  }

  @Override
  public AuthenticatedToken parseToken(String token) {
    if (token == null || token.isBlank()) {
      throw new UnauthenticatedException("JWT token is missing");
    }

    try {
      Claims claims = parser.parseSignedClaims(token).getPayload();

      String username = claims.getSubject();
      String roleName = claims.get("role", String.class);
      String jwtId = claims.getId();

      if (username == null
          || roleName == null
          || jwtId == null
          || claims.getExpiration() == null
      ) {
        throw new UnauthenticatedException("Invalid or expired JWT token");
      }

      UserPrincipal principal = new UserPrincipal(username, Role.valueOf(roleName));
      JwtMetadata metadata = new JwtMetadata(
          UUID.fromString(jwtId),
          claims.getExpiration().toInstant()
      );

      return new AuthenticatedToken(principal, metadata);
    } catch (JwtException | IllegalArgumentException ex) {
      throw new UnauthenticatedException("Invalid or expired JWT token");
    }
  }
}

