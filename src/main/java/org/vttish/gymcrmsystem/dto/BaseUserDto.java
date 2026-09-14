package org.vttish.gymcrmsystem.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseUserDto {
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private boolean isActive;
}
