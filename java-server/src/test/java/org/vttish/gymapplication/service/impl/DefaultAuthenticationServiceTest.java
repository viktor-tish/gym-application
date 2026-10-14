package org.vttish.gymapplication.service.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
import org.vttish.gymapplication.dto.auth.ChangePasswordRequestDto;
import org.vttish.gymapplication.dto.auth.LoginRequestDto;
import org.vttish.gymapplication.dto.auth.UpdateUserStatusRequestDto;
import org.vttish.gymapplication.entity.User;
import org.vttish.gymapplication.exception.AuthenticationException;
import org.vttish.gymapplication.exception.BadRequestException;
import org.vttish.gymapplication.exception.ResourceNotFoundException;
import org.vttish.gymapplication.repository.UserRepository;
import org.vttish.gymapplication.utils.PasswordHasher;

@ExtendWith(MockitoExtension.class)
class DefaultAuthenticationServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private PasswordHasher passwordHasher;

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
  void login_ShouldSucceed_WhenCredentialsMatch() {
    LoginRequestDto dto = new LoginRequestDto("firstName.lastName", "plainPassword");

    when(userRepository.findByUsername("firstName.lastName")).thenReturn(Optional.of(testUser));
    when(passwordHasher.matches("plainPassword", "hashedPassword")).thenReturn(true);

    assertDoesNotThrow(() -> authenticationService.login(dto));
  }

  @Test
  void login_ShouldThrowException_WhenPasswordDoesNotMatch() {
    LoginRequestDto dto = new LoginRequestDto("firstName.lastName", "wrongPassword");

    when(userRepository.findByUsername("firstName.lastName")).thenReturn(Optional.of(testUser));
    when(passwordHasher.matches("wrongPassword", "hashedPassword")).thenReturn(false);

    assertThrows(
        AuthenticationException.class, () -> authenticationService.login(dto));
  }

  @Test
  void login_ShouldThrowException_WhenUserNotFound() {
    LoginRequestDto dto = new LoginRequestDto("firstName.lastName", "plainPassword");

    when(userRepository.findByUsername("firstName.lastName")).thenReturn(Optional.empty());

    assertThrows(
        AuthenticationException.class, () -> authenticationService.login(dto));
  }

  @Test
  void changePassword_ShouldUpdatePassword_WhenOldPasswordIsCorrect() {
    when(userRepository.findByUsername("firstName.lastName")).thenReturn(Optional.of(testUser));
    when(passwordHasher.matches("plainPassword", "hashedPassword")).thenReturn(true);
    when(passwordHasher.hash("newPlainPassword")).thenReturn("newHashedPassword");

    ChangePasswordRequestDto dto = new ChangePasswordRequestDto(
        "firstName.lastName", "plainPassword", "newPlainPassword"
    );

    authenticationService.changePassword(dto);

    verify(passwordHasher).hash("newPlainPassword");
  }

  @Test
  void changePassword_ShouldThrowException_WhenOldPasswordIsWrong() {
    ChangePasswordRequestDto dto = new ChangePasswordRequestDto(
        "firstName.lastName", "wrongPassword", "newPlainPassword"
    );

    when(userRepository.findByUsername("firstName.lastName")).thenReturn(Optional.of(testUser));
    when(passwordHasher.matches("wrongPassword", "hashedPassword")).thenReturn(false);

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
