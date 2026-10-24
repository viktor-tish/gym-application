package org.vttish.gymapplication.security.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.vttish.gymapplication.exception.AuthenticationException;
import org.vttish.gymapplication.security.Role;
import org.vttish.gymapplication.security.UserPrincipal;

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
  void parseToken_ShouldReturnUserPrincipal_WhenTokenIsValid() {
    String token = jwtService.generateToken(TRAINEE_PRINCIPAL);

    UserPrincipal principal = jwtService.parseToken(token);

    assertNotNull(principal);
    assertEquals("firstName.lastName", principal.username());
    assertEquals(Role.TRAINEE, principal.role());
  }

  @Test
  void parseToken_ShouldThrowAuthenticationException_WhenTokenIsExpired() {
    DefaultJwtService expiredJwtService = new DefaultJwtService(SECRET, -1000L);
    String expiredToken = expiredJwtService.generateToken(TRAINER_PRINCIPAL);

    assertThrows(AuthenticationException.class, () -> jwtService.parseToken(expiredToken));
  }

  @Test
  void parseToken_ShouldThrowAuthenticationException_WhenSignatureIsInvalid() {
    String otherSecret = "YW5vdGhlclNlY3JldEtleUZvclRlc3RpbmdJbnZhbGlkU2lnbmF0dXJlMTIzNDU=";
    DefaultJwtService otherJwtService = new DefaultJwtService(otherSecret, EXPIRATION_MS);
    String foreignToken = otherJwtService.generateToken(TRAINER_PRINCIPAL);

    assertThrows(AuthenticationException.class, () -> jwtService.parseToken(foreignToken));
  }

  @Test
  void parseToken_ShouldThrowAuthenticationException_WhenTokenIsMalformed() {
    assertThrows(
        AuthenticationException.class,
        () -> jwtService.parseToken("malformed.jwt.token")
    );
  }

  @Test
  void parseToken_ShouldThrowAuthenticationException_WhenTokenIsNull() {
    assertThrows(AuthenticationException.class, () -> jwtService.parseToken(null));
  }

  @Test
  void parseToken_ShouldThrowAuthenticationException_WhenRoleClaimIsMissing() {
    Instant now = Instant.now();
    Instant validity = now.plusMillis(EXPIRATION_MS);

    String tokenWithoutRole = Jwts.builder()
        .subject("firstName.lastName")
        .issuedAt(java.util.Date.from(now))
        .expiration(java.util.Date.from(validity))
        .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET)))
        .compact();

    assertThrows(
        AuthenticationException.class,
        () -> jwtService.parseToken(tokenWithoutRole)
    );
  }
}
