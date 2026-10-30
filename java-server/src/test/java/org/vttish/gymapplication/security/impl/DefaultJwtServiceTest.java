package org.vttish.gymapplication.security.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.vttish.gymapplication.exception.UnauthenticatedException;
import org.vttish.gymapplication.security.model.AuthenticatedToken;
import org.vttish.gymapplication.security.model.Role;
import org.vttish.gymapplication.security.model.UserPrincipal;

class DefaultJwtServiceTest {

  private static final String SECRET =
      "YmVsaWV2ZWR0ZW50Y292ZXJvbGRlc3RvY2N1cm5vZGRlZGNpdGl6ZW50b29rc29sYXI=";
  private static final long EXPIRATION_MS = 3600000L;
  private static final UserPrincipal TRAINEE_PRINCIPAL =
      new UserPrincipal("firstName.lastName", Role.TRAINEE);
  private static final UserPrincipal TRAINER_PRINCIPAL =
      new UserPrincipal("firstName.lastName", Role.TRAINER);

  private DefaultJwtService jwtService;

  @BeforeEach
  void setUp() {
    jwtService = new DefaultJwtService(SECRET, EXPIRATION_MS);
  }

  @Test
  void generateToken_ShouldReturnNonBlankToken() {
    String token = jwtService.generateToken(TRAINEE_PRINCIPAL);

    assertNotNull(token);
    assertFalse(token.isBlank());
  }

  @Test
  void parseToken_ShouldReturnAuthenticatedToken_WhenTokenIsValid() {
    String token = jwtService.generateToken(TRAINEE_PRINCIPAL);

    AuthenticatedToken authJwt = jwtService.parseToken(token);

    assertNotNull(authJwt);
    assertNotNull(authJwt.userPrincipal());
    assertEquals("firstName.lastName", authJwt.userPrincipal().username());
    assertEquals(Role.TRAINEE, authJwt.userPrincipal().role());

    assertNotNull(authJwt.jwtMetadata());
    assertNotNull(authJwt.jwtMetadata().jwtId());
    assertNotNull(authJwt.jwtMetadata().expiration());
  }

  @Test
  void parseToken_ShouldThrowUnauthenticatedException_WhenTokenIsExpired() {
    DefaultJwtService expiredJwtService = new DefaultJwtService(SECRET, -1000L);
    String expiredToken = expiredJwtService.generateToken(TRAINER_PRINCIPAL);

    assertThrows(UnauthenticatedException.class,
        () -> jwtService.parseToken(expiredToken));
  }

  @Test
  void parseToken_ShouldThrowUnauthenticatedException_WhenSignatureIsInvalid() {
    String otherSecret = "YW5vdGhlclNlY3JldEtleUZvclRlc3RpbmdJbnZhbGlkU2lnbmF0dXJlMTIzNDU=";
    DefaultJwtService otherJwtService = new DefaultJwtService(otherSecret, EXPIRATION_MS);
    String foreignToken = otherJwtService.generateToken(TRAINER_PRINCIPAL);

    assertThrows(UnauthenticatedException.class,
        () -> jwtService.parseToken(foreignToken));
  }

  @Test
  void parseToken_ShouldThrowUnauthenticatedException_WhenTokenIsMalformed() {
    assertThrows(
        UnauthenticatedException.class,
        () -> jwtService.parseToken("malformed.jwt.token")
    );
  }

  @Test
  void parseToken_ShouldThrowUnauthenticatedException_WhenTokenIsNull() {
    assertThrows(UnauthenticatedException.class, () -> jwtService.parseToken(null));
  }

  @Test
  void parseToken_ShouldThrowUnauthenticatedException_WhenTokenIsBlank() {
    assertThrows(UnauthenticatedException.class, () -> jwtService.parseToken("   "));
  }

  @Test
  void parseToken_ShouldThrowUnauthenticatedException_WhenRoleClaimIsMissing() {
    Instant now = Instant.now();
    Instant validity = now.plusMillis(EXPIRATION_MS);

    String tokenWithoutRole = Jwts.builder()
        .subject("firstName.lastName")
        .id(UUID.randomUUID().toString())
        .issuedAt(Date.from(now))
        .expiration(Date.from(validity))
        .signWith(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)))
        .compact();

    assertThrows(
        UnauthenticatedException.class,
        () -> jwtService.parseToken(tokenWithoutRole)
    );
  }

  @Test
  void parseToken_ShouldThrowUnauthenticatedException_WhenSubjectIsMissing() {
    Instant now = Instant.now();
    Instant validity = now.plusMillis(EXPIRATION_MS);

    String tokenWithoutSubject = Jwts.builder()
        .claim("role", Role.TRAINEE.name())
        .id(UUID.randomUUID().toString())
        .issuedAt(Date.from(now))
        .expiration(Date.from(validity))
        .signWith(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)))
        .compact();

    assertThrows(
        UnauthenticatedException.class,
        () -> jwtService.parseToken(tokenWithoutSubject)
    );
  }

  @Test
  void parseToken_ShouldThrowUnauthenticatedException_WhenJwtIdIsMissing() {
    Instant now = Instant.now();
    Instant validity = now.plusMillis(EXPIRATION_MS);

    String tokenWithoutId = Jwts.builder()
        .subject("firstName.lastName")
        .claim("role", Role.TRAINEE.name())
        .issuedAt(Date.from(now))
        .expiration(Date.from(validity))
        .signWith(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)))
        .compact();

    assertThrows(
        UnauthenticatedException.class,
        () -> jwtService.parseToken(tokenWithoutId)
    );
  }
}
