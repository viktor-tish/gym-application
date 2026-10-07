package org.vttish.gymapplication.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordDto {

  @NotBlank(message = "Username is mandatory")
  private String username;

  @NotBlank(message = "Old password is mandatory")
  private String oldPassword;

  @NotBlank(message = "New password is mandatory")
  @Size(min = 10, message = "Password must be at least 10 characters")
  private String newPassword;
}
