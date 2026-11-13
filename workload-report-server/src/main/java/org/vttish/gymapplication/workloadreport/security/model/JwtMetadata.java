package org.vttish.gymapplication.workloadreport.security.model;

import java.time.Instant;
import java.util.UUID;

public record JwtMetadata(
    UUID jwtId,
    Instant expiration
) {
}
