package org.vttish.gymapplication.workloadreport.dto;

import java.util.List;

public record TrainerWorkloadResponseDto(
    String username,
    String firstName,
    String lastName,
    boolean isActive,
    List<YearWorkloadItemResponseDto> years
) {
}
