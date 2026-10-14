package org.vttish.gymapplication.repository.filter;

import java.time.LocalDate;

public record TrainerTrainingsFilter(
    LocalDate fromDate,
    LocalDate toDate,
    String traineeName
) {
}
