package org.vttish.gymcrmsystem.dto;

import lombok.*;
import org.vttish.gymcrmsystem.entity.TrainingType;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class TrainerCardDto {
    private String firstName;
    private String lastName;
    private String username;
    private UUID userId;
    private TrainingType specialization;
}
