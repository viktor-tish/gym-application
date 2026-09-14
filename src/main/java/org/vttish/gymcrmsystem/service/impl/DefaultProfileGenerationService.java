package org.vttish.gymcrmsystem.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vttish.gymcrmsystem.persistence.dao.TraineeDao;
import org.vttish.gymcrmsystem.persistence.dao.TrainerDao;
import org.vttish.gymcrmsystem.service.ProfileGenerationService;

import java.security.SecureRandom;

@Service
public class DefaultProfileGenerationService implements ProfileGenerationService {

    @Autowired
    TraineeDao traineeDao;

    @Autowired
    TrainerDao trainerDao;

    private static final String PASSWORD_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private final SecureRandom random = new SecureRandom();
    private static final int PASSWORD_LENGTH = 10;

    @Override
    public String generateUniqueUsername(String firstName, String lastName) {
        StringBuilder baseUsername = new StringBuilder(firstName).append(".").append(lastName);
        int baseLength = baseUsername.length();

        String finalUsername = baseUsername.toString();
        int serialNumber = 1;

        while (traineeDao.existsByUsername(finalUsername) || trainerDao.existsByUsername(finalUsername)) {
            baseUsername.setLength(baseLength);
            finalUsername = baseUsername.append(serialNumber).toString();
            serialNumber++;
        }

        return finalUsername;
    }

    @Override
    public String generateRandomPassword() {
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);

        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            int charIdx = random.nextInt(PASSWORD_CHARS.length());
            password.append(PASSWORD_CHARS.charAt(charIdx));
        }

        return password.toString();
    }
}
