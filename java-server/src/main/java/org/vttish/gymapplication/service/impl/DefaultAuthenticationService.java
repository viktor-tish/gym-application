package org.vttish.gymapplication.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import org.vttish.gymapplication.security.PasswordHasher;
import org.vttish.gymapplication.security.Role;
import org.vttish.gymapplication.security.UserPrincipal;
import org.vttish.gymapplication.service.AuthenticationService;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DefaultAuthenticationService implements AuthenticationService {
  private final UserRepository userRepository;
  private final TraineeRepository traineeRepository;
  private final TrainerRepository trainerRepository;
  private final PasswordHasher passwordHasher;
  private final JwtService jwtService;
  private final GymMetrics gymMetrics;

  @Override
  public LoginResponseDto login(LoginRequestDto request) {
    try {
      User user = userRepository.findByUsername(request.username())
          .filter(foundUser -> passwordHasher.matches(request.password(), foundUser.getPassword()))
          .orElseThrow(() -> new AuthenticationException("Invalid username or password"));

      Role role = resolveRole(user.getUsername());
      UserPrincipal userPrincipal = new UserPrincipal(user.getUsername(), role);

      gymMetrics.incrementLoginSuccess();
      return new LoginResponseDto(jwtService.generateToken(userPrincipal));
    } catch (AuthenticationException ex) {
      gymMetrics.incrementLoginFailure();
      throw ex;
    }
  }

  @Override
  @Transactional
  public void changePassword(ChangePasswordRequestDto request) {
    User user = userRepository.findByUsername(request.username()).orElseThrow(
        () -> new ResourceNotFoundException("User not found with username: " + request.username())
    );

    if (!passwordHasher.matches(request.oldPassword(), user.getPassword())) {
      throw new AuthenticationException("Invalid old password");
    }

    user.setPassword(passwordHasher.hash(request.newPassword()));
  }

  @Override
  @Transactional
  public void updateStatus(String username, UpdateUserStatusRequestDto request) {
    User user = userRepository.findByUsername(username).orElseThrow(
        () -> new ResourceNotFoundException("User not found with username: " + username)
    );

    if (request.isActive().equals(user.getIsActive())) {
      throw new BadRequestException(
          "User is already " + (request.isActive() ? "active" : "inactive")
      );
    }

    user.setIsActive(request.isActive());
  }

  private Role resolveRole(String username) {
    if (traineeRepository.existsByUsername(username)) {
      return Role.TRAINEE;
    }

    if (trainerRepository.existsByUsername(username)) {
      return Role.TRAINER;
    }

    throw new AuthenticationException("User has no assigned role");
  }
}
