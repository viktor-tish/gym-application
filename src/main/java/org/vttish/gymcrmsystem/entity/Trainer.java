package org.vttish.gymcrmsystem.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
public class Trainer extends User {
    private final UUID userId;

    @Setter
    private TrainingType specialization;
}
