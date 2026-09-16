package org.vttish.gymcrmsystem.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.vttish.gymcrmsystem.mapper.TraineeMapper;
import org.vttish.gymcrmsystem.mapper.TrainerMapper;
import org.vttish.gymcrmsystem.mapper.TrainingMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class StorageInitializationPostProcessor implements BeanPostProcessor, InitializingBean {
    private ObjectMapper objectMapper;
    private Resource storageFile;
    private StorageWrapper initialData;

    private TraineeMapper traineeMapper;
    private TrainerMapper trainerMapper;
    private TrainingMapper trainingMapper;

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Value("${application.storage-file-path}")
    public void setStorageFilePath(Resource storageFile) {
        this.storageFile = storageFile;
    }

    @Autowired
    public void setTraineeMapper(TraineeMapper traineeMapper) {
        this.traineeMapper = traineeMapper;
    }

    @Autowired
    public void setTrainerMapper(TrainerMapper trainerMapper) {
        this.trainerMapper = trainerMapper;
    }

    @Autowired
    public void setTrainingMapper(TrainingMapper trainingMapper) {
        this.trainingMapper = trainingMapper;
    }

    @Override
    public void afterPropertiesSet() {
        try {
            if (storageFile.exists()) {
                try (InputStream inputStream = storageFile.getInputStream()) {
                    this.initialData = objectMapper.readValue(inputStream, StorageWrapper.class);
                    log.info(
                            "Parsed initial data from: {}. Trainees: {}, Trainers: {}, Trainings: {}",
                            storageFile.getFilename(),
                            initialData.trainees() != null ? initialData.trainees().size() : 0,
                            initialData.trainers() != null ? initialData.trainers().size() : 0,
                            initialData.trainings() != null ? initialData.trainings().size() : 0
                    );
                }
            } else {
                log.warn("Storage file not found at provided path. Starting with empty storage");
            }
        } catch (IOException e) {
            log.error("Failed to parse initial data from storage file", e);
            throw new RuntimeException("Could not initialize the storage", e);
        }
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if (initialData == null) {
            return bean;
        }

        if (beanName.equals("traineeStorage") && initialData.trainees() != null) {
            log.info("Fill traineeStorage bean with initial data");
            return initialData.trainees().entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            entry -> traineeMapper.toTrainee(entry.getValue()))
                    );
        } else if (beanName.equals("trainerStorage") && initialData.trainers() != null) {
            log.info("Fill trainerStorage bean with initial data");
            return initialData.trainers().entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            entry -> trainerMapper.toTrainer(entry.getValue()))
                    );
        } else if (beanName.equals("trainingStorage") && initialData.trainings() != null) {
            log.info("Fill trainingStorage bean with initial data");
            return initialData.trainings().entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            entry -> trainingMapper.toTraining(entry.getValue()))
                    );
        }

        return bean;
    }
}
