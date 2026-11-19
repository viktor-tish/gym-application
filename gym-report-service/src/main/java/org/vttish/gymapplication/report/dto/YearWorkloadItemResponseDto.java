package org.vttish.gymapplication.report.dto;

import java.util.List;

public record YearWorkloadItemResponseDto(
    int year,
    List<MonthlyWorkloadItemResponseDto> months
) {
}
