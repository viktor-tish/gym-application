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
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.vttish.gymapplication.entity.TrainerJpaEntity;

@ExtendWith(MockitoExtension.class)
class JpaTrainerRepositoryTest {

  @Mock
  private EntityManager entityManager;

  @Mock
  private TypedQuery<TrainerJpaEntity> queryMock;

  @InjectMocks
  private JpaTrainerRepository trainerRepository;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(
        trainerRepository, "entityManager", entityManager);
    ReflectionTestUtils.setField(
        trainerRepository, "entityClass", TrainerJpaEntity.class);
  }

  @Test
  void findAll_ShouldUseJoinFetch() {
    List<TrainerJpaEntity> expectedList = List.of(new TrainerJpaEntity());
    when(entityManager.createQuery(anyString(), eq(TrainerJpaEntity.class)))
        .thenReturn(queryMock);
    when(queryMock.getResultList()).thenReturn(expectedList);

    List<TrainerJpaEntity> result = trainerRepository.findAll();

    assertEquals(1, result.size());
    verify(entityManager).createQuery(
        "SELECT tr FROM TrainerJpaEntity tr "
        + "JOIN FETCH tr.user u JOIN FETCH tr.specialization s",
        TrainerJpaEntity.class
    );
  }

  @Test
  void findByUsername_WhenUsernameIsNull_ShouldReturnEmpty() {
    Optional<TrainerJpaEntity> result = trainerRepository.findByUsername(null);
    assertTrue(result.isEmpty());
    verify(entityManager, never()).createQuery(anyString(), any());
  }

  @Test
  void findByUsername_WhenExists_ShouldReturnEntity() {
    String username = "firstName.lastName";
    TrainerJpaEntity trainer = new TrainerJpaEntity();
    
    when(entityManager.createQuery(anyString(), eq(TrainerJpaEntity.class)))
        .thenReturn(queryMock);
    when(queryMock.setParameter("username", username)).thenReturn(queryMock);
    when(queryMock.getResultList()).thenReturn(List.of(trainer));

    Optional<TrainerJpaEntity> result = trainerRepository.findByUsername(username);

    assertTrue(result.isPresent());
    assertEquals(trainer, result.get());
  }

  @Test
  void findTrainersNotAssignedToTrainee_WhenUsernameIsNull_ShouldReturnEmpty() {
    List<TrainerJpaEntity> result = 
        trainerRepository.findTrainersNotAssignedToTrainee(null);
    assertTrue(result.isEmpty());
    verify(entityManager, never()).createQuery(anyString(), any());
  }

  @Test
  void findTrainersNotAssignedToTrainee_ShouldExecuteSubquery() {
    String traineeUsername = "firstName.lastName";
    List<TrainerJpaEntity> expectedList = List.of(new TrainerJpaEntity());
    
    when(entityManager.createQuery(anyString(), eq(TrainerJpaEntity.class)))
        .thenReturn(queryMock);
    when(queryMock.setParameter("traineeUsername", traineeUsername))
        .thenReturn(queryMock);
    when(queryMock.getResultList()).thenReturn(expectedList);

    List<TrainerJpaEntity> result = 
        trainerRepository.findTrainersNotAssignedToTrainee(traineeUsername);

    assertEquals(1, result.size());
    verify(entityManager).createQuery(
        ArgumentMatchers.contains(
            "SELECT assignedTr FROM TraineeJpaEntity te"), 
        eq(TrainerJpaEntity.class)
    );
  }
}
