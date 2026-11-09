package org.vttish.gymapplication.workloadreport.dto;

import java.util.List;

public record YearWorkloadItemResponseDto(
    int year,
    List<MonthlyWorkloadItemResponseDto> months
) {
}
