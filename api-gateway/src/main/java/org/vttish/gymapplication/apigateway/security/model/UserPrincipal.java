package org.vttish.gymapplication.apigateway.security.model;

public record UserPrincipal(
    String username,
    Role role
) {
}
