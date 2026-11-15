package org.vttish.gymapplication.core.security.model;

public record ParsedToken(
    UserPrincipal userPrincipal,
    JwtMetadata jwtMetadata
) {
}
