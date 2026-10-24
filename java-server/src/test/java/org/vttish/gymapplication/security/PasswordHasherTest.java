package org.vttish.gymapplication.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PasswordHasherTest {

  private PasswordHasher passwordHasher;

  @BeforeEach
  void setUp() {
    passwordHasher = new PasswordHasher();
  }

  @Test
  void hash_ShouldReturnHashedString_WhenValidInput() {
    String rawPassword = "password";
    String hashedPassword = passwordHasher.hash(rawPassword);

    assertNotNull(hashedPassword);
    assertNotEquals(rawPassword, hashedPassword);
  }

  @Test
  void hash_ShouldReturnSameHash_ForSameInput() {
    String rawPassword = "password";
    String hash1 = passwordHasher.hash(rawPassword);
    String hash2 = passwordHasher.hash(rawPassword);

    assertEquals(hash1, hash2);
  }

  @Test
  void hash_ShouldReturnNull_WhenInputIsNull() {
    assertNull(passwordHasher.hash(null));
  }

  @Test
  void matches_ShouldReturnTrue_WhenPasswordsMatch() {
    String rawPassword = "password";
    String hashedPassword = passwordHasher.hash(rawPassword);

    assertTrue(passwordHasher.matches(rawPassword, hashedPassword));
  }

  @Test
  void matches_ShouldReturnFalse_WhenPasswordsDoNotMatch() {
    String rawPassword = "password";
    String wrongPassword = "wrongPassword";
    String hashedPassword = passwordHasher.hash(rawPassword);

    assertFalse(passwordHasher.matches(wrongPassword, hashedPassword));
  }

  @Test
  void matches_ShouldReturnFalse_WhenRawPasswordIsNull() {
    String hashedPassword = passwordHasher.hash("password");
    assertFalse(passwordHasher.matches(null, hashedPassword));
  }

  @Test
  void matches_ShouldReturnFalse_WhenHashedPasswordIsNull() {
    assertFalse(passwordHasher.matches("password", null));
  }
}
