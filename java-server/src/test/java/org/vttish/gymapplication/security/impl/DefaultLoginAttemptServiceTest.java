package org.vttish.gymapplication.security.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.github.benmanes.caffeine.cache.Cache;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.vttish.gymapplication.security.properties.LoginAttemptProperties;

@ExtendWith(MockitoExtension.class)
class DefaultLoginAttemptServiceTest {

  @Mock
  private Cache<String, Integer> attemptsPerIpCache;

  @Mock
  private Cache<String, Instant> bannedIpsCache;

  @Mock
  private LoginAttemptProperties loginAttemptProperties;

  private DefaultLoginAttemptService loginAttemptService;
  private ConcurrentMap<String, Integer> attemptsMap;

  private static final String IP = "127.0.0.1";

  @BeforeEach
  void setUp() {
    attemptsMap = new ConcurrentHashMap<>();
    loginAttemptService = new DefaultLoginAttemptService(
        attemptsPerIpCache, bannedIpsCache, loginAttemptProperties);
  }

  @Test
  void loginFailed_ShouldIncrementAttempts_WhenNotBanned() {
    when(attemptsPerIpCache.asMap()).thenReturn(attemptsMap);
    when(loginAttemptProperties.maxAttempts()).thenReturn(3);

    loginAttemptService.loginFailed(IP);

    assertEquals(1, attemptsMap.get(IP));
    verify(bannedIpsCache, never()).put(any(), any());
  }

  @Test
  void loginFailed_ShouldBanIp_WhenMaxAttemptsReached() {
    attemptsMap.put(IP, 2);
    when(attemptsPerIpCache.asMap()).thenReturn(attemptsMap);
    when(loginAttemptProperties.maxAttempts()).thenReturn(3);
    when(loginAttemptProperties.banDuration()).thenReturn(Duration.ofMinutes(5));

    loginAttemptService.loginFailed(IP);

    verify(bannedIpsCache).put(eq(IP), any(Instant.class));
    verify(attemptsPerIpCache).invalidate(IP);
  }

  @Test
  void loginFailed_ShouldDoNothing_WhenAlreadyBanned() {
    when(bannedIpsCache.getIfPresent(IP)).thenReturn(Instant.now().plus(Duration.ofMinutes(5)));

    loginAttemptService.loginFailed(IP);

    verifyNoInteractions(attemptsPerIpCache);
    verifyNoInteractions(loginAttemptProperties);
  }

  @Test
  void loginSucceeded_ShouldInvalidateCaches() {
    loginAttemptService.loginSucceeded(IP);

    verify(attemptsPerIpCache).invalidate(IP);
    verify(bannedIpsCache).invalidate(IP);
  }

  @Test
  void isBanned_ShouldReturnTrue_WhenIpInBannedCache() {
    when(bannedIpsCache.getIfPresent(IP)).thenReturn(Instant.now().plus(Duration.ofMinutes(5)));

    assertTrue(loginAttemptService.isBanned(IP));
  }

  @Test
  void isBanned_ShouldReturnFalse_WhenIpNotInBannedCache() {
    assertFalse(loginAttemptService.isBanned(IP));
  }

  @Test
  void remainingBanTime_ShouldReturnZero_WhenNotBanned() {
    assertEquals(Duration.ZERO, loginAttemptService.remainingBanTime(IP));
  }

  @Test
  void remainingBanTime_ShouldReturnDuration_WhenBanned() {
    Instant future = Instant.now().plus(Duration.ofMinutes(3));
    when(bannedIpsCache.getIfPresent(IP)).thenReturn(future);

    Duration remaining = loginAttemptService.remainingBanTime(IP);

    assertTrue(remaining.toSeconds() > 0 && remaining.toMinutes() <= 3);
  }
}
