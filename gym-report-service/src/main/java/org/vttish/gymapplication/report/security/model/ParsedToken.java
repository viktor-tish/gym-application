package org.vttish.gymapplication.report.security.model;

public record ParsedToken(
    UserPrincipal userPrincipal,
    JwtMetadata jwtMetadata
) {
}
