package org.vttish.gymapplication.security;

public record UserPrincipal(
    String username,
    Role role
) {
}
