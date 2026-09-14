package org.vttish.gymcrmsystem.dto;

import lombok.Getter;
import lombok.Setter;
import org.vttish.gymcrmsystem.entity.TrainingType;

import java.util.UUID;

@Getter
@Setter
public class TrainerDto extends BaseUserDto {
    private UUID userId;
    private TrainingType specialization;
}
