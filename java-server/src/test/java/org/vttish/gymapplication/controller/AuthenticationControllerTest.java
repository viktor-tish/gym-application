package org.vttish.gymapplication.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.vttish.gymapplication.dto.auth.ChangePasswordRequestDto;
import org.vttish.gymapplication.dto.auth.LoginRequestDto;
import org.vttish.gymapplication.dto.auth.LoginResponseDto;
import org.vttish.gymapplication.exception.ResourceNotFoundException;
import org.vttish.gymapplication.exception.UnauthenticatedException;
import org.vttish.gymapplication.facade.GymFacade;
import org.vttish.gymapplication.security.ClientIpResolver;
import org.vttish.gymapplication.security.UserPrincipalArgumentResolver;
import org.vttish.gymapplication.security.model.Role;
import org.vttish.gymapplication.security.model.UserPrincipal;

class AuthenticationControllerTest {
  private static GymFacade gymFacade;
  private static ClientIpResolver clientIpResolver;
  private static MockMvc mockMvc;
  private static ObjectMapper objectMapper;

  private static final String CLIENT_IP = "127.0.0.1";

  @BeforeAll
  static void setUp() {
    gymFacade = mock(GymFacade.class);
    clientIpResolver = mock(ClientIpResolver.class);
    AuthenticationController controller = new AuthenticationController(gymFacade, clientIpResolver);

    mockMvc = MockMvcBuilders
        .standaloneSetup(controller)
        .setCustomArgumentResolvers(new UserPrincipalArgumentResolver())
        .setControllerAdvice(new GlobalExceptionHandler())
        .build();

    objectMapper = new ObjectMapper();
  }

  @BeforeEach
  void resetMocks() {
    reset(gymFacade, clientIpResolver);
    SecurityContextHolder.clearContext();
  }

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  private void setAuthentication(UserPrincipal principal) {
    if (principal == null) {
      SecurityContextHolder.clearContext();
      return;
    }
    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
        principal,
        null,
        List.of(new SimpleGrantedAuthority("ROLE_" + principal.role().name()))
    );
    SecurityContextHolder.getContext().setAuthentication(authentication);
  }

  @Test
  void login_ShouldReturnOk_WhenCredentialsAreValid() throws Exception {
    LoginRequestDto request = new LoginRequestDto("firstName.lastName", "password123");
    LoginResponseDto response = new LoginResponseDto("mocked.jwt.token");

    when(clientIpResolver.resolve(any())).thenReturn(CLIENT_IP);
    when(gymFacade.login(request, CLIENT_IP)).thenReturn(response);

    MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andReturn();

    LoginResponseDto actual = objectMapper.readValue(
        result.getResponse().getContentAsString(),
        LoginResponseDto.class
    );

    assertEquals(response, actual);
    verify(clientIpResolver).resolve(any());
    verify(gymFacade).login(request, CLIENT_IP);
  }

  @Test
  void login_ShouldReturnBadRequest_WhenPayloadIsInvalid() throws Exception {
    LoginRequestDto invalidRequest = new LoginRequestDto("", "");

    mockMvc.perform(post("/api/v1/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void login_ShouldReturnUnauthorized_WhenCredentialsAreIncorrect() throws Exception {
    LoginRequestDto request = new LoginRequestDto("firstName.lastName", "wrongPassword");

    when(clientIpResolver.resolve(any())).thenReturn(CLIENT_IP);
    doThrow(new UnauthenticatedException("Invalid username or password"))
        .when(gymFacade).login(request, CLIENT_IP);

    mockMvc.perform(post("/api/v1/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isUnauthorized());

    verify(clientIpResolver).resolve(any());
    verify(gymFacade).login(request, CLIENT_IP);
  }

  @Test
  void changePassword_ShouldReturnOk_WhenRequestIsValid() throws Exception {
    ChangePasswordRequestDto request = new ChangePasswordRequestDto(
        "firstName.lastName", "oldPassword", "newPassword"
    );
    doNothing().when(gymFacade).changePassword(request);
    setAuthentication(new UserPrincipal("firstName.lastName", Role.TRAINEE));

    mockMvc.perform(post("/api/v1/auth/change-password")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk());

    verify(gymFacade).changePassword(request);
  }

  @Test
  void changePassword_ShouldReturnBadRequest_WhenPayloadIsInvalid() throws Exception {
    ChangePasswordRequestDto invalidRequest = new ChangePasswordRequestDto(
        "", "", ""
    );
    setAuthentication(new UserPrincipal("firstName.lastName", Role.TRAINEE));

    mockMvc.perform(post("/api/v1/auth/change-password")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void changePassword_ShouldReturnUnauthorized_WhenOldPasswordIsInvalid() throws Exception {
    ChangePasswordRequestDto request = new ChangePasswordRequestDto(
        "firstName.lastName", "wrongOldPassword", "newPassword"
    );
    doThrow(new UnauthenticatedException("Invalid old password"))
        .when(gymFacade).changePassword(request);
    setAuthentication(new UserPrincipal("firstName.lastName", Role.TRAINEE));

    mockMvc.perform(post("/api/v1/auth/change-password")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void changePassword_ShouldReturnNotFound_WhenUserDoesNotExist() throws Exception {
    ChangePasswordRequestDto request = new ChangePasswordRequestDto(
        "nonexistent.user", "oldPassword", "newPassword"
    );
    doThrow(new ResourceNotFoundException("User not found with username: nonexistent.user"))
        .when(gymFacade).changePassword(request);
    setAuthentication(new UserPrincipal("nonexistent.user", Role.TRAINEE));

    mockMvc.perform(post("/api/v1/auth/change-password")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound());
  }

  @Test
  void changePassword_ShouldReturnForbidden_WhenUsernameDoesNotMatchPrincipal() throws Exception {
    ChangePasswordRequestDto request = new ChangePasswordRequestDto(
        "firstName.lastName", "oldPassword", "newPassword"
    );
    setAuthentication(new UserPrincipal("other.user", Role.TRAINEE));

    mockMvc.perform(post("/api/v1/auth/change-password")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isForbidden());
  }

  @Test
  void changePassword_ShouldReturnForbidden_WhenPrincipalIsNull() throws Exception {
    ChangePasswordRequestDto request = new ChangePasswordRequestDto(
        "firstName.lastName", "oldPassword", "newPassword"
    );

    mockMvc.perform(post("/api/v1/auth/change-password")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isForbidden());
  }
}
