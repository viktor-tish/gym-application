package org.vttish.gymapplication.core.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vttish.gymapplication.core.actuator.metrics.GymMetrics;
import org.vttish.gymapplication.core.dto.auth.ChangePasswordRequestDto;
import org.vttish.gymapplication.core.dto.auth.LoginRequestDto;
import org.vttish.gymapplication.core.dto.auth.LoginResponseDto;
import org.vttish.gymapplication.core.dto.auth.UpdateUserStatusRequestDto;
import org.vttish.gymapplication.core.entity.User;
import org.vttish.gymapplication.core.exception.BadRequestException;
import org.vttish.gymapplication.core.exception.ResourceNotFoundException;
import org.vttish.gymapplication.core.exception.UnauthenticatedException;
import org.vttish.gymapplication.core.logging.Loggable;
import org.vttish.gymapplication.core.repository.TraineeRepository;
import org.vttish.gymapplication.core.repository.TrainerRepository;
import org.vttish.gymapplication.core.repository.UserRepository;
import org.vttish.gymapplication.core.security.JwtService;
import org.vttish.gymapplication.core.security.LoginAttemptService;
import org.vttish.gymapplication.core.security.model.Role;
import org.vttish.gymapplication.core.security.model.UserPrincipal;
import org.vttish.gymapplication.core.service.AuthenticationService;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Loggable
public class DefaultAuthenticationService implements AuthenticationService {
  private final UserRepository userRepository;
  private final TraineeRepository traineeRepository;
  private final TrainerRepository trainerRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final LoginAttemptService loginAttemptService;
  private final GymMetrics gymMetrics;

  @Override
  public LoginResponseDto login(LoginRequestDto request, String ip) {
    try {
      User user = userRepository.findByUsername(request.username())
          .filter(foundUser -> passwordEncoder.matches(request.password(), foundUser.getPassword()))
          .orElseThrow(() -> new UnauthenticatedException("Invalid username or password"));

      Role role = resolveRole(user.getUsername());
      UserPrincipal userPrincipal = new UserPrincipal(user.getUsername(), role);

      gymMetrics.incrementLoginSuccess();
      loginAttemptService.loginSucceeded(ip);

      return new LoginResponseDto(jwtService.generate(userPrincipal));
    } catch (UnauthenticatedException ex) {
      loginAttemptService.loginFailed(ip);
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

    if (!passwordEncoder.matches(request.oldPassword(), user.getPassword())) {
      throw new UnauthenticatedException("Invalid old password");
    }

    user.setPassword(passwordEncoder.encode(request.newPassword()));
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

    throw new UnauthenticatedException("User has no assigned role");
  }
}
