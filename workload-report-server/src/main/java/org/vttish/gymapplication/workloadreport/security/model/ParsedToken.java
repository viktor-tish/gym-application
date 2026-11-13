package org.vttish.gymapplication.workloadreport.security.model;

public record ParsedToken(
    UserPrincipal userPrincipal,
    JwtMetadata jwtMetadata
) {
}
