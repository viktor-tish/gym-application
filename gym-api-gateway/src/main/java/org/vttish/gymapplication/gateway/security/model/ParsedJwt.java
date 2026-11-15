package org.vttish.gymapplication.gateway.security.model;

public record ParsedJwt(
    UserPrincipal userPrincipal,
    JwtMetadata jwtMetadata
) {
}
