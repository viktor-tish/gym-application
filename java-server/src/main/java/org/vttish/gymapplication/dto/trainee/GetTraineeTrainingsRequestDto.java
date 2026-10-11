package org.vttish.gymapplication.dto.trainee;

import java.time.LocalDate;
import java.util.UUID;
import org.vttish.gymapplication.validation.DateRange;
import org.vttish.gymapplication.validation.ValidDateRange;

@ValidDateRange(message = "Invalid date range")
public record GetTraineeTrainingsRequestDto(
    LocalDate fromDate,
    LocalDate toDate,
    String trainerName,
    UUID trainingTypeId
) implements DateRange {

  @Override
  public LocalDate getFromDate() {
    return fromDate;
  }

  @Override
  public LocalDate getToDate() {
    return toDate;
  }
}
