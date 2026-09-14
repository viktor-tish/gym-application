package org.vttish.gymcrmsystem.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.vttish.gymcrmsystem.entity.Trainee;
import org.vttish.gymcrmsystem.entity.Trainer;
import org.vttish.gymcrmsystem.entity.Training;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StorageInitializationPostProcessorTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private Resource storageFile;

    @Mock
    private StorageWrapper storageWrapper;

    private StorageInitializationPostProcessor postProcessor;

    @BeforeEach
    public void setUp() {
        postProcessor = new StorageInitializationPostProcessor();
        postProcessor.setObjectMapper(objectMapper);
        postProcessor.setStorageFilePath(storageFile);
    }

    private void setUpSuccessfulStorageFileLoad() throws IOException {
        when(storageFile.exists()).thenReturn(true);
        when(storageFile.getInputStream()).thenReturn(new ByteArrayInputStream("{}".getBytes()));
        when(objectMapper.readValue(any(InputStream.class), eq(StorageWrapper.class))).thenReturn(storageWrapper);

        postProcessor.afterPropertiesSet();
    }

    @Test
    public void afterPropertiesSet_shouldLoadData_whenFileExists() throws IOException {
        InputStream dummyInputStream = new ByteArrayInputStream("{}".getBytes());
        when(storageFile.exists()).thenReturn(true);
        when(storageFile.getInputStream()).thenReturn(dummyInputStream);
        when(objectMapper.readValue(any(InputStream.class), eq(StorageWrapper.class))).thenReturn(storageWrapper);

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
        Map<UUID, Trainee> traineeMap = Map.of();
        when(storageWrapper.trainees()).thenReturn(traineeMap);
        setUpSuccessfulStorageFileLoad();

        Object result = postProcessor.postProcessAfterInitialization(emptyBean, "traineeStorage");

        assertSame(traineeMap, result);
    }

    @Test
    public void postProcessAfterInitialization_shouldReplaceTrainerStorage() throws IOException {
        Object emptyBean = new Object();
        Map<UUID, Trainer> trainerMap = Map.of();
        when(storageWrapper.trainers()).thenReturn(trainerMap);
        setUpSuccessfulStorageFileLoad();

        Object result = postProcessor.postProcessAfterInitialization(emptyBean, "trainerStorage");

        assertSame(trainerMap, result);
    }

    @Test
    public void postProcessAfterInitialization_shouldReplaceTrainingStorage() throws IOException {
        Object emptyBean = new Object();
        Map<UUID, Training> trainingMap = Map.of();
        when(storageWrapper.trainings()).thenReturn(trainingMap);
        setUpSuccessfulStorageFileLoad();

        Object result = postProcessor.postProcessAfterInitialization(emptyBean, "trainingStorage");

        assertSame(trainingMap, result);
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
