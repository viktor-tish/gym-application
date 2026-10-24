package org.vttish.gymapplication.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;
import org.vttish.gymapplication.exception.AuthenticationException;
import org.vttish.gymapplication.exception.ForbiddenException;

@ExtendWith(MockitoExtension.class)
class AuthInterceptorTest {

  @Mock
  private JwtService jwtService;

  private AuthInterceptor interceptor;
  private MockHttpServletRequest request;
  private MockHttpServletResponse response;
  private Object handler;

  static class TestOpenController {
    public void openMethod() {
      // Intentionally empty for test purposes
    }
  }

  static class TestRoleController {

    @RequireRole({ Role.TRAINEE })
    public void traineeMethod() {
      // Intentionally empty for test purposes
    }

    @RequireRole({ Role.TRAINER })
    public void trainerMethod() {
      // Intentionally empty for test purposes
    }
  }

  @RequireRole({ Role.TRAINER })
  static class TestClassLevelTrainerController {
    public void defaultMethod() {
      // Intentionally empty for test purposes
    }
  }

  @BeforeEach
  void setUp() {
    List<PublicEndpoint> whiteList = List.of(
        PublicEndpoint.of(HttpMethod.POST, "/api/v1/auth/login"),
        PublicEndpoint.anyMethod("/swagger-ui/**")
    );

    interceptor = new AuthInterceptor(jwtService, whiteList);
    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();
    handler = new Object();
  }

  @Test
  void preHandle_ShouldReturnTrue_WhenMethodIsOptions() {
    request.setMethod("OPTIONS");

    boolean result = interceptor.preHandle(request, response, handler);

    assertTrue(result);
    verifyNoInteractions(jwtService);
  }

  @Test
  void preHandle_ShouldReturnTrue_WhenEndpointIsWhiteListed() {
    request.setMethod("POST");
    request.setRequestURI("/api/v1/auth/login");

    boolean result = interceptor.preHandle(request, response, handler);

    assertTrue(result);
    verifyNoInteractions(jwtService);
  }

  @Test
  void preHandle_ShouldThrowAuthenticationException_WhenAuthHeaderIsMissing() {
    request.setMethod("GET");
    request.setRequestURI("/api/v1/trainees/john.doe");

    assertThrows(
        AuthenticationException.class,
        () -> interceptor.preHandle(request, response, handler)
    );
  }

  @Test
  void preHandle_ShouldThrowAuthenticationException_WhenAuthHeaderDoesNotStartWithBearer() {
    request.setMethod("GET");
    request.setRequestURI("/api/v1/trainees/john.doe");
    request.addHeader(HttpHeaders.AUTHORIZATION, "Basic dXNlcjpwYXNz");

    assertThrows(
        AuthenticationException.class,
        () -> interceptor.preHandle(request, response, handler)
    );
  }

  @Test
  void preHandle_ShouldThrowAuthenticationException_WhenTokenIsBlank() {
    request.setMethod("GET");
    request.setRequestURI("/api/v1/trainees/john.doe");
    request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer    ");

    assertThrows(
        AuthenticationException.class,
        () -> interceptor.preHandle(request, response, handler)
    );
  }

  @Test
  void preHandle_ShouldSetPrincipalAttributeAndReturnTrue_WhenTokenIsValidAndNoRequireRole()
      throws Exception {
    request.setMethod("GET");
    request.setRequestURI("/api/v1/trainees/john.doe");
    request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer valid.jwt.token");

    UserPrincipal principal = new UserPrincipal("john.doe", Role.TRAINEE);
    when(jwtService.parseToken("valid.jwt.token")).thenReturn(principal);

    Method method = TestOpenController.class.getMethod("openMethod");
    HandlerMethod handlerMethod =
        new HandlerMethod(new TestOpenController(), method);

    boolean result = interceptor.preHandle(request, response, handlerMethod);

    assertTrue(result);
    assertNotNull(request.getAttribute("userPrincipal"));
    assertEquals(principal, request.getAttribute("userPrincipal"));
  }

  @Test
  void preHandle_ShouldReturnTrue_WhenMethodRequiresRoleAndUserHasRequiredRole()
      throws Exception {
    request.setMethod("GET");
    request.setRequestURI("/api/v1/trainees/john.doe");
    request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer valid.jwt.token");

    UserPrincipal principal = new UserPrincipal("john.doe", Role.TRAINEE);
    when(jwtService.parseToken("valid.jwt.token")).thenReturn(principal);

    Method method = TestRoleController.class.getMethod("traineeMethod");
    HandlerMethod handlerMethod =
        new HandlerMethod(new TestRoleController(), method);

    boolean result = interceptor.preHandle(request, response, handlerMethod);

    assertTrue(result);
  }

  @Test
  void preHandle_ShouldThrowForbiddenException_WhenMethodRequiresRoleAndUserLacksRequiredRole()
      throws Exception {
    request.setMethod("GET");
    request.setRequestURI("/api/v1/trainees/john.doe");
    request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer valid.jwt.token");

    UserPrincipal principal = new UserPrincipal("john.doe", Role.TRAINEE);
    when(jwtService.parseToken("valid.jwt.token")).thenReturn(principal);

    Method method = TestRoleController.class.getMethod("trainerMethod");
    HandlerMethod handlerMethod =
        new HandlerMethod(new TestRoleController(), method);

    assertThrows(
        ForbiddenException.class,
        () -> interceptor.preHandle(request, response, handlerMethod)
    );
  }

  @Test
  void preHandle_ShouldCheckClassLevelRequireRole_WhenMethodHasNoAnnotation()
      throws Exception {
    request.setMethod("GET");
    request.setRequestURI("/api/v1/trainers/jane.smith");
    request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer valid.jwt.token");

    UserPrincipal principal = new UserPrincipal("john.doe", Role.TRAINEE);
    when(jwtService.parseToken("valid.jwt.token")).thenReturn(principal);

    Method method = TestClassLevelTrainerController.class.getMethod("defaultMethod");
    HandlerMethod handlerMethod = new HandlerMethod(new TestClassLevelTrainerController(), method);

    assertThrows(
        ForbiddenException.class,
        () -> interceptor.preHandle(request, response, handlerMethod)
    );
  }

  @Test
  void preHandle_ShouldReturnTrue_WhenHandlerIsNotHandlerMethod() {
    request.setMethod("GET");
    request.setRequestURI("/api/v1/test");
    request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer valid.jwt.token");

    UserPrincipal principal = new UserPrincipal("john.doe", Role.TRAINEE);
    when(jwtService.parseToken("valid.jwt.token")).thenReturn(principal);

    boolean result = interceptor.preHandle(request, response, handler);

    assertTrue(result);
  }

  @Test
  void preHandle_ShouldWork_WhenWhiteListIsNull() {
    request.setMethod("GET");
    request.setRequestURI("/api/v1/test");
    request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer valid.jwt.token");

    UserPrincipal principal = new UserPrincipal("john.doe", Role.TRAINEE);
    when(jwtService.parseToken("valid.jwt.token")).thenReturn(principal);

    AuthInterceptor interceptorWithoutWhiteList = new AuthInterceptor(jwtService, null);

    boolean result = interceptorWithoutWhiteList.preHandle(request, response, handler);

    assertTrue(result);
  }

  @Test
  void preHandle_ShouldReturnTrue_WhenUsernamePathVariableMatchesPrincipal() throws Exception {
    request.setMethod("GET");
    request.setRequestURI("/api/v1/trainees/john.doe");
    request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer valid.jwt.token");
    request.setAttribute(
        HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE,
        Map.of("username", "john.doe")
    );

    UserPrincipal principal = new UserPrincipal("john.doe", Role.TRAINEE);
    when(jwtService.parseToken("valid.jwt.token")).thenReturn(principal);

    Method method = TestOpenController.class.getMethod("openMethod");
    HandlerMethod handlerMethod = new HandlerMethod(new TestOpenController(), method);

    boolean result = interceptor.preHandle(request, response, handlerMethod);

    assertTrue(result);
  }

  @Test
  void preHandle_ShouldThrowForbiddenException_WhenUsernamePathVariableDoesNotMatchPrincipal()
      throws Exception {
    request.setMethod("GET");
    request.setRequestURI("/api/v1/trainees/jane.smith");
    request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer valid.jwt.token");
    request.setAttribute(
        HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE,
        Map.of("username", "jane.smith")
    );

    UserPrincipal principal = new UserPrincipal("john.doe", Role.TRAINEE);
    when(jwtService.parseToken("valid.jwt.token")).thenReturn(principal);

    Method method = TestOpenController.class.getMethod("openMethod");
    HandlerMethod handlerMethod = new HandlerMethod(new TestOpenController(), method);

    assertThrows(
        ForbiddenException.class,
        () -> interceptor.preHandle(request, response, handlerMethod)
    );
  }
}
