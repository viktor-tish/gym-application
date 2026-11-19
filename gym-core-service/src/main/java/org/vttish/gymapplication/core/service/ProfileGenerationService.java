package org.vttish.gymapplication.core.service;

public interface ProfileGenerationService {
  String generateUniqueUsername(String firstName, String lastName);

  String generateRandomPassword();
}
