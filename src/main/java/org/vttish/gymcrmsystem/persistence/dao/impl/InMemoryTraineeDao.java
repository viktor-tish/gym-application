package org.vttish.gymcrmsystem.persistence.dao.impl;

import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.vttish.gymcrmsystem.entity.Trainee;
import org.vttish.gymcrmsystem.persistence.dao.TraineeDao;

@Component
public class InMemoryTraineeDao extends InMemoryUserDao<Trainee> implements TraineeDao {

  @Autowired
  public void setTraineeStorage(Map<UUID, Trainee> traineeStorage) {
    setStorage(traineeStorage);
  }

  @Override
  public UUID getEntityId(Trainee entity) {
    return entity.getUserId();
  }
}
