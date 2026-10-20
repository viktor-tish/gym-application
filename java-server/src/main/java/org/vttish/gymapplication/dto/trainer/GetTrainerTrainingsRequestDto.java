package org.vttish.gymapplication.dto.trainer;

import java.time.LocalDate;
import org.vttish.gymapplication.validation.DateRange;
import org.vttish.gymapplication.validation.ValidDateRange;

@ValidDateRange(message = "Invalid date range")
public record GetTrainerTrainingsRequestDto(
    LocalDate fromDate,
    LocalDate toDate,
    String traineeName
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
