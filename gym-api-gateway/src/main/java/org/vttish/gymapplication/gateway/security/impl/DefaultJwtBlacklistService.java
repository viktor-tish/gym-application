package org.vttish.gymapplication.gateway.security.impl;

import com.github.benmanes.caffeine.cache.Cache;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.vttish.gymapplication.gateway.security.JwtBlacklistService;

@Service
@RequiredArgsConstructor
public class DefaultJwtBlacklistService implements JwtBlacklistService {

  private final Cache<UUID, Instant> jwtBlackListCache;

  @Override
  public void blacklist(UUID jwtId, Instant expiration) {
    jwtBlackListCache.put(jwtId, expiration);
  }

  @Override
  public boolean isBlacklisted(UUID jwtId) {
    return jwtBlackListCache.getIfPresent(jwtId) != null;
  }
}
