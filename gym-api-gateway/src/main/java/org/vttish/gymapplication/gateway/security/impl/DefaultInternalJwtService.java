package org.vttish.gymapplication.gateway.security.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Service;
import org.vttish.gymapplication.gateway.exception.UnauthenticatedException;
import org.vttish.gymapplication.gateway.security.InternalJwtService;
import org.vttish.gymapplication.gateway.security.model.InternalJwtMetadata;
import org.vttish.gymapplication.gateway.security.model.JwtMetadata;
import org.vttish.gymapplication.gateway.security.model.ParsedInternalJwt;
import org.vttish.gymapplication.gateway.security.model.Role;
import org.vttish.gymapplication.gateway.security.model.UserPrincipal;
import org.vttish.gymapplication.gateway.security.properties.InternalJwtProperties;

@Service
public class DefaultInternalJwtService implements InternalJwtService {

  private static final String CLAIM_ROLE = "role";
  private static final String CLAIM_SCOPE = "scope";
  private static final String CLAIM_EXT_JTI = "ext_jti";

  private final InternalJwtProperties properties;
  private final SecretKey signingKey;
  private final JwtParser parser;

  public DefaultInternalJwtService(InternalJwtProperties properties) {
    this.properties = properties;
    this.signingKey = Keys.hmacShaKeyFor(properties.secret().getBytes(StandardCharsets.UTF_8));
    this.parser = Jwts.parser().verifyWith(signingKey).build();
  }

  @Override
  public String generate(
      UserPrincipal principal,
      JwtMetadata metadata,
      InternalJwtProperties.Rule rule
  ) {
    Instant now = Instant.now();
    Instant validity = now.plusMillis(properties.expirationMs());

    return Jwts.builder()
        .subject(principal.username())
        .claim(CLAIM_ROLE, principal.role().name())
        .claim(CLAIM_EXT_JTI, metadata.jwtId())
        .claim(CLAIM_SCOPE, String.join(" ", rule.scopes()))
        .audience().add(rule.audiences()).and()
        .issuedAt(java.util.Date.from(now))
        .expiration(java.util.Date.from(validity))
        .id(UUID.randomUUID().toString())
        .signWith(signingKey)
        .compact();
  }

  @Override
  public ParsedInternalJwt parse(String token) {
    if (token == null || token.isBlank()) {
      throw new UnauthenticatedException("JWT token is missing");
    }

    try {
      Claims claims = parser.parseSignedClaims(token).getPayload();

      String username = claims.getSubject();
      String jwtId = claims.getId();
      String roleName = claims.get(CLAIM_ROLE, String.class);
      String externalJwtId = claims.get(CLAIM_EXT_JTI, String.class);
      String scopes = claims.get(CLAIM_SCOPE, String.class);
      Set<String> audiences = claims.getAudience();

      if (username == null
          || jwtId == null
          || roleName == null
          || externalJwtId == null
          || scopes == null
          || audiences == null
          || claims.getExpiration() == null
      ) {
        throw new UnauthenticatedException("Invalid or expired JWT token");
      }

      UserPrincipal principal = new UserPrincipal(username, Role.valueOf(roleName));
      InternalJwtMetadata metadata = new InternalJwtMetadata(
          UUID.fromString(jwtId),
          UUID.fromString(externalJwtId),
          audiences.stream().toList(),
          Arrays.asList(scopes.strip().split(" ")),
          claims.getExpiration().toInstant()
      );

      return new ParsedInternalJwt(principal, metadata);
    } catch (JwtException | IllegalArgumentException ex) {
      throw new UnauthenticatedException("Invalid or expired JWT token");
    }
  }
}
