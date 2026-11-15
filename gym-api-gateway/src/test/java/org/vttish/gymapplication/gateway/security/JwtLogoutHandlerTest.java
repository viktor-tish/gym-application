package org.vttish.gymapplication.gateway.security;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.vttish.gymapplication.gateway.security.model.JwtAuthenticationToken;
import org.vttish.gymapplication.gateway.security.model.JwtMetadata;

@ExtendWith(MockitoExtension.class)
class JwtLogoutHandlerTest {

  @Mock
  private JwtBlacklistService jwtBlacklistService;

  @Mock
  private HttpServletRequest request;

  @Mock
  private HttpServletResponse response;

  @Mock
  private JwtAuthenticationToken jwtAuthenticationToken;

  private JwtLogoutHandler logoutHandler;

  private static final UUID JWT_ID = UUID.randomUUID();
  private static final Instant EXPIRATION = Instant.now().plusMillis(3600000L);
  private static final JwtMetadata JWT_METADATA = new JwtMetadata(JWT_ID, EXPIRATION);

  @BeforeEach
  void setUp() {
    logoutHandler = new JwtLogoutHandler(jwtBlacklistService);
    SecurityContextHolder.clearContext();
  }

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void logout_ShouldBlacklistTokenAndClearContext_WhenValidAuthenticationProvided() {
    when(jwtAuthenticationToken.jwtMetadata()).thenReturn(JWT_METADATA);

    logoutHandler.logout(request, response, jwtAuthenticationToken);

    verify(jwtBlacklistService).blacklist(JWT_ID, EXPIRATION);
    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }

  @Test
  void logout_ShouldClearContextOnly_WhenAuthenticationIsNull() {
    logoutHandler.logout(request, response, null);

    verifyNoInteractions(jwtBlacklistService);
    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }

  @Test
  void logout_ShouldClearContextOnly_authenticationIsNotJwtAuthenticationToken() {
    logoutHandler.logout(request, response, mock(Authentication.class));

    verifyNoInteractions(jwtBlacklistService);
    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }
}
