package org.vttish.gymapplication.apigateway.security.impl;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.benmanes.caffeine.cache.Cache;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DefaultJwtBlacklistServiceTest {

  @Mock
  private Cache<UUID, Instant> jwtBlackListCache;

  private DefaultJwtBlacklistService blacklistService;

  private static final UUID JWT_ID = UUID.randomUUID();
  private static final Instant EXPIRATION = Instant.now().plusMillis(3600000L);

  @BeforeEach
  void setUp() {
    blacklistService = new DefaultJwtBlacklistService(jwtBlackListCache);
  }

  @Test
  void blacklist_ShouldPutTokenIntoCache() {
    blacklistService.blacklist(JWT_ID, EXPIRATION);

    verify(jwtBlackListCache).put(JWT_ID, EXPIRATION);
  }

  @Test
  void isBlacklisted_ShouldReturnTrue_WhenTokenIsInCache() {
    when(jwtBlackListCache.getIfPresent(JWT_ID)).thenReturn(EXPIRATION);

    boolean result = blacklistService.isBlacklisted(JWT_ID);

    assertTrue(result);
    verify(jwtBlackListCache).getIfPresent(JWT_ID);
  }

  @Test
  void isBlacklisted_ShouldReturnFalse_WhenTokenIsNotInCache() {
    when(jwtBlackListCache.getIfPresent(JWT_ID)).thenReturn(null);

    boolean result = blacklistService.isBlacklisted(JWT_ID);

    assertFalse(result);
    verify(jwtBlackListCache).getIfPresent(JWT_ID);
  }
}
