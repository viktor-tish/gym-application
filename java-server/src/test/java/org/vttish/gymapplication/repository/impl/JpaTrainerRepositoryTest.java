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
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.vttish.gymapplication.entity.Trainer;

@ExtendWith(MockitoExtension.class)
class JpaTrainerRepositoryTest {

  @Mock
  private EntityManager entityManager;

  @Mock
  private TypedQuery<Long> countQueryMock;

  @Mock
  private TypedQuery<Trainer> queryMock;

  @InjectMocks
  private JpaTrainerRepository trainerRepository;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(trainerRepository, "entityManager", entityManager);
    ReflectionTestUtils.setField(trainerRepository, "entityClass", Trainer.class);
  }

  @Test
  void findAll_ShouldUseJoinFetch() {
    List<Trainer> expectedList = List.of(new Trainer());
    when(entityManager.createQuery(anyString(), eq(Trainer.class)))
        .thenReturn(queryMock);
    when(queryMock.getResultList()).thenReturn(expectedList);

    List<Trainer> result = trainerRepository.findAll();

    assertEquals(1, result.size());
    verify(entityManager).createQuery(
        "SELECT tr FROM Trainer tr "
            + "JOIN FETCH tr.user u JOIN FETCH tr.specialization s",
        Trainer.class
    );
  }

  @Test
  void findByUsername_WhenUsernameIsNull_ShouldReturnEmpty() {
    Optional<Trainer> result = trainerRepository.findByUsername(null);
    assertTrue(result.isEmpty());
    verify(entityManager, never()).createQuery(anyString(), any());
  }

  @Test
  void findByUsername_WhenExists_ShouldReturnEntity() {
    String username = "firstName.lastName";
    Trainer trainer = new Trainer();

    when(entityManager.createQuery(anyString(), eq(Trainer.class)))
        .thenReturn(queryMock);
    when(queryMock.setParameter("username", username)).thenReturn(queryMock);
    when(queryMock.getResultList()).thenReturn(List.of(trainer));

    Optional<Trainer> result = trainerRepository.findByUsername(username);

    assertTrue(result.isPresent());
    assertEquals(trainer, result.get());
  }

  @Test
  void findTrainersNotAssignedToTrainee_WhenUsernameIsNull_ShouldReturnEmpty() {
    List<Trainer> result =
        trainerRepository.findTrainersNotAssignedToTrainee(null);
    assertTrue(result.isEmpty());
    verify(entityManager, never()).createQuery(anyString(), any());
  }

  @Test
  void findTrainersNotAssignedToTrainee_ShouldExecuteSubquery() {
    String traineeUsername = "firstName.lastName";
    List<Trainer> expectedList = List.of(new Trainer());

    when(entityManager.createQuery(anyString(), eq(Trainer.class)))
        .thenReturn(queryMock);
    when(queryMock.setParameter("traineeUsername", traineeUsername))
        .thenReturn(queryMock);
    when(queryMock.getResultList()).thenReturn(expectedList);

    List<Trainer> result =
        trainerRepository.findTrainersNotAssignedToTrainee(traineeUsername);

    assertEquals(1, result.size());
    verify(entityManager).createQuery(
        ArgumentMatchers.contains(
            "SELECT assignedTr FROM Trainee te"),
        eq(Trainer.class)
    );
  }

  @Test
  void findAllByUsernameIn_WhenNullOrEmpty_ShouldReturnEmptyList() {
    List<Trainer> nullResult = trainerRepository.findAllByUsernameIn(null);
    assertTrue(nullResult.isEmpty());

    List<Trainer> emptyResult = trainerRepository.findAllByUsernameIn(List.of());
    assertTrue(emptyResult.isEmpty());

    verify(entityManager, never()).createQuery(anyString(), any());
  }

  @Test
  void findAllByUsernameIn_WhenValid_ShouldReturnTrainers() {
    List<String> usernames = List.of("trainer1", "trainer2");
    List<Trainer> expectedList = List.of(new Trainer(), new Trainer());

    when(entityManager.createQuery(anyString(), eq(Trainer.class)))
        .thenReturn(queryMock);
    when(queryMock.setParameter("usernames", usernames)).thenReturn(queryMock);
    when(queryMock.getResultList()).thenReturn(expectedList);

    List<Trainer> result = trainerRepository.findAllByUsernameIn(usernames);

    assertEquals(2, result.size());
    verify(entityManager).createQuery(
        "SELECT tr FROM Trainer tr "
            + "JOIN FETCH tr.user u "
            + "JOIN FETCH tr.specialization s "
            + "WHERE u.username IN (:usernames)",
        Trainer.class
    );
  }

  @Test
  void existsByUsername_WhenUsernameIsNull_ShouldReturnFalse() {
    boolean exists = trainerRepository.existsByUsername(null);
    assertFalse(exists);
    verify(entityManager, never()).createQuery(anyString(), any());
  }

  @Test
  void existsByUsername_WhenTrainerExists_ShouldReturnTrue() {
    String username = "firstName.lastName";
    when(entityManager.createQuery(anyString(), eq(Long.class)))
        .thenReturn(countQueryMock);
    when(countQueryMock.setParameter("username", username))
        .thenReturn(countQueryMock);
    when(countQueryMock.getSingleResult()).thenReturn(1L);

    boolean exists = trainerRepository.existsByUsername(username);

    assertTrue(exists);
    verify(entityManager).createQuery(
        "SELECT COUNT(tr) FROM Trainer tr JOIN tr.user u WHERE u.username = :username",
        Long.class
    );
  }

  @Test
  void existsByUsername_WhenTrainerDoesNotExist_ShouldReturnFalse() {
    String username = "nonexistent.username";
    when(entityManager.createQuery(anyString(), eq(Long.class)))
        .thenReturn(countQueryMock);
    when(countQueryMock.setParameter("username", username))
        .thenReturn(countQueryMock);
    when(countQueryMock.getSingleResult()).thenReturn(0L);

    boolean exists = trainerRepository.existsByUsername(username);

    assertFalse(exists);
  }
}
