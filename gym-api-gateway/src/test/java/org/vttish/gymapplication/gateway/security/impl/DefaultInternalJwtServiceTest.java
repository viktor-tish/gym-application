package org.vttish.gymapplication.gateway.security.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.vttish.gymapplication.gateway.exception.UnauthenticatedException;
import org.vttish.gymapplication.gateway.security.model.JwtMetadata;
import org.vttish.gymapplication.gateway.security.model.ParsedInternalJwt;
import org.vttish.gymapplication.gateway.security.model.Role;
import org.vttish.gymapplication.gateway.security.model.UserPrincipal;
import org.vttish.gymapplication.gateway.security.properties.InternalJwtProperties;

class DefaultInternalJwtServiceTest {

  private static final String SECRET =
      "YmVsaWV2ZWR0ZW50Y292ZXJvbGRlc3RvY2N1cm5vZGRlZGNpdGl6ZW50b29rc29sYXI=";
  private static final long EXPIRATION_MS = 3600000L;
  private static final UserPrincipal TRAINEE_PRINCIPAL =
      new UserPrincipal("firstName.lastName", Role.TRAINEE);
  private static final UUID EXTERNAL_JWT_ID = UUID.randomUUID();
  private static final JwtMetadata JWT_METADATA =
      new JwtMetadata(EXTERNAL_JWT_ID, Instant.now().plusMillis(EXPIRATION_MS));
  private static final InternalJwtProperties.Rule SAMPLE_RULE =
      new InternalJwtProperties.Rule(
          Set.of(InternalJwtProperties.Verb.GET),
          "/api/v1/test",
          List.of("audience-1"),
          List.of("scope:read", "scope:write")
      );

  private DefaultInternalJwtService internalJwtService;

  @BeforeEach
  void setUp() {
    InternalJwtProperties properties = new InternalJwtProperties(
        SECRET,
        EXPIRATION_MS,
        List.of(SAMPLE_RULE)
    );
    internalJwtService = new DefaultInternalJwtService(properties);
  }

  @Test
  void generate_ShouldReturnNonBlankToken() {
    String token = internalJwtService.generate(TRAINEE_PRINCIPAL, JWT_METADATA, SAMPLE_RULE);

    assertNotNull(token);
    assertFalse(token.isBlank());
  }

  @Test
  void parse_ShouldReturnParsedInternalJwt_WhenTokenIsValid() {
    String token = internalJwtService.generate(TRAINEE_PRINCIPAL, JWT_METADATA, SAMPLE_RULE);

    ParsedInternalJwt parsedJwt = internalJwtService.parse(token);

    assertNotNull(parsedJwt);
    assertNotNull(parsedJwt.userPrincipal());
    assertEquals("firstName.lastName", parsedJwt.userPrincipal().username());
    assertEquals(Role.TRAINEE, parsedJwt.userPrincipal().role());

    assertNotNull(parsedJwt.internalJwtMetadata());
    assertNotNull(parsedJwt.internalJwtMetadata().jwtId());
    assertEquals(EXTERNAL_JWT_ID, parsedJwt.internalJwtMetadata().externalJwtId());
    assertEquals(SAMPLE_RULE.audiences(), parsedJwt.internalJwtMetadata().audiences());
    assertEquals(SAMPLE_RULE.scopes(), parsedJwt.internalJwtMetadata().scopes());
    assertNotNull(parsedJwt.internalJwtMetadata().expiration());
  }

  @Test
  void parse_ShouldThrowUnauthenticatedException_WhenTokenIsExpired() {
    InternalJwtProperties expiredProperties = new InternalJwtProperties(
        SECRET,
        -1000L,
        List.of(SAMPLE_RULE)
    );
    DefaultInternalJwtService expiredService = new DefaultInternalJwtService(expiredProperties);
    String expiredToken = expiredService.generate(TRAINEE_PRINCIPAL, JWT_METADATA, SAMPLE_RULE);

    assertThrows(UnauthenticatedException.class,
        () -> internalJwtService.parse(expiredToken));
  }

  @Test
  void parse_ShouldThrowUnauthenticatedException_WhenSignatureIsInvalid() {
    String otherSecret = "YW5vdGhlclNlY3JldEtleUZvclRlc3RpbmdJbnZhbGlkU2lnbmF0dXJlMTIzNDU=";
    InternalJwtProperties otherProperties = new InternalJwtProperties(
        otherSecret,
        EXPIRATION_MS,
        List.of(SAMPLE_RULE)
    );
    DefaultInternalJwtService otherService = new DefaultInternalJwtService(otherProperties);
    String foreignToken = otherService.generate(TRAINEE_PRINCIPAL, JWT_METADATA, SAMPLE_RULE);

    assertThrows(UnauthenticatedException.class,
        () -> internalJwtService.parse(foreignToken));
  }

  @Test
  void parse_ShouldThrowUnauthenticatedException_WhenTokenIsMalformed() {
    assertThrows(
        UnauthenticatedException.class,
        () -> internalJwtService.parse("malformed.jwt.token")
    );
  }

  @Test
  void parse_ShouldThrowUnauthenticatedException_WhenTokenIsNull() {
    assertThrows(UnauthenticatedException.class, () -> internalJwtService.parse(null));
  }

  @Test
  void parse_ShouldThrowUnauthenticatedException_WhenTokenIsBlank() {
    assertThrows(UnauthenticatedException.class, () -> internalJwtService.parse("    "));
  }

  @Test
  void parse_ShouldThrowUnauthenticatedException_WhenSubjectIsMissing() {
    Instant now = Instant.now();
    Instant validity = now.plusMillis(EXPIRATION_MS);

    String tokenWithoutSubject = Jwts.builder()
        .claim("role", Role.TRAINEE.name())
        .claim("ext_jti", EXTERNAL_JWT_ID.toString())
        .claim("scope", "scope:read")
        .audience().add("audience-1").and()
        .id(UUID.randomUUID().toString())
        .issuedAt(Date.from(now))
        .expiration(Date.from(validity))
        .signWith(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)))
        .compact();

    assertThrows(
        UnauthenticatedException.class,
        () -> internalJwtService.parse(tokenWithoutSubject)
    );
  }

  @Test
  void parse_ShouldThrowUnauthenticatedException_WhenRoleClaimIsMissing() {
    Instant now = Instant.now();
    Instant validity = now.plusMillis(EXPIRATION_MS);

    String tokenWithoutRole = Jwts.builder()
        .subject("firstName.lastName")
        .claim("ext_jti", EXTERNAL_JWT_ID.toString())
        .claim("scope", "scope:read")
        .audience().add("audience-1").and()
        .id(UUID.randomUUID().toString())
        .issuedAt(Date.from(now))
        .expiration(Date.from(validity))
        .signWith(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)))
        .compact();

    assertThrows(
        UnauthenticatedException.class,
        () -> internalJwtService.parse(tokenWithoutRole)
    );
  }

  @Test
  void parse_ShouldThrowUnauthenticatedException_WhenExternalJtiIsMissing() {
    Instant now = Instant.now();
    Instant validity = now.plusMillis(EXPIRATION_MS);

    String tokenWithoutExtJti = Jwts.builder()
        .subject("firstName.lastName")
        .claim("role", Role.TRAINEE.name())
        .claim("scope", "scope:read")
        .audience().add("audience-1").and()
        .id(UUID.randomUUID().toString())
        .issuedAt(Date.from(now))
        .expiration(Date.from(validity))
        .signWith(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)))
        .compact();

    assertThrows(
        UnauthenticatedException.class,
        () -> internalJwtService.parse(tokenWithoutExtJti)
    );
  }

  @Test
  void parse_ShouldThrowUnauthenticatedException_WhenScopeIsMissing() {
    Instant now = Instant.now();
    Instant validity = now.plusMillis(EXPIRATION_MS);

    String tokenWithoutScope = Jwts.builder()
        .subject("firstName.lastName")
        .claim("role", Role.TRAINEE.name())
        .claim("ext_jti", EXTERNAL_JWT_ID.toString())
        .audience().add("audience-1").and()
        .id(UUID.randomUUID().toString())
        .issuedAt(Date.from(now))
        .expiration(Date.from(validity))
        .signWith(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)))
        .compact();

    assertThrows(
        UnauthenticatedException.class,
        () -> internalJwtService.parse(tokenWithoutScope)
    );
  }

  @Test
  void parse_ShouldThrowUnauthenticatedException_WhenAudienceIsMissing() {
    Instant now = Instant.now();
    Instant validity = now.plusMillis(EXPIRATION_MS);

    String tokenWithoutAudience = Jwts.builder()
        .subject("firstName.lastName")
        .claim("role", Role.TRAINEE.name())
        .claim("ext_jti", EXTERNAL_JWT_ID.toString())
        .claim("scope", "scope:read")
        .id(UUID.randomUUID().toString())
        .issuedAt(Date.from(now))
        .expiration(Date.from(validity))
        .signWith(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)))
        .compact();

    assertThrows(
        UnauthenticatedException.class,
        () -> internalJwtService.parse(tokenWithoutAudience)
    );
  }

  @Test
  void parse_ShouldThrowUnauthenticatedException_WhenJwtIdIsMissing() {
    Instant now = Instant.now();
    Instant validity = now.plusMillis(EXPIRATION_MS);

    String tokenWithoutId = Jwts.builder()
        .subject("firstName.lastName")
        .claim("role", Role.TRAINEE.name())
        .claim("ext_jti", EXTERNAL_JWT_ID.toString())
        .claim("scope", "scope:read")
        .audience().add("audience-1").and()
        .issuedAt(Date.from(now))
        .expiration(Date.from(validity))
        .signWith(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)))
        .compact();

    assertThrows(
        UnauthenticatedException.class,
        () -> internalJwtService.parse(tokenWithoutId)
    );
  }
}
