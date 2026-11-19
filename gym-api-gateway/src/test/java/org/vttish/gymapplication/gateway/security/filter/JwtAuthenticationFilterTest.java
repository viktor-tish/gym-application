package org.vttish.gymapplication.gateway.security.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.vttish.gymapplication.gateway.exception.UnauthenticatedException;
import org.vttish.gymapplication.gateway.security.JwtBlacklistService;
import org.vttish.gymapplication.gateway.security.JwtService;
import org.vttish.gymapplication.gateway.security.model.JwtAuthenticationToken;
import org.vttish.gymapplication.gateway.security.model.JwtMetadata;
import org.vttish.gymapplication.gateway.security.model.ParsedJwt;
import org.vttish.gymapplication.gateway.security.model.Role;
import org.vttish.gymapplication.gateway.security.model.UserPrincipal;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

  @Mock
  private JwtService jwtService;

  @Mock
  private JwtBlacklistService jwtBlacklistService;

  @Mock
  private HttpServletRequest request;

  @Mock
  private HttpServletResponse response;

  @Mock
  private FilterChain filterChain;

  private JwtAuthenticationFilter filter;

  private static UUID jwtId;
  private static UserPrincipal principal;
  private static JwtMetadata metadata;
  private static ParsedJwt authenticatedToken;

  @BeforeAll
  static void setUpAll() {
    jwtId = UUID.randomUUID();
    principal = new UserPrincipal("firstName.lastName", Role.TRAINEE);
    metadata = new JwtMetadata(jwtId, Instant.now().plusMillis(3600000L));
    authenticatedToken = new ParsedJwt(principal, metadata);
  }

  @BeforeEach
  void setUp() {
    filter = new JwtAuthenticationFilter(jwtService, jwtBlacklistService);
    SecurityContextHolder.clearContext();
  }

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void doFilterInternal_ShouldAuthenticate_WhenValidTokenProvided() throws Exception {
    String token = "valid.jwt.token";

    when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);
    when(jwtService.parse(token)).thenReturn(authenticatedToken);
    when(jwtBlacklistService.isBlacklisted(jwtId)).thenReturn(false);

    filter.doFilterInternal(request, response, filterChain);

    JwtAuthenticationToken auth =
        (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

    assertNotNull(auth);
    assertEquals(principal, auth.userPrincipal());
    assertEquals(metadata, auth.jwtMetadata());
    assertEquals(1, auth.getAuthorities().size());
    assertEquals("ROLE_TRAINEE", auth.getAuthorities().iterator().next().getAuthority());
    verify(filterChain).doFilter(request, response);
  }

  @Test
  void doFilterInternal_ShouldNotAuthenticate_WhenTokenIsBlacklisted() throws Exception {
    String token = "blacklisted.jwt.token";

    when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);
    when(jwtService.parse(token)).thenReturn(authenticatedToken);
    when(jwtBlacklistService.isBlacklisted(jwtId)).thenReturn(true);

    filter.doFilterInternal(request, response, filterChain);

    assertNull(SecurityContextHolder.getContext().getAuthentication());
    verify(filterChain).doFilter(request, response);
  }

  @ParameterizedTest
  @NullSource
  @ValueSource(strings = {
      "Basic dXNlcjpwYXNz",
      "Bearer    "
  })
  void doFilterInternal_ShouldNotAuthenticate_WhenAuthorizationHeaderIsMissingOrInvalid(
      String authHeader
  ) throws Exception {
    when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(authHeader);

    filter.doFilterInternal(request, response, filterChain);

    assertNull(SecurityContextHolder.getContext().getAuthentication());
    verify(filterChain).doFilter(request, response);
  }

  @Test
  void doFilterInternal_ShouldNotAuthenticate_WhenInvalidTokenProvided() throws Exception {
    String token = "invalid.token";

    when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);
    when(jwtService.parse(token)).thenThrow(new UnauthenticatedException("Invalid JWT token"));

    filter.doFilterInternal(request, response, filterChain);

    assertNull(SecurityContextHolder.getContext().getAuthentication());
    verify(filterChain).doFilter(request, response);
  }

  @Test
  void doFilterInternal_ShouldNotAuthenticate_WhenUnexpectedExceptionOccurs() throws Exception {
    String token = "some.token";

    when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);
    when(jwtService.parse(token)).thenThrow(new RuntimeException("Database error"));

    filter.doFilterInternal(request, response, filterChain);

    assertNull(SecurityContextHolder.getContext().getAuthentication());
    verify(filterChain).doFilter(request, response);
  }
}
