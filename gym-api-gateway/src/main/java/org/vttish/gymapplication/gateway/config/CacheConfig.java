package org.vttish.gymapplication.gateway.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfig {

  @Bean
  public Cache<UUID, Instant> jwtBlackListCache() {
    Expiry<UUID, Instant> expiry = Expiry.writing((jwtId, expiration) -> {
      Duration remainingTtl = Duration.between(Instant.now(), expiration);

      return remainingTtl.isNegative() ? Duration.ZERO : remainingTtl;
    });

    return Caffeine.newBuilder()
        .maximumSize(100_000)
        .expireAfter(expiry)
        .build();
  }
}
