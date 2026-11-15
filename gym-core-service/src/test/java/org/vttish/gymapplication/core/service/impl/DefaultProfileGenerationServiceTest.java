package org.vttish.gymapplication.core.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.vttish.gymapplication.core.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class DefaultProfileGenerationServiceTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private DefaultProfileGenerationService profileGenerationService;

  @Test
  void generateUniqueUsername_ShouldReturnBaseName_WhenNoMatchesExist() {
    String firstName = "firstName";
    String lastName = "lastName";
    String expected = "firstName.lastName";

    when(userRepository.findUsernamesByPrefix(expected)).thenReturn(List.of());

    String result = profileGenerationService.generateUniqueUsername(firstName, lastName);

    assertEquals(expected, result);
  }

  @Test
  void generateUniqueUsername_ShouldReturnNextSuffix_WhenMatchesExist() {
    String firstName = "firstName";
    String lastName = "lastName";
    String baseName = "firstName.lastName";

    when(userRepository.findUsernamesByPrefix(baseName))
        .thenReturn(List.of("firstName.lastName", "firstName.lastName1", "firstName.lastName2"));

    String result = profileGenerationService.generateUniqueUsername(firstName, lastName);

    assertEquals("firstName.lastName3", result);
  }

  @Test
  void generateUniqueUsername_ShouldReturnBaseName1_WhenOnlyExactMatchExists() {
    String firstName = "firstName";
    String lastName = "lastName";
    String baseName = "firstName.lastName";

    when(userRepository.findUsernamesByPrefix(baseName)).thenReturn(List.of("firstName.lastName"));

    String result = profileGenerationService.generateUniqueUsername(firstName, lastName);

    assertEquals("firstName.lastName1", result);
  }

  @Test
  void generateRandomPassword_ShouldReturn10CharacterString() {
    String password = profileGenerationService.generateRandomPassword();

    assertNotNull(password);
    assertEquals(10, password.length());
    assertTrue(password.matches("^[A-Za-z0-9]+$"));
  }
}
