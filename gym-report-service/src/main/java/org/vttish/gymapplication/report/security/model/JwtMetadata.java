package org.vttish.gymapplication.report.security.model;

import java.time.Instant;
import java.util.UUID;

public record JwtMetadata(
    UUID jwtId,
    Instant expiration
) {
}
