package com.umamusume.backend.service;

import com.umamusume.backend.entity.Horse;
import com.umamusume.backend.repository.HorseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
public class TrainingService {

    private final HorseRepository horseRepository;

    public TrainingService(HorseRepository horseRepository) {
        this.horseRepository = horseRepository;
    }

    @Transactional
    public Map<String, Object> trainHorse(UUID horseId, String statType, int points) {
        Horse horse = horseRepository.findById(horseId)
                .orElseThrow(() -> new IllegalArgumentException("Horse not found"));

        boolean success = horse.trainStat(statType, points);
        
        if (success) {
            horseRepository.save(horse);
            return Map.of(
                "success", true,
                "message", "Training successful!",
                "horse", horse
            );
        } else {
            return Map.of(
                "success", false,
                "message", "Not enough training points!",
                "requiredPoints", points,
                "availablePoints", horse.getTrainingPoints()
            );
        }
    }

    @Transactional
    public Map<String, Object> restHorse(UUID horseId) {
        Horse horse = horseRepository.findById(horseId)
                .orElseThrow(() -> new IllegalArgumentException("Horse not found"));

        // Resting gives training points and recovers some stats
        int trainingPointsGained = 2 + (int)(Math.random() * 3); // 2-4 points
        horse.setTrainingPoints(horse.getTrainingPoints() + trainingPointsGained);

        // Small chance to gain a random stat point
        if (Math.random() < 0.1) { // 10% chance
            String[] stats = {"speed", "stamina", "power", "guts", "intelligence"};
            String randomStat = stats[(int)(Math.random() * stats.length)];
            horse.trainStat(randomStat, 1);
        }

        horseRepository.save(horse);

        return Map.of(
            "success", true,
            "message", "Horse rested and recovered!",
            "trainingPointsGained", trainingPointsGained,
            "horse", horse
        );
    }

    public Map<String, Object> getTrainingRecommendations(UUID horseId) {
        Horse horse = horseRepository.findById(horseId)
                .orElseThrow(() -> new IllegalArgumentException("Horse not found"));

        // Find the lowest stat to recommend training
        Map<String, Integer> stats = Map.of(
            "speed", horse.getSpeed(),
            "stamina", horse.getStamina(),
            "power", horse.getPower(),
            "guts", horse.getGuts(),
            "intelligence", horse.getIntelligence()
        );

        String lowestStat = stats.entrySet().stream()
                .min(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("speed");

        int currentLowest = stats.get(lowestStat);
        int pointsNeeded = Math.max(1, 100 - currentLowest);

        return Map.of(
            "recommendedStat", lowestStat,
            "currentValue", currentLowest,
            "pointsNeeded", pointsNeeded,
            "trainingPointsAvailable", horse.getTrainingPoints(),
            "canTrain", horse.getTrainingPoints() >= 1
        );
    }

    @Transactional
    public Map<String, Object> breedHorses(UUID sireId, UUID damId, String foalName, UUID ownerId) {
        Horse sire = horseRepository.findById(sireId)
                .orElseThrow(() -> new IllegalArgumentException("Sire not found"));
        Horse dam = horseRepository.findById(damId)
                .orElseThrow(() -> new IllegalArgumentException("Dam not found"));

        // Check if horses are eligible for breeding
        if (sire.getLevel() < 5 || dam.getLevel() < 5) {
            return Map.of(
                "success", false,
                "message", "Both horses must be at least level 5 to breed"
            );
        }

        if (sire.getRacesRun() < 3 || dam.getRacesRun() < 3) {
            return Map.of(
                "success", false,
                "message", "Both horses must have run at least 3 races to breed"
            );
        }

        // Create the foal
        Horse foal = new Horse(foalName, sire.getOwner(), sire, dam);
        foal.setOwner(sire.getOwner()); // Foal belongs to sire's owner
        horseRepository.save(foal);

        return Map.of(
            "success", true,
            "message", "Breeding successful! New foal created.",
            "foal", foal
        );
    }
}
