package org.vttish.gymcrmsystem.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@RequiredArgsConstructor
@Getter
@Setter
public class Trainee extends User {
    private final UUID userId;
    private LocalDate dateOfBirth;
    private String address;

    public Trainee() {
        userId = UUID.randomUUID();
    }
}
