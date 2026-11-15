package org.vttish.gymapplication.report.security.model;

public record UserPrincipal(
    String username,
    Role role
) {
}
