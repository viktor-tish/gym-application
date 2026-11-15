package org.vttish.gymapplication.core.security;

import java.time.Duration;

public interface LoginAttemptService {

  void loginFailed(String ip);

  void loginSucceeded(String ip);

  boolean isBanned(String ip);

  Duration remainingBanTime(String ip);
}
