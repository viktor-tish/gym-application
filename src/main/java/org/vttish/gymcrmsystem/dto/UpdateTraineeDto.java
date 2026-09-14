package org.vttish.gymcrmsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateTraineeDto {
    private UUID userId;
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private boolean isActive;
    private LocalDate dateOfBirth;
    private String address;
}
