package org.vttish.gymapplication.repository.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.vttish.gymapplication.entity.User;

@ExtendWith(MockitoExtension.class)
class JpaUserRepositoryTest {

  @Mock
  private EntityManager entityManager;

  @Mock
  private TypedQuery<Long> countQueryMock;

  @Mock
  private TypedQuery<User> userQueryMock;

  @Mock
  private TypedQuery<String> stringQueryMock;

  @InjectMocks
  private JpaUserRepository userRepository;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(userRepository, "entityManager", entityManager);
    ReflectionTestUtils.setField(userRepository, "entityClass", User.class);
  }

  @Test
  void existsByUsername_WhenUsernameIsNull_ShouldReturnFalse() {
    boolean exists = userRepository.existsByUsername(null);
    assertFalse(exists);
    verify(entityManager, never()).createQuery(anyString(), any());
  }

  @Test
  void existsByUsername_WhenUserExists_ShouldReturnTrue() {
    String username = "firstName.lastName";
    when(entityManager.createQuery(anyString(), eq(Long.class)))
        .thenReturn(countQueryMock);
    when(countQueryMock.setParameter("username", username))
        .thenReturn(countQueryMock);
    when(countQueryMock.getSingleResult()).thenReturn(1L);

    boolean exists = userRepository.existsByUsername(username);

    assertTrue(exists);
    verify(entityManager).createQuery(
        "SELECT COUNT(u) FROM User u WHERE u.username = :username",
        Long.class
    );
  }

  @Test
  void findByUsername_WhenUsernameIsNull_ShouldReturnEmpty() {
    Optional<User> result = userRepository.findByUsername(null);
    assertTrue(result.isEmpty());
    verify(entityManager, never()).createQuery(anyString(), any());
  }

  @Test
  void findByUsername_WhenUserExists_ShouldReturnUser() {
    String username = "firstName.lastName";
    User user = new User();
    when(entityManager.createQuery(anyString(), eq(User.class)))
        .thenReturn(userQueryMock);
    when(userQueryMock.setParameter("username", username))
        .thenReturn(userQueryMock);
    when(userQueryMock.getResultList()).thenReturn(List.of(user));

    Optional<User> result = userRepository.findByUsername(username);

    assertTrue(result.isPresent());
    assertEquals(user, result.get());
  }

  @Test
  void findByUsername_WhenUserDoesNotExist_ShouldReturnEmpty() {
    String username = "nonexistent.username";

    when(entityManager.createQuery(anyString(), eq(User.class)))
        .thenReturn(userQueryMock);
    when(userQueryMock.setParameter("username", username))
        .thenReturn(userQueryMock);
    when(userQueryMock.getResultList()).thenReturn(List.of());

    Optional<User> result = userRepository.findByUsername(username);

    assertTrue(result.isEmpty());
  }

  @Test
  void findUsernamesByPrefix_WhenPrefixIsNull_ShouldReturnEmptyList() {
    List<String> result = userRepository.findUsernamesByPrefix(null);
    assertTrue(result.isEmpty());
    verify(entityManager, never()).createQuery(anyString(), any());
  }

  @Test
  void findUsernamesByPrefix_ShouldReturnList() {
    String prefix = "firstName.lastName";

    List<String> expectedList = List.of(
        "firstName.lastName",
        "firstName.lastName1"
    );

    when(entityManager.createQuery(anyString(), eq(String.class)))
        .thenReturn(stringQueryMock);
    when(stringQueryMock.setParameter("prefix", prefix + "%"))
        .thenReturn(stringQueryMock);
    when(stringQueryMock.getResultList()).thenReturn(expectedList);

    List<String> result = userRepository.findUsernamesByPrefix(prefix);

    assertEquals(2, result.size());
    verify(entityManager).createQuery(
        "SELECT u.username FROM User u "
            + "WHERE u.username LIKE :prefix",
        String.class
    );
  }
}
