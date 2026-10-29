package org.vttish.gymapplication.security.model;

public record AuthenticatedToken(
    UserPrincipal userPrincipal,
    JwtMetadata jwtMetadata
) {
}
