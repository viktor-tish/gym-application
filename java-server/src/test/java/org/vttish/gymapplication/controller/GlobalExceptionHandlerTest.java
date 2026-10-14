package org.vttish.gymapplication.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.vttish.gymapplication.dto.error.ErrorResponseDto;
import org.vttish.gymapplication.exception.AuthenticationException;
import org.vttish.gymapplication.exception.BadRequestException;
import org.vttish.gymapplication.exception.ResourceNotFoundException;

class GlobalExceptionHandlerTest {
  private static MockMvc mockMvc;
  private static ObjectMapper objectMapper;

  @RestController
  @RequestMapping("/test")
  static class TestController {

    record TestRequestDto(
        @NotBlank(message = "Name is mandatory")
        String name
    ) {}

    @PostMapping("/validation")
    public void testValidation(@Valid @RequestBody TestRequestDto request) {
      // Intentionally empty: endpoint is used only to trigger @Valid argument validation
    }

    @GetMapping("/not-found")
    public void testNotFound() throws NoResourceFoundException {
      throw new NoResourceFoundException(HttpMethod.GET, "/test/not-found");
    }

    @GetMapping("/resource-not-found")
    public void testResourceNotFound() {
      throw new ResourceNotFoundException("Resource not found");
    }

    @GetMapping("/bad-request")
    public void testBadRequest() {
      throw new BadRequestException("Custom bad request");
    }

    @GetMapping("/unauthorized")
    public void testUnauthorized() {
      throw new AuthenticationException("Unauthorized access");
    }

    @GetMapping("/server-error")
    public void testServerError() {
      throw new RuntimeException("Unexpected exception");
    }
  }

  @BeforeAll
  static void setUp() {
    mockMvc = MockMvcBuilders
        .standaloneSetup(new TestController())
        .setControllerAdvice(new GlobalExceptionHandler())
        .build();

    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
  }

  @Test
  void shouldReturnBadRequestWhenValidationFails() throws Exception {
    String invalidJson = "{\"name\": \"\"}";

    MvcResult result = mockMvc.perform(post("/test/validation")
            .contentType(MediaType.APPLICATION_JSON)
            .content(invalidJson))
        .andExpect(status().isBadRequest())
        .andReturn();

    ErrorResponseDto response = objectMapper.readValue(
        result.getResponse().getContentAsString(),
        ErrorResponseDto.class
    );

    assertEquals(400, response.status());
    assertEquals("Bad Request", response.error());
    assertEquals("Validation failed", response.message());
    assertEquals("/test/validation", response.path());
    assertNotNull(response.timestamp());
    assertEquals("name", response.validationErrors().get(0).field());
    assertEquals("Name is mandatory", response.validationErrors().get(0).message());
  }

  @Test
  void shouldReturnBadRequestWhenJsonIsMalformed() throws Exception {
    String malformedJson = "{malformed-json";

    MvcResult result = mockMvc.perform(post("/test/validation")
            .contentType(MediaType.APPLICATION_JSON)
            .content(malformedJson))
        .andExpect(status().isBadRequest())
        .andReturn();

    ErrorResponseDto response = objectMapper.readValue(
        result.getResponse().getContentAsString(),
        ErrorResponseDto.class
    );

    assertEquals(400, response.status());
    assertEquals("Bad Request", response.error());
    assertEquals("Malformed JSON request body", response.message());
    assertEquals("/test/validation", response.path());
    assertNotNull(response.timestamp());
  }

  @Test
  void shouldReturnNotFoundWhenResourceDoesNotExist() throws Exception {
    MvcResult result = mockMvc.perform(get("/test/not-found"))
        .andExpect(status().isNotFound())
        .andReturn();

    ErrorResponseDto response = objectMapper.readValue(
        result.getResponse().getContentAsString(),
        ErrorResponseDto.class
    );

    assertEquals(404, response.status());
    assertEquals("Not Found", response.error());
    assertEquals("/test/not-found", response.path());
    assertNotNull(response.timestamp());
  }

  @Test
  void shouldReturnNotFoundWhenResourceNotFoundExceptionThrown() throws Exception {
    MvcResult result = mockMvc.perform(get("/test/resource-not-found"))
        .andExpect(status().isNotFound())
        .andReturn();

    ErrorResponseDto response = objectMapper.readValue(
        result.getResponse().getContentAsString(),
        ErrorResponseDto.class
    );

    assertEquals(404, response.status());
    assertEquals("Not Found", response.error());
    assertEquals("Resource not found", response.message());
    assertEquals("/test/resource-not-found", response.path());
    assertNotNull(response.timestamp());
  }

  @Test
  void shouldReturnBadRequestWhenBadRequestExceptionThrown() throws Exception {
    MvcResult result = mockMvc.perform(get("/test/bad-request"))
        .andExpect(status().isBadRequest())
        .andReturn();

    ErrorResponseDto response = objectMapper.readValue(
        result.getResponse().getContentAsString(),
        ErrorResponseDto.class
    );

    assertEquals(400, response.status());
    assertEquals("Bad Request", response.error());
    assertEquals("Custom bad request", response.message());
    assertEquals("/test/bad-request", response.path());
    assertNotNull(response.timestamp());
  }

  @Test
  void shouldReturnUnauthorizedWhenAuthenticationExceptionThrown() throws Exception {
    MvcResult result = mockMvc.perform(get("/test/unauthorized"))
        .andExpect(status().isUnauthorized())
        .andReturn();

    ErrorResponseDto response = objectMapper.readValue(
        result.getResponse().getContentAsString(),
        ErrorResponseDto.class
    );

    assertEquals(401, response.status());
    assertEquals("Unauthorized", response.error());
    assertEquals("Unauthorized access", response.message());
    assertEquals("/test/unauthorized", response.path());
    assertNotNull(response.timestamp());
  }

  @Test
  void shouldReturnMethodNotAllowedWhenHttpMethodIsNotSupported() throws Exception {
    MvcResult result = mockMvc.perform(get("/test/validation"))
        .andExpect(status().isMethodNotAllowed())
        .andReturn();

    ErrorResponseDto response = objectMapper.readValue(
        result.getResponse().getContentAsString(),
        ErrorResponseDto.class
    );

    assertEquals(405, response.status());
    assertEquals("Method Not Allowed", response.error());
    assertEquals("/test/validation", response.path());
    assertNotNull(response.timestamp());
  }

  @Test
  void shouldReturnInternalServerErrorOnUnhandledException() throws Exception {
    MvcResult result = mockMvc.perform(get("/test/server-error"))
        .andExpect(status().isInternalServerError())
        .andReturn();

    ErrorResponseDto response = objectMapper.readValue(
        result.getResponse().getContentAsString(),
        ErrorResponseDto.class
    );

    assertEquals(500, response.status());
    assertEquals("Internal Server Error", response.error());
    assertEquals("An unexpected error occurred", response.message());
    assertEquals("/test/server-error", response.path());
    assertNotNull(response.timestamp());
  }
}
