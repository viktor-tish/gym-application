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
import org.vttish.gymapplication.security.model.ParsedToken;
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
  void generate_ShouldReturnNonBlankToken() {
    String token = jwtService.generate(TRAINEE_PRINCIPAL);

    assertNotNull(token);
    assertFalse(token.isBlank());
  }

  @Test
  void parse_ShouldReturnAuthenticatedToken_WhenTokenIsValid() {
    String token = jwtService.generate(TRAINEE_PRINCIPAL);

    ParsedToken authJwt = jwtService.parse(token);

    assertNotNull(authJwt);
    assertNotNull(authJwt.userPrincipal());
    assertEquals("firstName.lastName", authJwt.userPrincipal().username());
    assertEquals(Role.TRAINEE, authJwt.userPrincipal().role());

    assertNotNull(authJwt.jwtMetadata());
    assertNotNull(authJwt.jwtMetadata().jwtId());
    assertNotNull(authJwt.jwtMetadata().expiration());
  }

  @Test
  void parse_ShouldThrowUnauthenticatedException_WhenTokenIsExpired() {
    DefaultJwtService expiredJwtService = new DefaultJwtService(SECRET, -1000L);
    String expiredToken = expiredJwtService.generate(TRAINER_PRINCIPAL);

    assertThrows(UnauthenticatedException.class,
        () -> jwtService.parse(expiredToken));
  }

  @Test
  void parse_ShouldThrowUnauthenticatedException_WhenSignatureIsInvalid() {
    String otherSecret = "YW5vdGhlclNlY3JldEtleUZvclRlc3RpbmdJbnZhbGlkU2lnbmF0dXJlMTIzNDU=";
    DefaultJwtService otherJwtService = new DefaultJwtService(otherSecret, EXPIRATION_MS);
    String foreignToken = otherJwtService.generate(TRAINER_PRINCIPAL);

    assertThrows(UnauthenticatedException.class,
        () -> jwtService.parse(foreignToken));
  }

  @Test
  void parse_ShouldThrowUnauthenticatedException_WhenTokenIsMalformed() {
    assertThrows(
        UnauthenticatedException.class,
        () -> jwtService.parse("malformed.jwt.token")
    );
  }

  @Test
  void parse_ShouldThrowUnauthenticatedException_WhenTokenIsNull() {
    assertThrows(UnauthenticatedException.class, () -> jwtService.parse(null));
  }

  @Test
  void parse_ShouldThrowUnauthenticatedException_WhenTokenIsBlank() {
    assertThrows(UnauthenticatedException.class, () -> jwtService.parse("   "));
  }

  @Test
  void parse_ShouldThrowUnauthenticatedException_WhenRoleClaimIsMissing() {
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
        () -> jwtService.parse(tokenWithoutRole)
    );
  }

  @Test
  void parse_ShouldThrowUnauthenticatedException_WhenSubjectIsMissing() {
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
        () -> jwtService.parse(tokenWithoutSubject)
    );
  }

  @Test
  void parse_ShouldThrowUnauthenticatedException_WhenJwtIdIsMissing() {
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
        () -> jwtService.parse(tokenWithoutId)
    );
  }
}
