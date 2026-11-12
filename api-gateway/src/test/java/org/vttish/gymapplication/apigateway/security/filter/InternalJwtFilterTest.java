package org.vttish.gymapplication.apigateway.security.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.vttish.gymapplication.apigateway.security.ClientIpResolver;
import org.vttish.gymapplication.apigateway.security.InternalJwtScopePolicy;
import org.vttish.gymapplication.apigateway.security.InternalJwtService;
import org.vttish.gymapplication.apigateway.security.model.JwtAuthenticationToken;
import org.vttish.gymapplication.apigateway.security.model.JwtMetadata;
import org.vttish.gymapplication.apigateway.security.model.Role;
import org.vttish.gymapplication.apigateway.security.model.UserPrincipal;
import org.vttish.gymapplication.apigateway.security.properties.InternalJwtProperties;

@ExtendWith(MockitoExtension.class)
class InternalJwtFilterTest {

  @Mock
  private InternalJwtScopePolicy policy;

  @Mock
  private InternalJwtService internalJwtService;

  @Mock
  private ClientIpResolver clientIpResolver;

  @Mock
  private HttpServletRequest request;

  @Mock
  private HttpServletResponse response;

  @Mock
  private FilterChain filterChain;

  @InjectMocks
  private InternalJwtFilter internalJwtFilter;

  private static final UserPrincipal PRINCIPAL = new UserPrincipal("testUser", Role.TRAINEE);
  private static final JwtMetadata METADATA =
      new JwtMetadata(UUID.randomUUID(), Instant.now().plusMillis(3600000L));
  private static final InternalJwtProperties.Rule SAMPLE_RULE = new InternalJwtProperties.Rule(
      Set.of(InternalJwtProperties.Verb.GET),
      "/api/v1/resource",
      List.of("audience-1"),
      List.of("scope:read")
  );

  @BeforeEach
  void setUp() {
    SecurityContextHolder.clearContext();
  }

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void doFilterInternal_ShouldNotGenerateJwt_WhenAuthenticationIsNull() throws Exception {
    when(clientIpResolver.resolve(request)).thenReturn("192.168.1.100");

    internalJwtFilter.doFilter(request, response, filterChain);

    verify(clientIpResolver).resolve(request);
    verifyNoInteractions(policy);
    verifyNoInteractions(internalJwtService);

    ArgumentCaptor<HttpServletRequest> requestCaptor =
        ArgumentCaptor.forClass(HttpServletRequest.class);
    verify(filterChain).doFilter(requestCaptor.capture(), eqResponse());

    assertNull(requestCaptor.getValue().getHeader(HttpHeaders.AUTHORIZATION));
    assertEquals("192.168.1.100", requestCaptor.getValue().getHeader("X-Client-Ip"));
  }

  @Test
  void doFilterInternal_ShouldNotGenerateJwt_WhenRuleDoesNotMatch() throws Exception {
    JwtAuthenticationToken auth = new JwtAuthenticationToken(PRINCIPAL, METADATA, List.of());
    SecurityContextHolder.getContext().setAuthentication(auth);

    when(clientIpResolver.resolve(request)).thenReturn("192.168.1.100");
    when(request.getMethod()).thenReturn("GET");
    when(request.getRequestURI()).thenReturn("/api/v1/unmatched");
    when(policy.match("GET", "/api/v1/unmatched")).thenReturn(Optional.empty());

    internalJwtFilter.doFilter(request, response, filterChain);

    verify(clientIpResolver).resolve(request);
    verify(policy).match("GET", "/api/v1/unmatched");
    verifyNoInteractions(internalJwtService);

    ArgumentCaptor<HttpServletRequest> requestCaptor =
        ArgumentCaptor.forClass(HttpServletRequest.class);
    verify(filterChain).doFilter(requestCaptor.capture(), eqResponse());
    assertNull(requestCaptor.getValue().getHeader(HttpHeaders.AUTHORIZATION));
    assertEquals("192.168.1.100", requestCaptor.getValue().getHeader("X-Client-Ip"));
  }

  @Test
  void doFilterInternal_ShouldSetAuthorizationHeader_WhenAuthenticatedAndRuleMatches()
      throws Exception {
    JwtAuthenticationToken auth = new JwtAuthenticationToken(PRINCIPAL, METADATA, List.of());
    SecurityContextHolder.getContext().setAuthentication(auth);

    when(clientIpResolver.resolve(request)).thenReturn("192.168.1.100");
    when(request.getMethod()).thenReturn("GET");
    when(request.getRequestURI()).thenReturn("/api/v1/resource");
    when(policy.match("GET", "/api/v1/resource")).thenReturn(Optional.of(SAMPLE_RULE));
    when(internalJwtService.generate(PRINCIPAL, METADATA, SAMPLE_RULE))
        .thenReturn("mocked-internal-jwt-token");

    internalJwtFilter.doFilter(request, response, filterChain);

    verify(clientIpResolver).resolve(request);
    verify(policy).match("GET", "/api/v1/resource");
    verify(internalJwtService).generate(PRINCIPAL, METADATA, SAMPLE_RULE);

    ArgumentCaptor<HttpServletRequest> requestCaptor =
        ArgumentCaptor.forClass(HttpServletRequest.class);
    verify(filterChain).doFilter(requestCaptor.capture(), eqResponse());

    assertEquals("Bearer mocked-internal-jwt-token", requestCaptor.getValue()
        .getHeader(HttpHeaders.AUTHORIZATION));
    assertEquals("192.168.1.100", requestCaptor.getValue().getHeader("X-Client-Ip"));
  }

  private HttpServletResponse eqResponse() {
    return any(HttpServletResponse.class);
  }
}
