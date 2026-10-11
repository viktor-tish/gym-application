package org.vttish.gymapplication.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequestDto(

    @NotBlank(message = "Username is mandatory")
    String username,

    @NotBlank(message = "Old password is mandatory")
    @Size(min = 10, message = "Password must be at least 10 characters")
    String oldPassword,

    @NotBlank(message = "New password is mandatory")
    @Size(min = 10, message = "Password must be at least 10 characters")
    String newPassword
) {}
