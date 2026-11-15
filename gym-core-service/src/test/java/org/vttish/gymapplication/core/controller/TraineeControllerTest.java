package org.vttish.gymapplication.core.controller;

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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.vttish.gymapplication.core.dto.auth.CredentialsResponseDto;
import org.vttish.gymapplication.core.dto.auth.UpdateUserStatusRequestDto;
import org.vttish.gymapplication.core.dto.trainee.GetTraineeTrainingsRequestDto;
import org.vttish.gymapplication.core.dto.trainee.RegisterTraineeRequestDto;
import org.vttish.gymapplication.core.dto.trainee.TraineeProfileResponseDto;
import org.vttish.gymapplication.core.dto.trainee.UpdateTraineeProfileRequestDto;
import org.vttish.gymapplication.core.dto.trainee.UpdateTraineeProfileResponseDto;
import org.vttish.gymapplication.core.dto.trainee.UpdateTraineeTrainersRequestDto;
import org.vttish.gymapplication.core.dto.trainer.TrainerItemResponseDto;
import org.vttish.gymapplication.core.dto.training.TraineeTrainingItemResponseDto;
import org.vttish.gymapplication.core.exception.BadRequestException;
import org.vttish.gymapplication.core.exception.ResourceNotFoundException;
import org.vttish.gymapplication.core.facade.GymFacade;

class TraineeControllerTest {
  private static GymFacade gymFacade;
  private static MockMvc mockMvc;
  private static ObjectMapper objectMapper;

  @BeforeAll
  static void setUp() {
    gymFacade = mock(GymFacade.class);
    TraineeController controller = new TraineeController(gymFacade);

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
    RegisterTraineeRequestDto request = new RegisterTraineeRequestDto(
        "John", "Doe", LocalDate.of(2000, 1, 1), "123 Main St"
    );
    CredentialsResponseDto response = new CredentialsResponseDto("firstName.lastName", "pass1234");

    when(gymFacade.registerTrainee(request)).thenReturn(response);

    MvcResult result = mockMvc.perform(post("/api/v1/trainees")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andReturn();

    assertEquals(
        "http://localhost/api/v1/trainees/firstName.lastName",
        result.getResponse().getHeader("Location")
    );

    CredentialsResponseDto actual = objectMapper.readValue(
        result.getResponse().getContentAsString(),
        CredentialsResponseDto.class
    );

    assertEquals(response, actual);
    verify(gymFacade).registerTrainee(request);
  }

  @Test
  void register_ShouldReturnBadRequest_WhenPayloadIsInvalid() throws Exception {
    RegisterTraineeRequestDto invalidRequest = new RegisterTraineeRequestDto(
        "", "", null, ""
    );

    mockMvc.perform(post("/api/v1/trainees")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void getProfile_ShouldReturnOk_WhenTraineeExists() throws Exception {
    TraineeProfileResponseDto response = new TraineeProfileResponseDto(
        "John", "Doe", LocalDate.of(2000, 1, 1), "123 Main St", true, List.of()
    );

    when(gymFacade.getTraineeProfile("firstName.lastName")).thenReturn(response);

    MvcResult result = mockMvc.perform(get("/api/v1/trainees/firstName.lastName"))
        .andExpect(status().isOk())
        .andReturn();

    TraineeProfileResponseDto actual = objectMapper.readValue(
        result.getResponse().getContentAsString(),
        TraineeProfileResponseDto.class
    );

    assertEquals(response, actual);
  }

  @Test
  void getProfile_ShouldReturnNotFound_WhenTraineeDoesNotExist() throws Exception {
    when(gymFacade.getTraineeProfile("unknown.trainee"))
        .thenThrow(new ResourceNotFoundException("Trainee not found"));

    mockMvc.perform(get("/api/v1/trainees/unknown.trainee"))
        .andExpect(status().isNotFound());
  }

  @Test
  void updateProfile_ShouldReturnOk_WhenRequestIsValid() throws Exception {
    UpdateTraineeProfileRequestDto request = new UpdateTraineeProfileRequestDto(
        "John", "Updated", LocalDate.of(2000, 1, 1), "456 New St", true
    );
    UpdateTraineeProfileResponseDto response = new UpdateTraineeProfileResponseDto(
        "firstName.lastName", "John", "Updated", LocalDate.of(2000, 1, 1), "456 New St", true,
        List.of()
    );

    when(gymFacade.updateTraineeProfile("firstName.lastName", request)).thenReturn(response);

    MvcResult result = mockMvc.perform(put("/api/v1/trainees/firstName.lastName")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andReturn();

    UpdateTraineeProfileResponseDto actual = objectMapper.readValue(
        result.getResponse().getContentAsString(),
        UpdateTraineeProfileResponseDto.class
    );

    assertEquals(response, actual);
  }

  @Test
  void updateProfile_ShouldReturnNotFound_WhenTraineeDoesNotExist() throws Exception {
    UpdateTraineeProfileRequestDto request = new UpdateTraineeProfileRequestDto(
        "John", "Updated", LocalDate.of(2000, 1, 1), "456 New St", true
    );

    when(gymFacade.updateTraineeProfile("unknown.trainee", request))
        .thenThrow(new ResourceNotFoundException("Trainee not found"));

    mockMvc.perform(put("/api/v1/trainees/unknown.trainee")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound());
  }

  @Test
  void delete_ShouldReturnNoContent() throws Exception {
    doNothing().when(gymFacade).deleteTrainee("firstName.lastName");

    mockMvc.perform(delete("/api/v1/trainees/firstName.lastName"))
        .andExpect(status().isNoContent());

    verify(gymFacade).deleteTrainee("firstName.lastName");
  }

  @Test
  void getUnassignedActiveTrainers_ShouldReturnOk_WhenTraineeExists() throws Exception {
    TrainerItemResponseDto trainerItem = new TrainerItemResponseDto(
        "trainer.username", "Coach", "Bob", null
    );

    when(gymFacade.getUnassignedActiveTrainers("firstName.lastName"))
        .thenReturn(List.of(trainerItem));

    MvcResult result = mockMvc.perform(
            get("/api/v1/trainees/firstName.lastName/trainers/not-assigned")
        )
        .andExpect(status().isOk())
        .andReturn();

    List<TrainerItemResponseDto> actual = objectMapper.readValue(
        result.getResponse().getContentAsString(),
        new TypeReference<>() {
        }
    );

    assertEquals(List.of(trainerItem), actual);
  }

  @Test
  void getUnassignedActiveTrainers_ShouldReturnNotFound_WhenTraineeNotFound() throws Exception {
    when(gymFacade.getUnassignedActiveTrainers("unknown.trainee"))
        .thenThrow(new ResourceNotFoundException("Trainee not found"));

    mockMvc.perform(get("/api/v1/trainees/unknown.trainee/trainers/not-assigned"))
        .andExpect(status().isNotFound());
  }

  @Test
  void updateTrainers_ShouldReturnOk_WhenValid() throws Exception {
    UpdateTraineeTrainersRequestDto request = new UpdateTraineeTrainersRequestDto(
        List.of("trainer.username")
    );
    TrainerItemResponseDto trainerItem = new TrainerItemResponseDto(
        "trainer.username", "Coach", "Bob", null
    );

    when(gymFacade.updateTraineeTrainers("firstName.lastName", request))
        .thenReturn(List.of(trainerItem));

    MvcResult result = mockMvc.perform(put("/api/v1/trainees/firstName.lastName/trainers")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andReturn();

    List<TrainerItemResponseDto> actual = objectMapper.readValue(
        result.getResponse().getContentAsString(),
        new TypeReference<>() {
        }
    );

    assertEquals(List.of(trainerItem), actual);
  }

  @Test
  void updateTrainers_ShouldReturnBadRequest_WhenPayloadIsInvalid() throws Exception {
    UpdateTraineeTrainersRequestDto invalidRequest = new UpdateTraineeTrainersRequestDto(null);

    mockMvc.perform(put("/api/v1/trainees/firstName.lastName/trainers")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void getTrainings_ShouldReturnOk_WhenParametersValid() throws Exception {
    TraineeTrainingItemResponseDto trainingItem = new TraineeTrainingItemResponseDto(
        null, "Cardio", LocalDate.of(2025, 1, 1), null, 60, "trainer.username"
    );

    when(gymFacade.getTraineeTrainings(
        eq("firstName.lastName"), any(GetTraineeTrainingsRequestDto.class)
    )).thenReturn(List.of(trainingItem));

    MvcResult result = mockMvc.perform(get("/api/v1/trainees/firstName.lastName/trainings")
            .param("fromDate", "2025-01-01")
            .param("toDate", "2025-12-31"))
        .andExpect(status().isOk())
        .andReturn();

    List<TraineeTrainingItemResponseDto> actual = objectMapper.readValue(
        result.getResponse().getContentAsString(),
        new TypeReference<>() {
        }
    );

    assertEquals(List.of(trainingItem), actual);
  }

  @Test
  void getTrainings_ShouldReturnBadRequest_WhenDateRangeIsInvalid() throws Exception {
    mockMvc.perform(get("/api/v1/trainees/firstName.lastName/trainings")
            .param("fromDate", "2025-12-31")
            .param("toDate", "2025-01-01"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void updateStatus_ShouldReturnOk_WhenStatusChanged() throws Exception {
    UpdateUserStatusRequestDto request = new UpdateUserStatusRequestDto(false);
    doNothing().when(gymFacade).updateStatus("firstName.lastName", request);

    mockMvc.perform(patch("/api/v1/trainees/firstName.lastName/status")
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

    mockMvc.perform(patch("/api/v1/trainees/firstName.lastName/status")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }
}
