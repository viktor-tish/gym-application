package org.vttish.gymapplication.core.security.properties;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.security.login-attempt")
public record LoginAttemptProperties(

    @Min(1)
    Integer maxAttempts,

    @NotNull
    Duration banDuration
) {
}
