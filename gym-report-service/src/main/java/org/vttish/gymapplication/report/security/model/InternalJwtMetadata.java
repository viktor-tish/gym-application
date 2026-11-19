package org.vttish.gymapplication.report.security.model;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record InternalJwtMetadata(
    UUID jwtId,
    UUID externalJwtId,
    List<String> audiences,
    List<String> scopes,
    Instant expiration
) {
}
