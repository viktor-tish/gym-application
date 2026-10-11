package org.vttish.gymapplication.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequestDto(

    @NotBlank(message = "Username is mandatory")
    String username,

    @NotBlank(message = "Password is mandatory")
    @Size(min = 10, message = "Password must be at least 10 characters")
    String password
) {}
