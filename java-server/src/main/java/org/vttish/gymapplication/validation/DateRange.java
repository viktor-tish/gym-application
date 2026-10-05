package org.vttish.gymapplication.validation;

import java.time.LocalDate;

public interface DateRange {
  LocalDate getFromDate();

  LocalDate getToDate();
}
