package org.vttish.gymapplication.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.vttish.gymapplication.dto.ChangePasswordDto;
import org.vttish.gymapplication.dto.CredentialsDto;

public interface AuthenticationService {
  boolean authenticate(@Valid CredentialsDto credentialsDto);

  void changePassword(@Valid ChangePasswordDto changePasswordDto);
  
  void toggleActivation(@NotBlank(message = "Username is mandatory") String username);
}
