package org.vttish.gymapplication.security.impl;

import com.github.benmanes.caffeine.cache.Cache;
import java.time.Duration;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.vttish.gymapplication.security.LoginAttemptService;
import org.vttish.gymapplication.security.properties.LoginAttemptProperties;

@Service
@RequiredArgsConstructor
public class DefaultLoginAttemptService implements LoginAttemptService {

  private final Cache<String, Integer> attemptsPerIpCache;
  private final Cache<String, Instant> bannedIpsCache;
  private final LoginAttemptProperties loginAttemptProperties;

  @Override
  public void loginFailed(String ip) {
    if (isBanned(ip)) {
      return;
    }

    // merge resets cache expiration
    int attempts = attemptsPerIpCache.asMap().merge(ip, 1, Integer::sum);

    if (attempts >= loginAttemptProperties.maxAttempts()) {
      bannedIpsCache.put(ip, Instant.now().plus(loginAttemptProperties.banDuration()));
      attemptsPerIpCache.invalidate(ip);
    }
  }

  @Override
  public void loginSucceeded(String ip) {
    attemptsPerIpCache.invalidate(ip);
    bannedIpsCache.invalidate(ip);
  }

  @Override
  public boolean isBanned(String ip) {
    return bannedIpsCache.getIfPresent(ip) != null;
  }

  @Override
  public Duration remainingBanTime(String ip) {
    Instant bannedUntil = bannedIpsCache.getIfPresent(ip);

    if (bannedUntil == null) {
      return Duration.ZERO;
    }

    Duration remaining = Duration.between(Instant.now(), bannedUntil);

    return remaining.isNegative() ? Duration.ZERO : remaining;
  }
}
