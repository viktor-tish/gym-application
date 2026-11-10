package org.vttish.gymapplication.client.impl;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import org.vttish.gymapplication.client.dto.ActionType;
import org.vttish.gymapplication.client.dto.UpdateTrainerWorkloadRequestDto;
import org.vttish.gymapplication.client.mapper.TrainerWorkloadMapper;
import org.vttish.gymapplication.event.TrainingCreatedEvent;
import org.vttish.gymapplication.event.TrainingDeletedEvent;

@ExtendWith(MockitoExtension.class)
class DefaultTrainerWorkloadClientTest {

  private MockRestServiceServer mockServer;

  @Mock
  private TrainerWorkloadMapper trainerWorkloadMapper;

  private DefaultTrainerWorkloadClient trainerWorkloadClient;

  @BeforeEach
  void setUp() {
    RestClient.Builder restClientBuilder = RestClient.builder();
    mockServer = MockRestServiceServer.bindTo(restClientBuilder).build();
    RestClient restClient = restClientBuilder.baseUrl("http://localhost:8080").build();

    trainerWorkloadClient = new DefaultTrainerWorkloadClient(restClient, trainerWorkloadMapper);
  }

  @Test
  void submitWorkload_ShouldSendPostRequest_WhenTrainingCreatedEventIsProcessed() {
    TrainingCreatedEvent event = new TrainingCreatedEvent(
        "firstName.lastName",
        "firstName",
        "lastName",
        true,
        LocalDate.of(2026, 6, 15),
        60
    );

    UpdateTrainerWorkloadRequestDto mappedDto = new UpdateTrainerWorkloadRequestDto(
        "firstName.lastName",
        "firstName",
        "lastName",
        true,
        LocalDate.of(2026, 6, 15),
        60,
        ActionType.ADD
    );

    when(trainerWorkloadMapper.toUpdateTrainerWorkloadRequestDto(event, ActionType.ADD))
        .thenReturn(mappedDto);

    mockServer.expect(requestTo("http://localhost:8080/api/v1/trainers/firstName.lastName/workloads"))
        .andExpect(method(HttpMethod.POST))
        .andRespond(withStatus(HttpStatus.OK));

    trainerWorkloadClient.submitWorkload(event);

    mockServer.verify();
    verify(trainerWorkloadMapper).toUpdateTrainerWorkloadRequestDto(event, ActionType.ADD);
  }

  @Test
  void cancelWorkload_ShouldSendPostRequest_WhenTrainingDeletedEventIsProcessed() {
    TrainingDeletedEvent event = new TrainingDeletedEvent(
        "firstName.lastName",
        "firstName",
        "lastName",
        true,
        LocalDate.of(2026, 6, 15),
        60
    );

    UpdateTrainerWorkloadRequestDto mappedDto = new UpdateTrainerWorkloadRequestDto(
        "firstName.lastName",
        "firstName",
        "lastName",
        true,
        LocalDate.of(2026, 6, 15),
        60,
        ActionType.DELETE
    );

    when(trainerWorkloadMapper.toUpdateTrainerWorkloadRequestDto(event, ActionType.DELETE))
        .thenReturn(mappedDto);

    mockServer.expect(requestTo("http://localhost:8080/api/v1/trainers/firstName.lastName/workloads"))
        .andExpect(method(HttpMethod.POST))
        .andRespond(withStatus(HttpStatus.OK));

    trainerWorkloadClient.cancelWorkload(event);

    mockServer.verify();
    verify(trainerWorkloadMapper).toUpdateTrainerWorkloadRequestDto(event, ActionType.DELETE);
  }
}
