package org.vttish.gymapplication.core.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidDateRangeValidator implements ConstraintValidator<ValidDateRange, DateRange> {

  @Override
  public boolean isValid(DateRange value, ConstraintValidatorContext context) {
    if (value == null) {
      return true;
    }

    if (value.getFromDate() == null || value.getToDate() == null) {
      return true;
    }

    return !value.getFromDate().isAfter(value.getToDate());
  }
}
