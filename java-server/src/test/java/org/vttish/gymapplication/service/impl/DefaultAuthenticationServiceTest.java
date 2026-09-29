package org.vttish.gymapplication.service.impl;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.vttish.gymapplication.dto.ChangePasswordDto;
import org.vttish.gymapplication.dto.CredentialsDto;
import org.vttish.gymapplication.entity.User;
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
  void authenticate_ShouldReturnTrue_WhenCredentialsMatch() {
    CredentialsDto dto = new CredentialsDto("firstName.lastName", "plainPassword");

    when(userRepository.findByUsername("firstName.lastName")).thenReturn(Optional.of(testUser));
    when(passwordHasher.matches("plainPassword", "hashedPassword")).thenReturn(true);

    assertTrue(authenticationService.authenticate(dto));
  }

  @Test
  void authenticate_ShouldReturnFalse_WhenPasswordDoesNotMatch() {
    CredentialsDto dto = new CredentialsDto("firstName.lastName", "wrongPassword");

    when(userRepository.findByUsername("firstName.lastName")).thenReturn(Optional.of(testUser));
    when(passwordHasher.matches("wrongPassword", "hashedPassword")).thenReturn(false);

    assertFalse(authenticationService.authenticate(dto));
  }

  @Test
  void authenticate_ShouldReturnFalse_WhenUserNotFound() {
    CredentialsDto dto = new CredentialsDto("firstName.lastName", "plainPassword");

    when(userRepository.findByUsername("firstName.lastName")).thenReturn(Optional.empty());

    assertFalse(authenticationService.authenticate(dto));
  }

  @Test
  void changePassword_ShouldUpdatePassword_WhenOldPasswordIsCorrect() {
    ChangePasswordDto dto = new ChangePasswordDto();
    dto.setUsername("firstName.lastName");
    dto.setOldPassword("plainPassword");
    dto.setNewPassword("newPlainPassword");

    when(userRepository.findByUsername("firstName.lastName")).thenReturn(Optional.of(testUser));
    when(passwordHasher.matches("plainPassword", "hashedPassword")).thenReturn(true);
    when(passwordHasher.hash("newPlainPassword")).thenReturn("newHashedPassword");

    authenticationService.changePassword(dto);

    verify(passwordHasher).hash("newPlainPassword");
  }

  @Test
  void changePassword_ShouldThrowException_WhenOldPasswordIsWrong() {
    ChangePasswordDto dto = new ChangePasswordDto();
    dto.setUsername("firstName.lastName");
    dto.setOldPassword("wrongPassword");
    dto.setNewPassword("newPlainPassword");

    when(userRepository.findByUsername("firstName.lastName")).thenReturn(Optional.of(testUser));
    when(passwordHasher.matches("wrongPassword", "hashedPassword")).thenReturn(false);

    assertThrows(
        IllegalArgumentException.class, () -> authenticationService.changePassword(dto));
  }

  @Test
  void changePassword_ShouldThrowException_WhenUserNotFound() {
    ChangePasswordDto dto = new ChangePasswordDto();
    dto.setUsername("nonexistent.username");

    when(userRepository.findByUsername("nonexistent.username")).thenReturn(Optional.empty());

    assertThrows(
        EntityNotFoundException.class, () -> authenticationService.changePassword(dto));
  }

  @Test
  void toggleActivation_ShouldToggleIsActive_WhenUserExists() {
    when(userRepository.findByUsername("firstName.lastName")).thenReturn(Optional.of(testUser));

    authenticationService.toggleActivation("firstName.lastName");

    assertFalse(testUser.getIsActive());
  }

  @Test
  void toggleActivation_ShouldThrowException_WhenUserNotFound() {
    when(userRepository.findByUsername("nonexistent.username")).thenReturn(Optional.empty());

    assertThrows(
        EntityNotFoundException.class,
        () -> authenticationService.toggleActivation("nonexistent.username"));
  }
}
