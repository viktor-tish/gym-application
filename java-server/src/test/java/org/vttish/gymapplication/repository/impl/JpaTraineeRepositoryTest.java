package org.vttish.gymapplication.repository.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import org.vttish.gymapplication.entity.Trainee;

@ExtendWith(MockitoExtension.class)
class JpaTraineeRepositoryTest {

  @Mock
  private EntityManager entityManager;

  @Mock
  private TypedQuery<Trainee> queryMock;

  @InjectMocks
  private JpaTraineeRepository traineeRepository;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(traineeRepository, "entityManager", entityManager);
    ReflectionTestUtils.setField(traineeRepository, "entityClass", Trainee.class);
  }

  @Test
  void findAll_ShouldUseJoinFetch() {
    List<Trainee> expectedList = List.of(new Trainee());
    when(entityManager.createQuery(anyString(), eq(Trainee.class)))
        .thenReturn(queryMock);
    when(queryMock.getResultList()).thenReturn(expectedList);

    List<Trainee> result = traineeRepository.findAll();

    assertEquals(1, result.size());
    verify(entityManager).createQuery(
        "SELECT t FROM Trainee t JOIN FETCH t.user u",
        Trainee.class
    );
  }

  @Test
  void findByUsername_WhenUsernameIsNull_ShouldReturnEmpty() {
    Optional<Trainee> result = traineeRepository.findByUsername(null);
    assertTrue(result.isEmpty());
    verify(entityManager, never()).createQuery(anyString(), any());
  }

  @Test
  void findByUsername_WhenExists_ShouldReturnEntity() {
    String username = "firstName.lastName";
    Trainee trainee = new Trainee();

    when(entityManager.createQuery(anyString(), eq(Trainee.class)))
        .thenReturn(queryMock);
    when(queryMock.setParameter("username", username)).thenReturn(queryMock);
    when(queryMock.getResultList()).thenReturn(List.of(trainee));

    Optional<Trainee> result = traineeRepository.findByUsername(username);

    assertTrue(result.isPresent());
    assertEquals(trainee, result.get());
    verify(entityManager).createQuery(
        "SELECT t FROM Trainee t "
            + "JOIN FETCH t.user u WHERE u.username = :username",
        Trainee.class
    );
  }

  @Test
  void deleteByUsername_WhenUsernameIsNull_ShouldDoNothing() {
    traineeRepository.deleteByUsername(null);
    verify(entityManager, never()).createQuery(anyString(), any());
    verify(entityManager, never()).remove(any());
  }

  @Test
  void deleteByUsername_WhenExists_ShouldCallRemove() {
    String username = "firstName.lastName";
    Trainee trainee = new Trainee();

    when(entityManager.createQuery(anyString(), eq(Trainee.class)))
        .thenReturn(queryMock);
    when(queryMock.setParameter("username", username)).thenReturn(queryMock);
    when(queryMock.getResultList()).thenReturn(List.of(trainee));

    traineeRepository.deleteByUsername(username);

    verify(entityManager).remove(trainee);
  }
}
