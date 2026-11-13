package org.vttish.gymapplication.workloadreport.security.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.util.List;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.vttish.gymapplication.workloadreport.exception.UnauthenticatedException;
import org.vttish.gymapplication.workloadreport.security.InternalJwtService;
import org.vttish.gymapplication.workloadreport.security.model.InternalJwtMetadata;
import org.vttish.gymapplication.workloadreport.security.model.ParsedInternalJwt;
import org.vttish.gymapplication.workloadreport.security.model.Role;
import org.vttish.gymapplication.workloadreport.security.model.UserPrincipal;

@ExtendWith(MockitoExtension.class)
class InternalJwtAuthenticationFilterTest {

  @Mock
  private InternalJwtService internalJwtService;

  @Mock
  private HttpServletRequest request;

  @Mock
  private HttpServletResponse response;

  @Mock
  private FilterChain filterChain;

  private InternalJwtAuthenticationFilter filter;

  private static final String APP_NAME = "gym-application";
  private static UUID jwtId;
  private static UUID externalJwtId;
  private static UserPrincipal principal;
  private static ParsedInternalJwt parsedInternalJwt;

  @BeforeAll
  static void setUpAll() {
    jwtId = UUID.randomUUID();
    externalJwtId = UUID.randomUUID();
    principal = new UserPrincipal("firstName.lastName", Role.TRAINEE);

    InternalJwtMetadata metadata = new InternalJwtMetadata(
        jwtId,
        externalJwtId,
        List.of(APP_NAME),
        List.of("scope:read", "scope:write"),
        Instant.now().plusMillis(3600000L)
    );

    parsedInternalJwt = new ParsedInternalJwt(principal, metadata);
  }

  @BeforeEach
  void setUp() {
    filter = new InternalJwtAuthenticationFilter(APP_NAME, internalJwtService);
    SecurityContextHolder.clearContext();
  }

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void doFilterInternal_ShouldAuthenticate_WhenValidTokenAndMatchingAudienceProvided()
      throws Exception {
    String token = "valid.internal.jwt.token";

    when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);
    when(internalJwtService.parse(token)).thenReturn(parsedInternalJwt);

    filter.doFilterInternal(request, response, filterChain);

    UsernamePasswordAuthenticationToken auth =
        (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext()
            .getAuthentication();

    assertNotNull(auth);
    assertEquals(principal, auth.getPrincipal());
    assertEquals(3, auth.getAuthorities().size());

    boolean hasRole = auth.getAuthorities().stream()
        .anyMatch(a -> a.getAuthority().equals("ROLE_TRAINEE"));
    boolean hasReadScope = auth.getAuthorities().stream()
        .anyMatch(a -> a.getAuthority().equals("SCOPE_scope:read"));
    boolean hasWriteScope = auth.getAuthorities().stream()
        .anyMatch(a -> a.getAuthority().equals("SCOPE_scope:write"));

    assertTrue(hasRole);
    assertTrue(hasReadScope);
    assertTrue(hasWriteScope);

    verify(filterChain).doFilter(request, response);
  }

  @Test
  void doFilterInternal_ShouldNotAuthenticate_WhenAudienceDoesNotMatch() throws Exception {
    String token = "valid.token.wrong.audience";
    InternalJwtMetadata wrongAudienceMetadata = new InternalJwtMetadata(
        jwtId,
        externalJwtId,
        List.of("other-service"),
        List.of("scope:read"),
        Instant.now().plusMillis(3600000L)
    );
    ParsedInternalJwt wrongAudienceParsed = new ParsedInternalJwt(principal, wrongAudienceMetadata);

    when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);
    when(internalJwtService.parse(token)).thenReturn(wrongAudienceParsed);

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
    when(internalJwtService.parse(token))
        .thenThrow(new UnauthenticatedException("Invalid JWT token"));

    filter.doFilterInternal(request, response, filterChain);

    assertNull(SecurityContextHolder.getContext().getAuthentication());
    verify(filterChain).doFilter(request, response);
  }

  @Test
  void doFilterInternal_ShouldNotAuthenticate_WhenUnexpectedExceptionOccurs() throws Exception {
    String token = "some.token";

    when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);
    when(internalJwtService.parse(token)).thenThrow(new RuntimeException("Database error"));

    filter.doFilterInternal(request, response, filterChain);

    assertNull(SecurityContextHolder.getContext().getAuthentication());
    verify(filterChain).doFilter(request, response);
  }
}
