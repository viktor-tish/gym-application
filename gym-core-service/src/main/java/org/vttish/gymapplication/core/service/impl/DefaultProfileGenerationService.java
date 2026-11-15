package org.vttish.gymapplication.core.service.impl;

import java.security.SecureRandom;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vttish.gymapplication.core.logging.Loggable;
import org.vttish.gymapplication.core.repository.UserRepository;
import org.vttish.gymapplication.core.service.ProfileGenerationService;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Loggable
public class DefaultProfileGenerationService implements ProfileGenerationService {
  private final UserRepository userRepository;

  private static final String PASSWORD_CHARS =
      "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
  private final SecureRandom random = new SecureRandom();
  private static final int PASSWORD_LENGTH = 10;

  @Override
  public String generateUniqueUsername(String firstName, String lastName) {
    String baseUsername = firstName + "." + lastName;
    List<String> existingUsernames = userRepository.findUsernamesByPrefix(baseUsername);

    if (!existingUsernames.contains(baseUsername)) {
      return baseUsername;
    }

    int maxSerialNumber = existingUsernames.stream()
        .map(username -> username.substring(baseUsername.length()))
        .filter(suffix -> suffix.matches("\\d+"))
        .mapToInt(Integer::parseInt)
        .max()
        .orElse(0);

    return baseUsername + (maxSerialNumber + 1);
  }

  @Override
  public String generateRandomPassword() {
    StringBuilder password = new StringBuilder(PASSWORD_LENGTH);

    for (int i = 0; i < PASSWORD_LENGTH; i++) {
      int charIdx = random.nextInt(PASSWORD_CHARS.length());
      password.append(PASSWORD_CHARS.charAt(charIdx));
    }

    return password.toString();
  }
}
