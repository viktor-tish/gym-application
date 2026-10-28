package org.vttish.gymapplication.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.vttish.gymapplication.actuator.metrics.GymMetrics;
import org.vttish.gymapplication.dto.auth.ChangePasswordRequestDto;
import org.vttish.gymapplication.dto.auth.LoginRequestDto;
import org.vttish.gymapplication.dto.auth.LoginResponseDto;
import org.vttish.gymapplication.dto.auth.UpdateUserStatusRequestDto;
import org.vttish.gymapplication.entity.User;
import org.vttish.gymapplication.exception.AuthenticationException;
import org.vttish.gymapplication.exception.BadRequestException;
import org.vttish.gymapplication.exception.ResourceNotFoundException;
import org.vttish.gymapplication.repository.TraineeRepository;
import org.vttish.gymapplication.repository.TrainerRepository;
import org.vttish.gymapplication.repository.UserRepository;
import org.vttish.gymapplication.security.JwtService;
import org.vttish.gymapplication.security.Role;
import org.vttish.gymapplication.security.UserPrincipal;

@ExtendWith(MockitoExtension.class)
class DefaultAuthenticationServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private TraineeRepository traineeRepository;

  @Mock
  private TrainerRepository trainerRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private JwtService jwtService;

  @Mock
  private GymMetrics gymMetrics;

  @InjectMocks
  private DefaultAuthenticationService authenticationService;

  private User testUser;

  @BeforeEach
  void setUp() {
    testUser = new User();
    testUser.setUsername("firstName.lastName");
    testUser.setPassword("hashedPassword");
    testUser.setIsActive(true);
  }

  @Test
  void login_ShouldReturnToken_WhenCredentialsMatchAndUserIsTrainee() {
    LoginRequestDto dto = new LoginRequestDto("firstName.lastName", "plainPassword");
    UserPrincipal principal = new UserPrincipal("firstName.lastName", Role.TRAINEE);

    when(userRepository.findByUsername("firstName.lastName")).thenReturn(Optional.of(testUser));
    when(passwordEncoder.matches("plainPassword", "hashedPassword")).thenReturn(true);
    when(traineeRepository.existsByUsername("firstName.lastName")).thenReturn(true);
    when(jwtService.generateToken(principal)).thenReturn("jwt.token.trainee");

    LoginResponseDto response = authenticationService.login(dto);

    assertNotNull(response);
    assertEquals("jwt.token.trainee", response.token());
    verify(gymMetrics).incrementLoginSuccess();
  }

  @Test
  void login_ShouldReturnToken_WhenCredentialsMatchAndUserIsTrainer() {
    LoginRequestDto dto = new LoginRequestDto("firstName.lastName", "plainPassword");
    UserPrincipal principal = new UserPrincipal("firstName.lastName", Role.TRAINER);

    when(userRepository.findByUsername("firstName.lastName")).thenReturn(Optional.of(testUser));
    when(passwordEncoder.matches("plainPassword", "hashedPassword")).thenReturn(true);
    when(traineeRepository.existsByUsername("firstName.lastName")).thenReturn(false);
    when(trainerRepository.existsByUsername("firstName.lastName")).thenReturn(true);
    when(jwtService.generateToken(principal)).thenReturn("jwt.token.trainer");

    LoginResponseDto response = authenticationService.login(dto);

    assertNotNull(response);
    assertEquals("jwt.token.trainer", response.token());
    verify(gymMetrics).incrementLoginSuccess();
  }

  @Test
  void login_ShouldThrowException_WhenUserHasNoAssignedRole() {
    when(userRepository.findByUsername("firstName.lastName")).thenReturn(Optional.of(testUser));
    when(passwordEncoder.matches("plainPassword", "hashedPassword")).thenReturn(true);
    when(traineeRepository.existsByUsername("firstName.lastName")).thenReturn(false);
    when(trainerRepository.existsByUsername("firstName.lastName")).thenReturn(false);

    LoginRequestDto dto = new LoginRequestDto("firstName.lastName", "plainPassword");

    assertThrows(
        AuthenticationException.class, () -> authenticationService.login(dto));
    verify(gymMetrics).incrementLoginFailure();
  }

  @Test
  void login_ShouldThrowException_WhenPasswordDoesNotMatch() {
    LoginRequestDto dto = new LoginRequestDto("firstName.lastName", "wrongPassword");

    when(userRepository.findByUsername("firstName.lastName")).thenReturn(Optional.of(testUser));
    when(passwordEncoder.matches("wrongPassword", "hashedPassword")).thenReturn(false);

    assertThrows(
        AuthenticationException.class, () -> authenticationService.login(dto));
    verify(gymMetrics).incrementLoginFailure();
  }

  @Test
  void login_ShouldThrowException_WhenUserNotFound() {
    LoginRequestDto dto = new LoginRequestDto("firstName.lastName", "plainPassword");

    when(userRepository.findByUsername("firstName.lastName")).thenReturn(Optional.empty());

    assertThrows(
        AuthenticationException.class, () -> authenticationService.login(dto));
    verify(gymMetrics).incrementLoginFailure();
  }

  @Test
  void changePassword_ShouldUpdatePassword_WhenOldPasswordIsCorrect() {
    when(userRepository.findByUsername("firstName.lastName")).thenReturn(Optional.of(testUser));
    when(passwordEncoder.matches("plainPassword", "hashedPassword")).thenReturn(true);
    when(passwordEncoder.encode("newPlainPassword")).thenReturn("newHashedPassword");

    ChangePasswordRequestDto dto = new ChangePasswordRequestDto(
        "firstName.lastName", "plainPassword", "newPlainPassword"
    );

    authenticationService.changePassword(dto);

    verify(passwordEncoder).encode("newPlainPassword");
  }

  @Test
  void changePassword_ShouldThrowException_WhenOldPasswordIsWrong() {
    ChangePasswordRequestDto dto = new ChangePasswordRequestDto(
        "firstName.lastName", "wrongPassword", "newPlainPassword"
    );

    when(userRepository.findByUsername("firstName.lastName")).thenReturn(Optional.of(testUser));
    when(passwordEncoder.matches("wrongPassword", "hashedPassword")).thenReturn(false);

    assertThrows(
        AuthenticationException.class, () -> authenticationService.changePassword(dto));
  }

  @Test
  void changePassword_ShouldThrowException_WhenUserNotFound() {
    ChangePasswordRequestDto dto = new ChangePasswordRequestDto(
        "nonexistent.username", "plainPassword", "newPlainPassword"
    );

    when(userRepository.findByUsername("nonexistent.username")).thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class, () -> authenticationService.changePassword(dto));
  }

  @Test
  void updateStatus_ShouldUpdateIsActive_WhenTargetStatusDiffers() {
    UpdateUserStatusRequestDto dto = new UpdateUserStatusRequestDto(false);

    when(userRepository.findByUsername("firstName.lastName")).thenReturn(Optional.of(testUser));

    authenticationService.updateStatus("firstName.lastName", dto);

    assertFalse(testUser.getIsActive());
  }

  @Test
  void updateStatus_ShouldThrowException_WhenStatusAlreadyMatches() {
    UpdateUserStatusRequestDto dto = new UpdateUserStatusRequestDto(true);

    when(userRepository.findByUsername("firstName.lastName")).thenReturn(Optional.of(testUser));

    assertThrows(
        BadRequestException.class,
        () -> authenticationService.updateStatus("firstName.lastName", dto)
    );
  }

  @Test
  void updateStatus_ShouldThrowException_WhenUserNotFound() {
    UpdateUserStatusRequestDto dto = new UpdateUserStatusRequestDto(false);

    when(userRepository.findByUsername("nonexistent.username")).thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class,
        () -> authenticationService.updateStatus("nonexistent.username", dto)
    );
  }
}
