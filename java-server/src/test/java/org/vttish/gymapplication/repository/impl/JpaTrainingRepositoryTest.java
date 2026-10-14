package org.vttish.gymapplication.repository.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.vttish.gymapplication.entity.Training;
import org.vttish.gymapplication.repository.filter.TraineeTrainingsFilter;
import org.vttish.gymapplication.repository.filter.TrainerTrainingsFilter;

@ExtendWith(MockitoExtension.class)
class JpaTrainingRepositoryTest {

  @Mock
  private EntityManager entityManager;

  @Mock
  private TypedQuery<Training> queryMock;

  @InjectMocks
  private JpaTrainingRepository trainingRepository;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(trainingRepository, "entityManager", entityManager);
    ReflectionTestUtils.setField(trainingRepository, "entityClass", Training.class);
  }

  @Test
  void findTraineeTrainingsByFilter_ShouldBuildCorrectJpql() {
    UUID typeId = UUID.randomUUID();

    TraineeTrainingsFilter filter = new TraineeTrainingsFilter(
        LocalDate.of(2000, 1, 1),
        LocalDate.of(2000, 12, 31),
        "firstName.lastName1",
        typeId
    );

    when(entityManager.createQuery(anyString(), eq(Training.class))).thenReturn(queryMock);
    when(queryMock.getResultList()).thenReturn(List.of(new Training()));

    String traineeUsername = "firstName.lastName";
    List<Training> result = trainingRepository.findTraineeTrainingsByFilter(
        traineeUsername, filter);

    assertEquals(1, result.size());

    String expectedJpql = "SELECT t FROM Training t "
        + "JOIN FETCH t.trainee tr JOIN FETCH tr.user tru "
        + "JOIN FETCH t.trainer trn JOIN FETCH trn.user trnu "
        + "JOIN FETCH t.type ty "
        + "WHERE tru.username = :traineeUsername "
        + "AND t.date >= :fromDate "
        + "AND t.date <= :toDate "
        + "AND (trnu.firstName = :trainerName OR trnu.lastName = :trainerName "
        + "OR trnu.username = :trainerName) "
        + "AND ty.id = :trainingTypeId";

    verify(entityManager).createQuery(expectedJpql, Training.class);
    verify(queryMock).setParameter("traineeUsername", traineeUsername);
    verify(queryMock).setParameter("fromDate", filter.fromDate());
    verify(queryMock).setParameter("toDate", filter.toDate());
    verify(queryMock).setParameter("trainerName", filter.trainerName());
    verify(queryMock).setParameter("trainingTypeId", typeId);
  }

  @Test
  void findTraineeTrainingsByFilter_WithNullFilter_ShouldBuildCorrectJpql() {
    String traineeUsername = "firstName.lastName";

    when(entityManager.createQuery(anyString(), eq(Training.class))).thenReturn(queryMock);
    when(queryMock.getResultList()).thenReturn(List.of(new Training()));

    List<Training> result = trainingRepository.findTraineeTrainingsByFilter(
        traineeUsername, null);

    assertEquals(1, result.size());

    String expectedJpql = "SELECT t FROM Training t "
        + "JOIN FETCH t.trainee tr JOIN FETCH tr.user tru "
        + "JOIN FETCH t.trainer trn JOIN FETCH trn.user trnu "
        + "JOIN FETCH t.type ty "
        + "WHERE tru.username = :traineeUsername";

    verify(entityManager).createQuery(expectedJpql, Training.class);
    verify(queryMock).setParameter("traineeUsername", traineeUsername);
  }

  @Test
  void findTrainerTrainingsByFilter_ShouldBuildCorrectJpql() {
    TrainerTrainingsFilter filter = new TrainerTrainingsFilter(
        LocalDate.of(2000, 1, 1),
        LocalDate.of(2000, 12, 31),
        "firstName.lastName"
    );

    when(entityManager.createQuery(anyString(), eq(Training.class))).thenReturn(queryMock);
    when(queryMock.getResultList()).thenReturn(List.of(new Training()));

    String trainerUsername = "firstName.lastName1";
    List<Training> result = trainingRepository.findTrainerTrainingsByFilter(
        trainerUsername, filter);

    assertEquals(1, result.size());

    String expectedJpql = "SELECT t FROM Training t "
        + "JOIN FETCH t.trainee tr JOIN FETCH tr.user tru "
        + "JOIN FETCH t.trainer trn JOIN FETCH trn.user trnu "
        + "JOIN FETCH t.type ty "
        + "WHERE trnu.username = :trainerUsername "
        + "AND t.date >= :fromDate "
        + "AND t.date <= :toDate "
        + "AND (tru.firstName = :traineeName OR tru.lastName = :traineeName "
        + "OR tru.username = :traineeName)";

    verify(entityManager).createQuery(expectedJpql, Training.class);
    verify(queryMock).setParameter("trainerUsername", trainerUsername);
    verify(queryMock).setParameter("fromDate", filter.fromDate());
    verify(queryMock).setParameter("toDate", filter.toDate());
    verify(queryMock).setParameter("traineeName", filter.traineeName());
  }

  @Test
  void findTrainerTrainingsByFilter_WithNullFilter_ShouldBuildCorrectJpql() {
    String trainerUsername = "firstName.lastName1";

    when(entityManager.createQuery(anyString(), eq(Training.class))).thenReturn(queryMock);
    when(queryMock.getResultList()).thenReturn(List.of(new Training()));

    List<Training> result = trainingRepository.findTrainerTrainingsByFilter(
        trainerUsername, null);

    assertEquals(1, result.size());

    String expectedJpql = "SELECT t FROM Training t "
        + "JOIN FETCH t.trainee tr JOIN FETCH tr.user tru "
        + "JOIN FETCH t.trainer trn JOIN FETCH trn.user trnu "
        + "JOIN FETCH t.type ty "
        + "WHERE trnu.username = :trainerUsername";

    verify(entityManager).createQuery(expectedJpql, Training.class);
    verify(queryMock).setParameter("trainerUsername", trainerUsername);
  }
}
