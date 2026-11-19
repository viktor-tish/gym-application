package org.vttish.gymapplication.core.validation;

import java.time.LocalDate;

public interface DateRange {
  LocalDate getFromDate();

  LocalDate getToDate();
}
