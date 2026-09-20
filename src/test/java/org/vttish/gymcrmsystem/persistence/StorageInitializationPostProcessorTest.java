package org.vttish.gymcrmsystem.persistence;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.vttish.gymcrmsystem.dto.TraineeDto;
import org.vttish.gymcrmsystem.dto.TrainerDto;
import org.vttish.gymcrmsystem.dto.TrainingDto;
import org.vttish.gymcrmsystem.entity.Trainee;
import org.vttish.gymcrmsystem.entity.Trainer;
import org.vttish.gymcrmsystem.entity.Training;
import org.vttish.gymcrmsystem.mapper.TraineeMapper;
import org.vttish.gymcrmsystem.mapper.TrainerMapper;
import org.vttish.gymcrmsystem.mapper.TrainingMapper;

@ExtendWith(MockitoExtension.class)
class StorageInitializationPostProcessorTest {

  @Mock
  private ObjectMapper objectMapper;

  @Mock
  private Resource storageFile;

  @Mock
  private StorageWrapper storageWrapper;

  @Mock
  private TraineeMapper traineeMapper;

  @Mock
  private TrainerMapper trainerMapper;

  @Mock
  private TrainingMapper trainingMapper;

  private StorageInitializationPostProcessor postProcessor;

  @BeforeEach
  public void setUp() {
    postProcessor = new StorageInitializationPostProcessor();
    postProcessor.setObjectMapper(objectMapper);
    postProcessor.setStorageFilePath(storageFile);
    postProcessor.setTraineeMapper(traineeMapper);
    postProcessor.setTrainerMapper(trainerMapper);
    postProcessor.setTrainingMapper(trainingMapper);
  }

  private void setUpSuccessfulStorageFileLoad() throws IOException {
    when(storageFile.exists()).thenReturn(true);
    when(storageFile.getInputStream()).thenReturn(new ByteArrayInputStream("{}".getBytes()));
    when(objectMapper.readValue(any(InputStream.class), eq(StorageWrapper.class)))
        .thenReturn(storageWrapper);

    postProcessor.afterPropertiesSet();
  }

  @Test
  public void afterPropertiesSet_shouldLoadData_whenFileExists() throws IOException {
    InputStream dummyInputStream = new ByteArrayInputStream("{}".getBytes());
    when(storageFile.exists()).thenReturn(true);
    when(storageFile.getInputStream()).thenReturn(dummyInputStream);
    when(objectMapper.readValue(any(InputStream.class), eq(StorageWrapper.class)))
        .thenReturn(storageWrapper);

    assertDoesNotThrow(() -> postProcessor.afterPropertiesSet());

    verify(objectMapper, times(1))
        .readValue(any(InputStream.class), eq(StorageWrapper.class));
  }

  @Test
  public void afterPropertiesSet_shouldDoNothing_whenFileDoesNotExist() throws IOException {
    when(storageFile.exists()).thenReturn(false);

    postProcessor.afterPropertiesSet();

    verify(storageFile, never()).getInputStream();
    verify(objectMapper, never()).readValue(any(InputStream.class), eq(StorageWrapper.class));
  }

  @Test
  public void afterPropertiesSet_shouldThrowRuntimeException_onIOException() throws IOException {
    when(storageFile.exists()).thenReturn(true);
    when(storageFile.getInputStream()).thenThrow(new IOException());

    assertThrows(RuntimeException.class, () -> postProcessor.afterPropertiesSet());
  }

  @Test
  public void postProcessAfterInitialization_shouldReplaceTraineeStorage() throws IOException {
    Object emptyBean = new Object();
    UUID id = UUID.randomUUID();
    TraineeDto dto = mock(TraineeDto.class);
    Trainee entity = mock(Trainee.class);

    when(storageWrapper.trainees()).thenReturn(Map.of(id, dto));
    when(traineeMapper.toTrainee(dto)).thenReturn(entity);
    setUpSuccessfulStorageFileLoad();

    Object result = postProcessor.postProcessAfterInitialization(emptyBean, "traineeStorage");

    assertInstanceOf(Map.class, result);
    Map<?, ?> resultMap = (Map<?, ?>) result;
    assertEquals(1, resultMap.size());
    assertEquals(entity, resultMap.get(id));
  }

  @Test
  public void postProcessAfterInitialization_shouldReplaceTrainerStorage() throws IOException {
    Object emptyBean = new Object();
    UUID id = UUID.randomUUID();
    TrainerDto dto = mock(TrainerDto.class);
    Trainer entity = mock(Trainer.class);

    when(storageWrapper.trainers()).thenReturn(Map.of(id, dto));
    when(trainerMapper.toTrainer(dto)).thenReturn(entity);
    setUpSuccessfulStorageFileLoad();

    Object result = postProcessor.postProcessAfterInitialization(emptyBean, "trainerStorage");

    assertInstanceOf(Map.class, result);
    Map<?, ?> resultMap = (Map<?, ?>) result;
    assertEquals(1, resultMap.size());
    assertEquals(entity, resultMap.get(id));
  }

  @Test
  public void postProcessAfterInitialization_shouldReplaceTrainingStorage() throws IOException {
    Object emptyBean = new Object();
    UUID id = UUID.randomUUID();
    TrainingDto dto = mock(TrainingDto.class);
    Training entity = mock(Training.class);

    when(storageWrapper.trainings()).thenReturn(Map.of(id, dto));
    when(trainingMapper.toTraining(dto)).thenReturn(entity);
    setUpSuccessfulStorageFileLoad();

    Object result = postProcessor.postProcessAfterInitialization(emptyBean, "trainingStorage");

    assertInstanceOf(Map.class, result);
    Map<?, ?> resultMap = (Map<?, ?>) result;
    assertEquals(1, resultMap.size());
    assertEquals(entity, resultMap.get(id));
  }

  @Test
  public void postProcessAfterInitialization_shouldReturnOriginalBean_whenBeanNameDoesNotMatch() throws IOException {
    Object originalBean = new Object();
    setUpSuccessfulStorageFileLoad();

    Object result = postProcessor.postProcessAfterInitialization(originalBean, "otherBean");

    assertSame(originalBean, result);
  }

  @Test
  public void postProcessAfterInitialization_shouldReturnOriginalBean_whenInitialDataIsNull() {
    Object originalBean = new Object();

    Object result = postProcessor.postProcessAfterInitialization(originalBean, "traineeStorage");

    assertSame(originalBean, result);
  }
}
