package org.vttish.gymapplication.report.security.model;

public record ParsedInternalJwt(
    UserPrincipal userPrincipal,
    InternalJwtMetadata internalJwtMetadata
) {
}
