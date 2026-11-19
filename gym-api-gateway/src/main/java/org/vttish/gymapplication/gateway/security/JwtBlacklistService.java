package org.vttish.gymapplication.gateway.security;

import java.time.Instant;
import java.util.UUID;

public interface JwtBlacklistService {

  void blacklist(UUID jwtId, Instant expiration);

  boolean isBlacklisted(UUID jwtId);
}
