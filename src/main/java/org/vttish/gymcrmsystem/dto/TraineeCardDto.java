package org.vttish.gymcrmsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TraineeCardDto {
    private String firstName;
    private String lastName;
    private String username;
    private UUID userId;
}
