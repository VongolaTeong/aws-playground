package com.umamusume.backend.service;

import com.umamusume.backend.entity.Horse;
import com.umamusume.backend.entity.Race;
import com.umamusume.backend.entity.RaceResult;
import com.umamusume.backend.repository.HorseRepository;
import com.umamusume.backend.repository.RaceResultRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RaceSimulationService {

    private final HorseRepository horseRepository;
    private final RaceResultRepository raceResultRepository;

    public RaceSimulationService(HorseRepository horseRepository, RaceResultRepository raceResultRepository) {
        this.horseRepository = horseRepository;
        this.raceResultRepository = raceResultRepository;
    }

    @Transactional
    public List<RaceResult> simulateRace(Race race, List<Horse> participants) {
        if (participants.size() < 2) {
            throw new IllegalArgumentException("Race needs at least 2 participants");
        }

        List<RaceParticipant> raceParticipants = participants.stream()
                .map(horse -> new RaceParticipant(horse, calculateRacePerformance(horse)))
                .collect(Collectors.toList());

        // Sort by performance (best first)
        raceParticipants.sort((a, b) -> Double.compare(b.performance, a.performance));

        // Add some randomness to make races more exciting
        addRandomness(raceParticipants);

        // Re-sort after adding randomness
        raceParticipants.sort((a, b) -> Double.compare(b.performance, a.performance));

        List<RaceResult> results = new ArrayList<>();
        int position = 1;

        for (RaceParticipant participant : raceParticipants) {
            Horse horse = participant.horse;
            double raceTime = calculateRaceTime(participant.performance);
            int earnings = calculateEarnings(position, race.getBasePrize());
            boolean won = position == 1;

            RaceResult result = new RaceResult(
                    race,
                    horse,
                    position,
                    earnings,
                    raceTime,
                    participant.performance
            );

            // Update horse stats
            horse.recordRaceResult(won, earnings);
            horseRepository.save(horse);

            results.add(raceResultRepository.save(result));
            position++;
        }

        return results;
    }

    private double calculateRacePerformance(Horse horse) {
        // Base performance from horse stats
        double basePerformance = horse.getRacePerformance();
        
        // Add level bonus
        double levelBonus = horse.getLevel() * 0.1;
        
        // Add experience bonus (diminishing returns)
        double expBonus = Math.log(horse.getExperience() + 1) * 0.05;
        
        // Add some randomness (±10%)
        double randomFactor = 0.9 + (Math.random() * 0.2);
        
        return (basePerformance + levelBonus + expBonus) * randomFactor;
    }

    private void addRandomness(List<RaceParticipant> participants) {
        for (RaceParticipant participant : participants) {
            // Add random factor that can significantly affect outcome
            double randomFactor = 0.7 + (Math.random() * 0.6); // 0.7 to 1.3
            participant.performance *= randomFactor;
        }
    }

    private double calculateRaceTime(double performance) {
        // Base time in seconds (simulate 1200m race)
        double baseTime = 120.0; // 2 minutes base
        // Better performance = faster time
        double timeReduction = (performance - 50.0) * 0.1;
        return Math.max(60.0, baseTime - timeReduction); // Minimum 1 minute
    }

    private int calculateEarnings(int position, int basePrize) {
        // Prize distribution: 1st gets most, others get less
        double[] prizeMultipliers = {1.0, 0.3, 0.2, 0.1, 0.05, 0.02, 0.01, 0.005};
        int multiplierIndex = Math.min(position - 1, prizeMultipliers.length - 1);
        return (int) (basePrize * prizeMultipliers[multiplierIndex]);
    }

    public List<Horse> getRecommendedHorses(Race race, int count) {
        // Get horses that are appropriate for this race level
        List<Horse> allHorses = horseRepository.findAll();
        
        return allHorses.stream()
                .filter(horse -> isHorseEligible(horse, race))
                .sorted((a, b) -> Double.compare(b.getRacePerformance(), a.getRacePerformance()))
                .limit(count)
                .collect(Collectors.toList());
    }

    private boolean isHorseEligible(Horse horse, Race race) {
        // Simple eligibility check - can be expanded based on race requirements
        return horse.getLevel() >= 1 && horse.getRacesRun() < 50; // Max 50 races per horse
    }

    public Map<String, Object> getRaceStatistics(Race race) {
        List<RaceResult> results = raceResultRepository.findByRace(race);
        
        if (results.isEmpty()) {
            return Map.of("message", "No race results found");
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalParticipants", results.size());
        stats.put("averageTime", results.stream().mapToDouble(RaceResult::getRaceTime).average().orElse(0.0));
        stats.put("fastestTime", results.stream().mapToDouble(RaceResult::getRaceTime).min().orElse(0.0));
        stats.put("totalPrizePool", results.stream().mapToInt(RaceResult::getEarnings).sum());
        
        return stats;
    }

    private static class RaceParticipant {
        final Horse horse;
        double performance;

        RaceParticipant(Horse horse, double performance) {
            this.horse = horse;
            this.performance = performance;
        }
    }
}
