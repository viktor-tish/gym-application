package org.vttish.gymcrmsystem.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class TraineeDto extends BaseUserDto {
    private UUID userId;
    private LocalDate dateOfBirth;
    private String address;
}
