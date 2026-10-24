package org.vttish.gymapplication.service;

import org.vttish.gymapplication.dto.auth.ChangePasswordRequestDto;
import org.vttish.gymapplication.dto.auth.LoginRequestDto;
import org.vttish.gymapplication.dto.auth.LoginResponseDto;
import org.vttish.gymapplication.dto.auth.UpdateUserStatusRequestDto;

public interface AuthenticationService {
  LoginResponseDto login(LoginRequestDto request);

  void changePassword(ChangePasswordRequestDto request);

  void updateStatus(String username, UpdateUserStatusRequestDto request);
}
