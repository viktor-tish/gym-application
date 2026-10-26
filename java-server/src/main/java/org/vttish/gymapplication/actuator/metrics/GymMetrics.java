package org.vttish.gymapplication.actuator.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;
import org.vttish.gymapplication.repository.TraineeRepository;
import org.vttish.gymapplication.repository.TrainerRepository;
import org.vttish.gymapplication.repository.TrainingRepository;

@Component
public class GymMetrics {
  private final Counter trainingCreatedCounter;
  private final Counter loginSuccessCounter;
  private final Counter loginFailureCounter;

  public GymMetrics(
      MeterRegistry registry,
      TraineeRepository traineeRepository,
      TrainerRepository trainerRepository,
      TrainingRepository trainingRepository
  ) {
    this.trainingCreatedCounter = Counter.builder("gym_trainings_created_total")
        .description("Total number of training sessions created")
        .register(registry);

    this.loginSuccessCounter = Counter.builder("gym_logins_total")
        .tag("status", "success")
        .description("Total login attempts")
        .register(registry);

    this.loginFailureCounter = Counter.builder("gym_logins_total")
        .tag("status", "failure")
        .description("Total login attempts")
        .register(registry);

    Gauge.builder("gym_trainings_total", trainingRepository, TrainingRepository::count)
        .description("Total trainings in the system")
        .register(registry);

    Gauge.builder("gym_trainees_total", traineeRepository, TraineeRepository::count)
        .description("Current number of registered trainees")
        .register(registry);

    Gauge.builder("gym_trainers_total", trainerRepository, TrainerRepository::count)
        .description("Current number of registered trainers")
        .register(registry);
  }

  public void incrementTrainingCreated() {
    trainingCreatedCounter.increment();
  }

  public void incrementLoginSuccess() {
    loginSuccessCounter.increment();
  }

  public void incrementLoginFailure() {
    loginFailureCounter.increment();
  }
}
