package org.vttish.gymapplication.apigateway.security.model;

public record ParsedJwt(
    UserPrincipal userPrincipal,
    JwtMetadata jwtMetadata
) {
}
