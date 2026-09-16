package org.vttish.gymcrmsystem.dto;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class TraineeCardDto {
    private String firstName;
    private String lastName;
    private String username;
    private UUID userId;
}
