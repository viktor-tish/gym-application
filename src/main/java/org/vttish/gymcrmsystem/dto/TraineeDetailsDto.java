package org.vttish.gymcrmsystem.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class TraineeDetailsDto {
    private String firstName;
    private String lastName;
    private String username;

    @ToString.Exclude
    private String password;

    private boolean isActive;
    private UUID userId;
    private LocalDate dateOfBirth;
    private String address;
}
