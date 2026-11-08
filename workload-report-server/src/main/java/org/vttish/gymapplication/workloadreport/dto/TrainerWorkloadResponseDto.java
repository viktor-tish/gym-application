package org.vttish.gymapplication.workloadreport.dto;

import java.util.List;
import java.util.Map;

public record TrainerWorkloadResponseDto(
    String username,
    String firstName,
    String lastName,
    boolean isActive,
    Map<Integer, List<MonthlyWorkloadItemResponseDto>> years
) {
}
