package org.vttish.gymapplication.core.dto.training;

import java.util.UUID;

public record TrainingTypeResponseDto(
    UUID id,
    String name
) {
}
