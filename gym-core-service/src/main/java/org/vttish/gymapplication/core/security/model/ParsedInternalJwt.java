package org.vttish.gymapplication.core.security.model;

public record ParsedInternalJwt(
    UserPrincipal userPrincipal,
    InternalJwtMetadata internalJwtMetadata
) {
}
