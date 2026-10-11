package org.vttish.gymapplication.dto.auth;

import jakarta.validation.constraints.NotNull;

public record UpdateUserStatusRequestDto(

    @NotNull(message = "Active status is mandatory")
    Boolean isActive
) {
}
