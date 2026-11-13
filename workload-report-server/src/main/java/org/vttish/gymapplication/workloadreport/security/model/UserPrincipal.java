package org.vttish.gymapplication.workloadreport.security.model;

public record UserPrincipal(
    String username,
    Role role
) {
}
