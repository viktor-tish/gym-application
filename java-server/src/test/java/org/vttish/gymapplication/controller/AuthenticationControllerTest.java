package org.vttish.gymapplication.controller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.vttish.gymapplication.dto.auth.ChangePasswordRequestDto;
import org.vttish.gymapplication.dto.auth.LoginRequestDto;
import org.vttish.gymapplication.exception.AuthenticationException;
import org.vttish.gymapplication.exception.ResourceNotFoundException;
import org.vttish.gymapplication.facade.GymFacade;

class AuthenticationControllerTest {
  private static GymFacade gymFacade;
  private static MockMvc mockMvc;
  private static ObjectMapper objectMapper;

  @BeforeAll
  static void setUp() {
    gymFacade = mock(GymFacade.class);
    AuthenticationController controller = new AuthenticationController(gymFacade);

    mockMvc = MockMvcBuilders
        .standaloneSetup(controller)
        .setControllerAdvice(new GlobalExceptionHandler())
        .build();

    objectMapper = new ObjectMapper();
  }

  @BeforeEach
  void resetMocks() {
    reset(gymFacade);
  }

  @Test
  void login_ShouldReturnOk_WhenCredentialsAreValid() throws Exception {
    LoginRequestDto request = new LoginRequestDto("firstName.lastName", "password123");
    doNothing().when(gymFacade).login(request);

    mockMvc.perform(post("/api/v1/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk());

    verify(gymFacade).login(request);
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
    doThrow(new AuthenticationException("Invalid username or password"))
        .when(gymFacade).login(request);

    mockMvc.perform(post("/api/v1/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void changePassword_ShouldReturnOk_WhenRequestIsValid() throws Exception {
    ChangePasswordRequestDto request = new ChangePasswordRequestDto(
        "firstName.lastName", "oldPassword", "newPassword"
    );
    doNothing().when(gymFacade).changePassword(request);

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
    doThrow(new AuthenticationException("Invalid old password"))
        .when(gymFacade).changePassword(request);

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

    mockMvc.perform(post("/api/v1/auth/change-password")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound());
  }
}
