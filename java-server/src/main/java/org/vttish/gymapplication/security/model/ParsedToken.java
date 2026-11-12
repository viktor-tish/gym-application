package org.vttish.gymapplication.security.model;

public record ParsedToken(
    UserPrincipal userPrincipal,
    JwtMetadata jwtMetadata
) {
}
