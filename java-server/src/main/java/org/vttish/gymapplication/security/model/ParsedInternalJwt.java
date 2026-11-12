package org.vttish.gymapplication.security.model;

public record ParsedInternalJwt(
    UserPrincipal userPrincipal,
    InternalJwtMetadata internalJwtMetadata
) {
}
