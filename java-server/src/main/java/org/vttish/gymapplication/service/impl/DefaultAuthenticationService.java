package org.vttish.gymapplication.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vttish.gymapplication.dto.auth.ChangePasswordRequestDto;
import org.vttish.gymapplication.dto.auth.LoginRequestDto;
import org.vttish.gymapplication.dto.auth.UpdateUserStatusRequestDto;
import org.vttish.gymapplication.entity.User;
import org.vttish.gymapplication.exception.AuthenticationException;
import org.vttish.gymapplication.exception.BadRequestException;
import org.vttish.gymapplication.exception.ResourceNotFoundException;
import org.vttish.gymapplication.repository.UserRepository;
import org.vttish.gymapplication.service.AuthenticationService;
import org.vttish.gymapplication.utils.PasswordHasher;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DefaultAuthenticationService implements AuthenticationService {

  private final UserRepository userRepository;
  private final PasswordHasher passwordHasher;

  @Override
  public void login(LoginRequestDto request) {
    boolean matches = userRepository.findByUsername(request.username())
        .map(user -> passwordHasher.matches(request.password(), user.getPassword()))
        .orElse(false);

    if (!matches) {
      throw new AuthenticationException("Invalid username or password");
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
}
