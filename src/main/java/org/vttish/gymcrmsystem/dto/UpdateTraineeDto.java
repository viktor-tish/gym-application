package org.vttish.gymcrmsystem.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UpdateTraineeDto {
    private UUID userId;
    private String firstName;
    private String lastName;
    private String username;

    @ToString.Exclude
    private String password;

    private boolean isActive;
    private LocalDate dateOfBirth;
    private String address;
}
