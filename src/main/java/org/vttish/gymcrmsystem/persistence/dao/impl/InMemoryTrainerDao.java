package org.vttish.gymcrmsystem.persistence.dao.impl;

import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.vttish.gymcrmsystem.entity.Trainer;
import org.vttish.gymcrmsystem.persistence.dao.TrainerDao;

@Component
public class InMemoryTrainerDao extends InMemoryUserDao<Trainer> implements TrainerDao {

  @Autowired
  public void setTrainerStorage(Map<UUID, Trainer> trainerStorage) {
    setStorage(trainerStorage);
  }

  @Override
  public UUID getEntityId(Trainer entity) {
    return entity.getUserId();
  }
}
