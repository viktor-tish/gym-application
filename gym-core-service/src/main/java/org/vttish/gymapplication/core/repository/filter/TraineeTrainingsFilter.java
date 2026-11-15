package org.vttish.gymapplication.core.repository.filter;

import java.time.LocalDate;
import java.util.UUID;

public record TraineeTrainingsFilter(
    LocalDate fromDate,
    LocalDate toDate,
    String trainerName,
    UUID trainingTypeId
) {
}
