package org.vttish.gymapplication.report.dto;

public record MonthlyWorkloadItemResponseDto(
    String month,
    int summaryDuration
) {
}
