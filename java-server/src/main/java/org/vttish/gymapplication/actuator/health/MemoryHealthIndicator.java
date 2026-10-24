package org.vttish.gymapplication.actuator.health;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class MemoryHealthIndicator implements HealthIndicator {
  private final long freeMemoryThresholdMb;

  public MemoryHealthIndicator(
      @Value("${management.health.memory.free-threshold-mb}") long freeMemoryThresholdMb
  ) {
    this.freeMemoryThresholdMb = freeMemoryThresholdMb;
  }

  @Override
  public Health health() {
    long freeMemoryMb = Runtime.getRuntime().freeMemory() / (1024 * 1024);
    long totalMemoryMb = Runtime.getRuntime().totalMemory() / (1024 * 1024);

    Health.Builder healthBuilder = freeMemoryMb < freeMemoryThresholdMb
        ? Health.down()
        : Health.up();

    return healthBuilder
        .withDetail("freeMemoryMB", freeMemoryMb)
        .withDetail("totalMemoryMB", totalMemoryMb)
        .withDetail("freeMemoryThresholdMB", freeMemoryThresholdMb)
        .build();
  }
}
