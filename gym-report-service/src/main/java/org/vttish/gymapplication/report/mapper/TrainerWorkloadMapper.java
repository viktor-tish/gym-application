package org.vttish.gymapplication.report.mapper;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.vttish.gymapplication.report.dto.MonthlyWorkloadItemResponseDto;
import org.vttish.gymapplication.report.dto.TrainerWorkloadResponseDto;
import org.vttish.gymapplication.report.dto.YearWorkloadItemResponseDto;
import org.vttish.gymapplication.report.entity.MonthlyWorkload;
import org.vttish.gymapplication.report.entity.TrainerProfile;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface TrainerWorkloadMapper {

  @Mapping(target = "username", source = "username")
  @Mapping(target = "firstName", source = "firstName")
  @Mapping(target = "lastName", source = "lastName")
  @Mapping(target = "isActive", source = "active")
  @Mapping(target = "years", source = "workloads")
  TrainerWorkloadResponseDto toTrainerWorkloadResponseDto(TrainerProfile profile);

  default List<YearWorkloadItemResponseDto> toYearWorkloads(Collection<MonthlyWorkload> workloads) {
    return workloads.stream()
        .collect(Collectors.groupingBy(
            MonthlyWorkload::getTrainingYear, TreeMap::new, Collectors.toList()))
        .entrySet().stream()
        .map(entry -> new YearWorkloadItemResponseDto(
            entry.getKey(), toMonthWorkloads(entry.getValue()))
        ).toList();
  }

  default List<MonthlyWorkloadItemResponseDto> toMonthWorkloads(List<MonthlyWorkload> workloads) {
    return workloads.stream()
        .sorted(Comparator.comparing(MonthlyWorkload::getTrainingMonth))
        .map(w -> new MonthlyWorkloadItemResponseDto(
            w.getTrainingMonth().name(), w.getTrainingSummaryDuration())
        ).toList();
  }
}
