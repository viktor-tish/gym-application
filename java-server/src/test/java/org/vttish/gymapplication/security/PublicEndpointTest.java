package org.vttish.gymapplication.security;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;

class PublicEndpointTest {

  @Test
  void matches_ShouldReturnTrue_WhenMethodAndPatternMatch() {
    PublicEndpoint endpoint = PublicEndpoint.of(HttpMethod.POST, "/api/v1/auth/login");
    MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/v1/auth/login");

    assertTrue(endpoint.matches(request));
  }

  @Test
  void matches_ShouldReturnFalse_WhenMethodDiffers() {
    PublicEndpoint endpoint = PublicEndpoint.of(HttpMethod.POST, "/api/v1/auth/login");
    MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/auth/login");

    assertFalse(endpoint.matches(request));
  }

  @Test
  void matches_ShouldReturnFalse_WhenPathDiffers() {
    PublicEndpoint endpoint = PublicEndpoint.of(HttpMethod.POST, "/api/v1/auth/login");
    MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/v1/other");

    assertFalse(endpoint.matches(request));
  }

  @Test
  void matches_ShouldReturnTrue_WhenEndpointHasNullMethodAndPatternMatches() {
    PublicEndpoint endpoint = PublicEndpoint.anyMethod("/swagger-ui/**");
    MockHttpServletRequest getRequest = new MockHttpServletRequest("GET", "/swagger-ui/index.html");
    MockHttpServletRequest postRequest = new MockHttpServletRequest("POST", "/swagger-ui/oauth");

    assertTrue(endpoint.matches(getRequest));
    assertTrue(endpoint.matches(postRequest));
  }

  @Test
  void matches_ShouldReturnFalse_WhenRequestIsNull() {
    PublicEndpoint endpoint = PublicEndpoint.of(HttpMethod.GET, "/api/v1/test");

    assertFalse(endpoint.matches(null));
  }
}
