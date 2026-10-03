package org.vttish.gymapplication.persistence.dao.impl;

import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.vttish.gymapplication.entity.Training;
import org.vttish.gymapplication.persistence.dao.TrainingDao;

@Component
public class InMemoryTrainingDao extends InMemoryBaseDao<Training, UUID> implements TrainingDao {

  @Autowired
  public void setTrainingStorage(Map<UUID, Training> trainingStorage) {
    setStorage(trainingStorage);
  }

  @Override
  public UUID getEntityId(Training entity) {
    return entity.getId();
  }
}
