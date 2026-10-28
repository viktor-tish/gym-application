package org.vttish.gymapplication.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.vttish.gymapplication.exception.AuthenticationException;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

  @Mock
  private JwtService jwtService;

  @Mock
  private HttpServletRequest request;

  @Mock
  private HttpServletResponse response;

  @Mock
  private FilterChain filterChain;

  private JwtAuthenticationFilter filter;

  @BeforeEach
  void setUp() {
    filter = new JwtAuthenticationFilter(jwtService);
    SecurityContextHolder.clearContext();
  }

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void doFilterInternal_ShouldAuthenticate_WhenValidTokenProvided() throws Exception {
    String token = "valid.jwt.token";
    UserPrincipal principal = new UserPrincipal("firstName.lastName", Role.TRAINEE);

    when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);
    when(jwtService.parseToken(token)).thenReturn(principal);

    filter.doFilterInternal(request, response, filterChain);

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    assertNotNull(auth);
    assertEquals(principal, auth.getPrincipal());
    assertEquals(1, auth.getAuthorities().size());
    verify(request).setAttribute("userPrincipal", principal);
    verify(filterChain).doFilter(request, response);
  }

  @Test
  void doFilterInternal_ShouldNotAuthenticate_WhenAuthorizationHeaderMissing() throws Exception {
    when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);

    filter.doFilterInternal(request, response, filterChain);

    assertNull(SecurityContextHolder.getContext().getAuthentication());
    verify(filterChain).doFilter(request, response);
  }

  @Test
  void doFilterInternal_ShouldNotAuthenticate_WhenInvalidTokenProvided() throws Exception {
    String token = "invalid.token";

    when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);
    when(jwtService.parseToken(token)).thenThrow(new AuthenticationException("Invalid JWT token"));

    filter.doFilterInternal(request, response, filterChain);

    assertNull(SecurityContextHolder.getContext().getAuthentication());
    verify(filterChain).doFilter(request, response);
  }
}
