package org.vttish.gymapplication.gateway.security.model;

import java.time.Instant;
import java.util.UUID;

public record JwtMetadata(
    UUID jwtId,
    Instant expiration
) {
}
