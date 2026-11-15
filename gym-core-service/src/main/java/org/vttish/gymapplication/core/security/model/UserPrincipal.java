package org.vttish.gymapplication.core.security.model;

public record UserPrincipal(
    String username,
    Role role
) {
}
