package org.vttish.gymapplication.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.vttish.gymapplication.security.properties.LoginAttemptProperties;

@Configuration
public class CacheConfig {

  @Bean
  public Cache<String, Integer> attemptsPerIpCache(LoginAttemptProperties loginAttemptProperties) {
    return Caffeine.newBuilder()
        .maximumSize(100_000)
        .expireAfterWrite(loginAttemptProperties.banDuration())
        .build();
  }

  @Bean
  public Cache<String, Instant> bannedIpsCache(LoginAttemptProperties loginAttemptProperties) {
    return Caffeine.newBuilder()
        .maximumSize(100_000)
        .expireAfterWrite(loginAttemptProperties.banDuration())
        .build();
  }
}
