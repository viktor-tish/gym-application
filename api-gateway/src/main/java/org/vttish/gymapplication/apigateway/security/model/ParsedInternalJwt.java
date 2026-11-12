package org.vttish.gymapplication.apigateway.security.model;

public record ParsedInternalJwt(
    UserPrincipal userPrincipal,
    InternalJwtMetadata internalJwtMetadata
) {
}
