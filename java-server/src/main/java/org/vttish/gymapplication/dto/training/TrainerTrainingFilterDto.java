package org.vttish.gymapplication.dto.training;

import java.time.LocalDate;
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
public class TrainerTrainingFilterDto implements DateRange {
  private LocalDate fromDate;
  private LocalDate toDate;
  private String traineeName;
}
