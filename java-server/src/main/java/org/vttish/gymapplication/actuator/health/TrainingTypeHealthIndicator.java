package org.vttish.gymapplication.actuator.health;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import org.vttish.gymapplication.repository.TrainingTypeRepository;

@Component
@RequiredArgsConstructor
public class TrainingTypeHealthIndicator implements HealthIndicator {
  private final TrainingTypeRepository trainingTypeRepository;

  @Override
  public Health health() {
    try {
      long count = trainingTypeRepository.count();
      if (count > 0) {
        return Health.up()
            .withDetail("availableTrainingTypes", count)
            .withDetail("status", "Reference data is loaded")
            .build();
      }
      return Health.down()
          .withDetail("reason", "No training types found in database")
          .build();
    } catch (Exception e) {
      return Health.down(e)
          .withDetail("reason", "Cannot query training types from database")
          .build();
    }
  }
}
