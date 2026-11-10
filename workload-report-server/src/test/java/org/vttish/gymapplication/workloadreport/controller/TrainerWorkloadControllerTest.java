package org.vttish.gymapplication.workloadreport.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.vttish.gymapplication.workloadreport.dto.ActionType;
import org.vttish.gymapplication.workloadreport.dto.MonthlyWorkloadItemResponseDto;
import org.vttish.gymapplication.workloadreport.dto.TrainerWorkloadResponseDto;
import org.vttish.gymapplication.workloadreport.dto.UpdateTrainerWorkloadRequestDto;
import org.vttish.gymapplication.workloadreport.dto.YearWorkloadItemResponseDto;
import org.vttish.gymapplication.workloadreport.exception.ResourceNotFoundException;
import org.vttish.gymapplication.workloadreport.handler.TrainerWorkloadHandler;

class TrainerWorkloadControllerTest {
  private static TrainerWorkloadHandler trainerWorkloadHandler;
  private static MockMvc mockMvc;
  private static ObjectMapper objectMapper;

  @BeforeAll
  static void setUp() {
    trainerWorkloadHandler = mock(TrainerWorkloadHandler.class);
    TrainerWorkloadController controller = new TrainerWorkloadController(trainerWorkloadHandler);

    mockMvc = MockMvcBuilders
        .standaloneSetup(controller)
        .setControllerAdvice(new GlobalExceptionHandler())
        .build();

    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
  }

  @BeforeEach
  void resetMocks() {
    reset(trainerWorkloadHandler);
  }

  @Test
  void updateWorkload_ShouldReturnNoContent_WhenRequestIsValidAndUsernamesMatch() throws Exception {
    UpdateTrainerWorkloadRequestDto request = new UpdateTrainerWorkloadRequestDto(
        "firstName.lastName", "firstName", "lastName", true,
        LocalDate.of(2026, 6, 15), 60, ActionType.ADD
    );

    doNothing().when(trainerWorkloadHandler).handleUpdateWorkload(any(UpdateTrainerWorkloadRequestDto.class));

    mockMvc.perform(post("/api/v1/trainers/firstName.lastName/workloads")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNoContent());

    verify(trainerWorkloadHandler).handleUpdateWorkload(request);
  }

  @Test
  void updateWorkload_ShouldReturnBadRequest_WhenUsernameInPathDoesNotMatchBody() throws Exception {
    UpdateTrainerWorkloadRequestDto request = new UpdateTrainerWorkloadRequestDto(
        "other.username", "firstName", "lastName", true,
        LocalDate.of(2026, 6, 15), 60, ActionType.ADD
    );

    mockMvc.perform(post("/api/v1/trainers/firstName.lastName/workloads")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void getWorkload_ShouldReturnOk_WhenTrainerExists() throws Exception {
    TrainerWorkloadResponseDto response = new TrainerWorkloadResponseDto(
        "firstName.lastName", "firstName", "lastName", true,
        List.of(new YearWorkloadItemResponseDto(2026, List.of(
            new MonthlyWorkloadItemResponseDto("JUNE", 60)
        )))
    );

    when(trainerWorkloadHandler.handleGetWorkload("firstName.lastName")).thenReturn(response);

    MvcResult result = mockMvc.perform(get("/api/v1/trainers/firstName.lastName/workload"))
        .andExpect(status().isOk())
        .andReturn();

    TrainerWorkloadResponseDto actual = objectMapper.readValue(
        result.getResponse().getContentAsString(),
        TrainerWorkloadResponseDto.class
    );

    assertEquals(response, actual);
  }

  @Test
  void getWorkload_ShouldReturnNotFound_WhenTrainerDoesNotExist() throws Exception {
    when(trainerWorkloadHandler.handleGetWorkload("unknown.trainer"))
        .thenThrow(new ResourceNotFoundException("Trainer workload not found"));

    mockMvc.perform(get("/api/v1/trainers/unknown.trainer/workload"))
        .andExpect(status().isNotFound());
  }
}
