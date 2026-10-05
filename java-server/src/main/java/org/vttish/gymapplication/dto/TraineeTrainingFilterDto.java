package org.vttish.gymapplication.dto;

import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.vttish.gymapplication.validation.DateRange;
import org.vttish.gymapplication.validation.ValidDateRange;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@ValidDateRange(message = "Invalid date range")
public class TraineeTrainingFilterDto implements DateRange {
  private LocalDate fromDate;
  private LocalDate toDate;
  private String trainerName;
  private UUID trainingTypeId;
}
