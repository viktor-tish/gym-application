package org.vttish.gymapplication.workloadreport.dto;

public record MonthlyWorkloadItemResponseDto(
    String month,
    int summaryDuration
) {
}
