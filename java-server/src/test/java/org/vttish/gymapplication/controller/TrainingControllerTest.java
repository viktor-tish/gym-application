package org.vttish.gymapplication.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import org.vttish.gymapplication.dto.training.CreateTrainingRequestDto;
import org.vttish.gymapplication.dto.training.TrainingTypeResponseDto;
import org.vttish.gymapplication.exception.ResourceNotFoundException;
import org.vttish.gymapplication.facade.GymFacade;
import org.vttish.gymapplication.security.Role;
import org.vttish.gymapplication.security.UserPrincipal;
import org.vttish.gymapplication.security.UserPrincipalArgumentResolver;

class TrainingControllerTest {
  private static GymFacade gymFacade;
  private static MockMvc mockMvc;
  private static ObjectMapper objectMapper;

  @BeforeAll
  static void setUp() {
    gymFacade = mock(GymFacade.class);
    TrainingController controller = new TrainingController(gymFacade);

    mockMvc = MockMvcBuilders
        .standaloneSetup(controller)
        .setCustomArgumentResolvers(new UserPrincipalArgumentResolver())
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
  void create_ShouldReturnCreated_WhenCallerIsTrainer() throws Exception {
    CreateTrainingRequestDto request = new CreateTrainingRequestDto(
        "firstName.lastName", "trainee.username", "Morning Workout", LocalDate.of(2025, 5, 1), 60
    );
    doNothing().when(gymFacade).createTraining(request);

    mockMvc.perform(post("/api/v1/trainings")
            .requestAttr("userPrincipal", new UserPrincipal("firstName.lastName", Role.TRAINER))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated());

    verify(gymFacade).createTraining(request);
  }

  @Test
  void create_ShouldReturnCreated_WhenCallerIsTrainee() throws Exception {
    CreateTrainingRequestDto request = new CreateTrainingRequestDto(
        "firstName.lastName", "trainee.username", "Morning Workout", LocalDate.of(2025, 5, 1), 60
    );
    doNothing().when(gymFacade).createTraining(request);

    mockMvc.perform(post("/api/v1/trainings")
            .requestAttr("userPrincipal", new UserPrincipal("trainee.username", Role.TRAINEE))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated());

    verify(gymFacade).createTraining(request);
  }

  @Test
  void create_ShouldReturnBadRequest_WhenPayloadIsInvalid() throws Exception {
    CreateTrainingRequestDto invalidRequest = new CreateTrainingRequestDto(
        "", "", "", null, -10
    );

    mockMvc.perform(post("/api/v1/trainings")
            .requestAttr("userPrincipal", new UserPrincipal("trainer.username", Role.TRAINER))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void create_ShouldReturnNotFound_WhenTrainerOrTraineeDoesNotExist() throws Exception {
    CreateTrainingRequestDto request = new CreateTrainingRequestDto(
        "unknown.trainer", "trainee.username", "Workout", LocalDate.of(2025, 5, 1), 60
    );
    doThrow(new ResourceNotFoundException("Trainer not found with username: unknown.trainer"))
        .when(gymFacade).createTraining(request);

    mockMvc.perform(post("/api/v1/trainings")
            .requestAttr("userPrincipal", new UserPrincipal("unknown.trainer", Role.TRAINER))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound());
  }

  @Test
  void create_ShouldReturnForbidden_WhenTrainerDoesNotMatchPrincipal() throws Exception {
    CreateTrainingRequestDto request = new CreateTrainingRequestDto(
        "trainer.username", "trainee.username", "Morning Workout", LocalDate.of(2025, 5, 1), 60
    );

    mockMvc.perform(post("/api/v1/trainings")
            .requestAttr("userPrincipal", new UserPrincipal("other.trainer", Role.TRAINER))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isForbidden());
  }

  @Test
  void create_ShouldReturnForbidden_WhenTraineeDoesNotMatchPrincipal() throws Exception {
    CreateTrainingRequestDto request = new CreateTrainingRequestDto(
        "trainer.username", "trainee.username", "Morning Workout", LocalDate.of(2025, 5, 1), 60
    );

    mockMvc.perform(post("/api/v1/trainings")
            .requestAttr("userPrincipal", new UserPrincipal("other.trainee", Role.TRAINEE))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isForbidden());
  }

  @Test
  void create_ShouldReturnForbidden_WhenPrincipalIsNull() throws Exception {
    CreateTrainingRequestDto request = new CreateTrainingRequestDto(
        "trainer.username", "trainee.username", "Morning Workout", LocalDate.of(2025, 5, 1), 60
    );

    mockMvc.perform(post("/api/v1/trainings")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isForbidden());
  }

  @Test
  void getTypes_ShouldReturnOk_WithListOfTrainingTypes() throws Exception {
    TrainingTypeResponseDto type1 = new TrainingTypeResponseDto(UUID.randomUUID(), "Fitness");
    TrainingTypeResponseDto type2 = new TrainingTypeResponseDto(UUID.randomUUID(), "Yoga");
    List<TrainingTypeResponseDto> response = List.of(type1, type2);

    when(gymFacade.getTrainingTypes()).thenReturn(response);

    MvcResult result = mockMvc.perform(get("/api/v1/trainings/types"))
        .andExpect(status().isOk())
        .andReturn();

    List<TrainingTypeResponseDto> actual = objectMapper.readValue(
        result.getResponse().getContentAsString(),
        new TypeReference<>() {}
    );

    assertEquals(response, actual);
    verify(gymFacade).getTrainingTypes();
  }
}
