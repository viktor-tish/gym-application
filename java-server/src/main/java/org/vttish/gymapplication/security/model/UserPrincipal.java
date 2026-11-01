package org.vttish.gymapplication.security.model;

public record UserPrincipal(
    String username,
    Role role
) {
}
