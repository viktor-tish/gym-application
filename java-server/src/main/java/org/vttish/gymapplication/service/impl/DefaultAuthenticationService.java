package org.vttish.gymapplication.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.vttish.gymapplication.dto.ChangePasswordDto;
import org.vttish.gymapplication.dto.CredentialsDto;
import org.vttish.gymapplication.entity.User;
import org.vttish.gymapplication.repository.UserRepository;
import org.vttish.gymapplication.service.AuthenticationService;
import org.vttish.gymapplication.utils.PasswordHasher;

@Service
@Validated
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DefaultAuthenticationService implements AuthenticationService {
  private final UserRepository userRepository;
  private final PasswordHasher passwordHasher;

  @Override
  public boolean authenticate(CredentialsDto credentialsDto) {
    return userRepository.findByUsername(credentialsDto.getUsername())
        .map(user ->
            passwordHasher.matches(credentialsDto.getPassword(), user.getPassword())
        ).orElse(false);
  }

  @Override
  @Transactional
  public void changePassword(ChangePasswordDto changePasswordDto) {
    User user = userRepository.findByUsername(changePasswordDto.getUsername())
        .orElseThrow(() -> new EntityNotFoundException("User not found"));

    if (!passwordHasher.matches(changePasswordDto.getOldPassword(), user.getPassword())) {
      throw new IllegalArgumentException("Invalid old password");
    }

    user.setPassword(passwordHasher.hash(changePasswordDto.getNewPassword()));
  }

  @Override
  @Transactional
  public void toggleActivation(String username) {
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new EntityNotFoundException("User not found"));
    
    user.setIsActive(!user.getIsActive());
  }
}
