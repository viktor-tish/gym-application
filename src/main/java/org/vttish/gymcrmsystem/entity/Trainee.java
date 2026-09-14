package org.vttish.gymcrmsystem.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@RequiredArgsConstructor
@Getter
public class Trainee extends User {
    private final UUID userId;

    public Trainee() {
        userId = UUID.randomUUID();
    }

    @Setter
    private LocalDate dateOfBirth;

    @Setter
    private String address;
}
