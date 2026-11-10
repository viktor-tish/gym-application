package org.vttish.gymapplication.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.vttish.gymapplication.dto.auth.CredentialsResponseDto;
import org.vttish.gymapplication.dto.auth.UpdateUserStatusRequestDto;
import org.vttish.gymapplication.dto.trainer.GetTrainerTrainingsRequestDto;
import org.vttish.gymapplication.dto.trainer.RegisterTrainerRequestDto;
import org.vttish.gymapplication.dto.trainer.TrainerProfileResponseDto;
import org.vttish.gymapplication.dto.trainer.UpdateTrainerProfileRequestDto;
import org.vttish.gymapplication.dto.trainer.UpdateTrainerProfileResponseDto;
import org.vttish.gymapplication.dto.training.TrainerTrainingItemResponseDto;
import org.vttish.gymapplication.dto.training.TrainingTypeResponseDto;
import org.vttish.gymapplication.exception.BadRequestException;
import org.vttish.gymapplication.exception.ResourceNotFoundException;
import org.vttish.gymapplication.facade.GymFacade;

class TrainerControllerTest {
  private static GymFacade gymFacade;
  private static MockMvc mockMvc;
  private static ObjectMapper objectMapper;

  @BeforeAll
  static void setUp() {
    gymFacade = mock(GymFacade.class);
    TrainerController controller = new TrainerController(gymFacade);

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
    reset(gymFacade);
  }

  @Test
  void register_ShouldReturnCreatedWithLocation_WhenRequestIsValid() throws Exception {
    UUID specializationId = UUID.randomUUID();
    RegisterTrainerRequestDto request = new RegisterTrainerRequestDto(
        "Alice", "Smith", specializationId
    );
    CredentialsResponseDto response = new CredentialsResponseDto("firstName.lastName", "pass1234");

    when(gymFacade.registerTrainer(request)).thenReturn(response);

    MvcResult result = mockMvc.perform(post("/api/v1/trainers")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andReturn();

    assertEquals(
        "http://localhost/api/v1/trainers/firstName.lastName",
        result.getResponse().getHeader("Location")
    );

    CredentialsResponseDto actual = objectMapper.readValue(
        result.getResponse().getContentAsString(),
        CredentialsResponseDto.class
    );

    assertEquals(response, actual);
    verify(gymFacade).registerTrainer(request);
  }

  @Test
  void register_ShouldReturnBadRequest_WhenPayloadIsInvalid() throws Exception {
    RegisterTrainerRequestDto invalidRequest = new RegisterTrainerRequestDto("", "", null);

    mockMvc.perform(post("/api/v1/trainers")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void getProfile_ShouldReturnOk_WhenTrainerExists() throws Exception {
    TrainingTypeResponseDto specialization = new TrainingTypeResponseDto(
        UUID.randomUUID(), "Fitness"
    );
    TrainerProfileResponseDto response = new TrainerProfileResponseDto(
        "Alice", "Smith", specialization, true, List.of()
    );

    when(gymFacade.getTrainerProfile("firstName.lastName")).thenReturn(response);

    MvcResult result = mockMvc.perform(get("/api/v1/trainers/firstName.lastName"))
        .andExpect(status().isOk())
        .andReturn();

    TrainerProfileResponseDto actual = objectMapper.readValue(
        result.getResponse().getContentAsString(),
        TrainerProfileResponseDto.class
    );

    assertEquals(response, actual);
  }

  @Test
  void getProfile_ShouldReturnNotFound_WhenTrainerDoesNotExist() throws Exception {
    when(gymFacade.getTrainerProfile("unknown.trainer"))
        .thenThrow(new ResourceNotFoundException("Trainer not found"));

    mockMvc.perform(get("/api/v1/trainers/unknown.trainer"))
        .andExpect(status().isNotFound());
  }

  @Test
  void updateProfile_ShouldReturnOk_WhenRequestIsValid() throws Exception {
    UpdateTrainerProfileRequestDto request = new UpdateTrainerProfileRequestDto(
        "Alice", "Updated", true
    );
    TrainingTypeResponseDto specialization = new TrainingTypeResponseDto(
        UUID.randomUUID(), "Fitness"
    );
    UpdateTrainerProfileResponseDto response = new UpdateTrainerProfileResponseDto(
        "firstName.lastName", "Alice", "Updated", specialization, true, List.of()
    );

    when(gymFacade.updateTrainerProfile("firstName.lastName", request)).thenReturn(response);

    MvcResult result = mockMvc.perform(put("/api/v1/trainers/firstName.lastName")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andReturn();

    UpdateTrainerProfileResponseDto actual = objectMapper.readValue(
        result.getResponse().getContentAsString(),
        UpdateTrainerProfileResponseDto.class
    );

    assertEquals(response, actual);
  }

  @Test
  void updateProfile_ShouldReturnNotFound_WhenTrainerDoesNotExist() throws Exception {
    UpdateTrainerProfileRequestDto request = new UpdateTrainerProfileRequestDto(
        "Alice", "Updated", true
    );

    when(gymFacade.updateTrainerProfile("unknown.trainer", request))
        .thenThrow(new ResourceNotFoundException("Trainer not found"));

    mockMvc.perform(put("/api/v1/trainers/unknown.trainer")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound());
  }

  @Test
  void updateProfile_ShouldReturnBadRequest_WhenPayloadIsInvalid() throws Exception {
    UpdateTrainerProfileRequestDto invalidRequest = new UpdateTrainerProfileRequestDto(
        "", "", null
    );

    mockMvc.perform(put("/api/v1/trainers/firstName.lastName")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void getTrainings_ShouldReturnOk_WhenParametersValid() throws Exception {
    TrainingTypeResponseDto trainingType = new TrainingTypeResponseDto(
        UUID.randomUUID(), "Fitness"
    );
    TrainerTrainingItemResponseDto trainingItem = new TrainerTrainingItemResponseDto(
        null, "Morning Workout", LocalDate.of(2025, 1, 1), trainingType, 45, "trainee.username"
    );

    when(gymFacade.getTrainerTrainings(
        eq("firstName.lastName"), any(GetTrainerTrainingsRequestDto.class)
    )).thenReturn(List.of(trainingItem));

    MvcResult result = mockMvc.perform(get("/api/v1/trainers/firstName.lastName/trainings")
            .param("fromDate", "2025-01-01")
            .param("toDate", "2025-12-31")
            .param("traineeName", "trainee.username"))
        .andExpect(status().isOk())
        .andReturn();

    List<TrainerTrainingItemResponseDto> actual = objectMapper.readValue(
        result.getResponse().getContentAsString(),
        new TypeReference<>() {
        }
    );

    assertEquals(List.of(trainingItem), actual);
  }

  @Test
  void deleteTraining_ShouldReturnNoContent_WhenTrainingExists() throws Exception {
    UUID trainingId = UUID.randomUUID();
    doNothing().when(gymFacade).deleteTraining(trainingId, "firstName.lastName");

    mockMvc.perform(delete("/api/v1/trainers/firstName.lastName/trainings/{id}", trainingId))
        .andExpect(status().isNoContent());

    verify(gymFacade).deleteTraining(trainingId, "firstName.lastName");
  }

  @Test
  void deleteTraining_ShouldReturnNotFound_WhenTrainingDoesNotExist() throws Exception {
    UUID trainingId = UUID.randomUUID();
    doThrow(new ResourceNotFoundException("Training not found"))
        .when(gymFacade).deleteTraining(trainingId, "firstName.lastName");

    mockMvc.perform(delete("/api/v1/trainers/firstName.lastName/trainings/{id}", trainingId))
        .andExpect(status().isNotFound());
  }

  @Test
  void getTrainings_ShouldReturnBadRequest_WhenDateRangeIsInvalid() throws Exception {
    mockMvc.perform(get("/api/v1/trainers/firstName.lastName/trainings")
            .param("fromDate", "2025-12-31")
            .param("toDate", "2025-01-01"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void updateStatus_ShouldReturnOk_WhenStatusChanged() throws Exception {
    UpdateUserStatusRequestDto request = new UpdateUserStatusRequestDto(false);
    doNothing().when(gymFacade).updateStatus("firstName.lastName", request);

    mockMvc.perform(patch("/api/v1/trainers/firstName.lastName/status")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk());

    verify(gymFacade).updateStatus("firstName.lastName", request);
  }

  @Test
  void updateStatus_ShouldReturnBadRequest_WhenStatusAlreadyMatches() throws Exception {
    UpdateUserStatusRequestDto request = new UpdateUserStatusRequestDto(true);
    doThrow(new BadRequestException("User is already active"))
        .when(gymFacade).updateStatus("firstName.lastName", request);

    mockMvc.perform(patch("/api/v1/trainers/firstName.lastName/status")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }
}
