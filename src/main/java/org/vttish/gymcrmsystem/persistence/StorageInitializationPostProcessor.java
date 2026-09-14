package org.vttish.gymcrmsystem.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Component
public class StorageInitializationPostProcessor implements BeanPostProcessor, InitializingBean {
    private ObjectMapper objectMapper;
    private Resource storageFile;
    private StorageWrapper initialData;

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Value("${application.storage-file-path}")
    public void setStorageFilePath(Resource storageFile) {
        this.storageFile = storageFile;
    }

    @Override
    public void afterPropertiesSet() {
        try {
            if (storageFile.exists()) {
                try (InputStream inputStream = storageFile.getInputStream()) {
                    this.initialData = objectMapper.readValue(inputStream, StorageWrapper.class);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize the storage", e);
        }
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if (initialData == null) {
            return bean;
        }

        if (beanName.equals("traineeStorage") && initialData.trainees() != null) {
            return initialData.trainees();
        } else if (beanName.equals("trainerStorage") && initialData.trainers() != null) {
            return initialData.trainers();
        } else if (beanName.equals("trainingStorage") && initialData.trainings() != null) {
            return initialData.trainings();
        }

        return bean;
    }
}
