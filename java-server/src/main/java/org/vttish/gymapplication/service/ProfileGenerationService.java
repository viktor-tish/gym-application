package org.vttish.gymapplication.service;

public interface ProfileGenerationService {
  String generateUniqueUsername(String firstName, String lastName);

  String generateRandomPassword();

  boolean existsByUsername(String username);
}
