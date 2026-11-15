package org.vttish.gymapplication.core.service;

import org.vttish.gymapplication.core.dto.auth.ChangePasswordRequestDto;
import org.vttish.gymapplication.core.dto.auth.LoginRequestDto;
import org.vttish.gymapplication.core.dto.auth.LoginResponseDto;
import org.vttish.gymapplication.core.dto.auth.UpdateUserStatusRequestDto;

public interface AuthenticationService {
  LoginResponseDto login(LoginRequestDto request, String ip);

  void changePassword(ChangePasswordRequestDto request);

  void updateStatus(String username, UpdateUserStatusRequestDto request);
}
