package org.vttish.gymapplication.service.impl;

import java.security.SecureRandom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vttish.gymapplication.persistence.dao.TraineeDao;
import org.vttish.gymapplication.persistence.dao.TrainerDao;
import org.vttish.gymapplication.service.ProfileGenerationService;

@Service
public class DefaultProfileGenerationService implements ProfileGenerationService {

  @Autowired
  private TraineeDao traineeDao;

  @Autowired
  private TrainerDao trainerDao;

  private static final String PASSWORD_CHARS =
      "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
  private final SecureRandom random = new SecureRandom();
  private static final int PASSWORD_LENGTH = 10;

  @Override
  public String generateUniqueUsername(String firstName, String lastName) {
    StringBuilder baseUsername = new StringBuilder(firstName).append(".").append(lastName);
    int baseLength = baseUsername.length();

    String finalUsername = baseUsername.toString();
    int serialNumber = 1;

    while (existsByUsername(finalUsername)) {
      baseUsername.setLength(baseLength);
      finalUsername = baseUsername.append(serialNumber).toString();
      serialNumber++;
    }

    return finalUsername;
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

  @Override
  public boolean existsByUsername(String username) {
    return traineeDao.existsByUsername(username) || trainerDao.existsByUsername(username);
  }
}
