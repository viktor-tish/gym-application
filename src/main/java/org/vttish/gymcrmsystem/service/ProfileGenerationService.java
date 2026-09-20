package org.vttish.gymcrmsystem.service;

public interface ProfileGenerationService {
  String generateUniqueUsername(String firstName, String lastName);

  String generateRandomPassword();

  boolean existsByUsername(String username);
}
