package org.vttish.gymcrmsystem.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
@Setter
public class Trainer extends User {
    private final UUID userId;
    private TrainingType specialization;

    public Trainer() {
        userId = UUID.randomUUID();
    }
}
