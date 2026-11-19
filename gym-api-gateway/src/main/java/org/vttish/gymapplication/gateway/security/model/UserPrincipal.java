package org.vttish.gymapplication.gateway.security.model;

public record UserPrincipal(
    String username,
    Role role
) {
}
