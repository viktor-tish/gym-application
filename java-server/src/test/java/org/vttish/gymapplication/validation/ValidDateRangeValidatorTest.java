package org.vttish.gymapplication.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class ValidDateRangeValidatorTest {
  private final ValidDateRangeValidator validator = new ValidDateRangeValidator();

  record TestDateRange(LocalDate fromDate, LocalDate toDate) implements DateRange {

    @Override
    public LocalDate getFromDate() {
      return fromDate;
    }

    @Override
    public LocalDate getToDate() {
      return toDate;
    }
  }

  @Test
  void shouldBeValidWhenValueIsNull() {
    assertTrue(validator.isValid(null, null));
  }

  @Test
  void shouldBeValidWhenFromDateIsNull() {
    TestDateRange range = new TestDateRange(null, LocalDate.of(2000, 1, 1));
    assertTrue(validator.isValid(range, null));
  }

  @Test
  void shouldBeValidWhenToDateIsNull() {
    TestDateRange range = new TestDateRange(LocalDate.of(2000, 1, 1), null);
    assertTrue(validator.isValid(range, null));
  }

  @Test
  void shouldBeValidWhenBothDatesAreNull() {
    TestDateRange range = new TestDateRange(null, null);
    assertTrue(validator.isValid(range, null));
  }

  @Test
  void shouldBeValidWhenFromDateIsBeforeToDate() {
    LocalDate from = LocalDate.of(2000, 1, 1);
    LocalDate to = LocalDate.of(2000, 1, 10);
    TestDateRange range = new TestDateRange(from, to);

    assertTrue(validator.isValid(range, null));
  }

  @Test
  void shouldBeValidWhenFromDateEqualsToDate() {
    LocalDate sameDate = LocalDate.of(2000, 5, 20);
    TestDateRange range = new TestDateRange(sameDate, sameDate);

    assertTrue(validator.isValid(range, null));
  }

  @Test
  void shouldBeInvalidWhenFromDateIsAfterToDate() {
    LocalDate from = LocalDate.of(2000, 10, 15);
    LocalDate to = LocalDate.of(2000, 10, 10);
    TestDateRange range = new TestDateRange(from, to);

    assertFalse(validator.isValid(range, null));
  }
}
