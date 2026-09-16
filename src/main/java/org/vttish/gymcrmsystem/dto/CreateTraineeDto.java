package org.vttish.gymcrmsystem.dto;


import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class CreateTraineeDto {
    private String firstName;
    private String lastName;
    private boolean isActive;
    private LocalDate dateOfBirth;
    private String address;
}
