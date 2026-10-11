package org.vttish.gymapplication.dto.training;

import java.util.UUID;

public record TrainingTypeResponseDto(
    UUID id,
    String name
) {}
