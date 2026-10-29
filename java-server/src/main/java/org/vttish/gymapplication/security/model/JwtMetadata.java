package org.vttish.gymapplication.security.model;

import java.time.Instant;
import java.util.UUID;

public record JwtMetadata(
    UUID jwtId,
    Instant expiration
) {
}
