package org.vttish.gymapplication.workloadreport.security.model;

public record ParsedInternalJwt(
    UserPrincipal userPrincipal,
    InternalJwtMetadata internalJwtMetadata
) {
}
