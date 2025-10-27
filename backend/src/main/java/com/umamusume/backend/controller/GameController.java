package com.umamusume.backend.controller;

import com.umamusume.backend.entity.Horse;
import com.umamusume.backend.entity.Race;
import com.umamusume.backend.entity.RaceResult;
import com.umamusume.backend.repository.HorseRepository;
import com.umamusume.backend.repository.RaceRepository;
import com.umamusume.backend.service.RaceSimulationService;
import com.umamusume.backend.service.TrainingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/game")
public class GameController {

    private final RaceSimulationService raceSimulationService;
    private final TrainingService trainingService;
    private final HorseRepository horseRepository;
    private final RaceRepository raceRepository;

    public GameController(RaceSimulationService raceSimulationService, 
                         TrainingService trainingService,
                         HorseRepository horseRepository,
                         RaceRepository raceRepository) {
        this.raceSimulationService = raceSimulationService;
        this.trainingService = trainingService;
        this.horseRepository = horseRepository;
        this.raceRepository = raceRepository;
    }

    @PostMapping("/races/{raceId}/simulate")
    public ResponseEntity<List<RaceResult>> simulateRace(@PathVariable UUID raceId) {
        Race race = raceRepository.findById(raceId)
                .orElseThrow(() -> new IllegalArgumentException("Race not found"));

        List<Horse> participants = raceSimulationService.getRecommendedHorses(race, race.getMaxParticipants());
        List<RaceResult> results = raceSimulationService.simulateRace(race, participants);
        
        // Mark race as completed
        race.setIsCompleted(true);
        raceRepository.save(race);

        return ResponseEntity.ok(results);
    }

    @PostMapping("/horses/{horseId}/train")
    public ResponseEntity<Map<String, Object>> trainHorse(
            @PathVariable UUID horseId,
            @RequestBody TrainHorseRequest request) {
        Map<String, Object> result = trainingService.trainHorse(horseId, request.statType(), request.points());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/horses/{horseId}/rest")
    public ResponseEntity<Map<String, Object>> restHorse(@PathVariable UUID horseId) {
        Map<String, Object> result = trainingService.restHorse(horseId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/horses/{horseId}/training-recommendations")
    public ResponseEntity<Map<String, Object>> getTrainingRecommendations(@PathVariable UUID horseId) {
        Map<String, Object> result = trainingService.getTrainingRecommendations(horseId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/horses/breed")
    public ResponseEntity<Map<String, Object>> breedHorses(@RequestBody BreedHorsesRequest request) {
        Map<String, Object> result = trainingService.breedHorses(
                request.sireId(), 
                request.damId(), 
                request.foalName(), 
                request.ownerId()
        );
        return ResponseEntity.ok(result);
    }

    @GetMapping("/races/{raceId}/statistics")
    public ResponseEntity<Map<String, Object>> getRaceStatistics(@PathVariable UUID raceId) {
        Race race = raceRepository.findById(raceId)
                .orElseThrow(() -> new IllegalArgumentException("Race not found"));
        
        Map<String, Object> stats = raceSimulationService.getRaceStatistics(race);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/horses/{horseId}/stats")
    public ResponseEntity<Map<String, Object>> getHorseStats(@PathVariable UUID horseId) {
        Horse horse = horseRepository.findById(horseId)
                .orElseThrow(() -> new IllegalArgumentException("Horse not found"));

        Map<String, Object> stats = new HashMap<>();
        stats.put("id", horse.getId());
        stats.put("name", horse.getName());
        stats.put("level", horse.getLevel());
        stats.put("experience", horse.getExperience());
        stats.put("trainingPoints", horse.getTrainingPoints());
        stats.put("speed", horse.getSpeed());
        stats.put("stamina", horse.getStamina());
        stats.put("power", horse.getPower());
        stats.put("guts", horse.getGuts());
        stats.put("intelligence", horse.getIntelligence());
        stats.put("totalStats", horse.getTotalStats());
        stats.put("racePerformance", horse.getRacePerformance());
        stats.put("racesWon", horse.getRacesWon());
        stats.put("racesRun", horse.getRacesRun());
        stats.put("totalEarnings", horse.getTotalEarnings());
        stats.put("winRate", horse.getRacesRun() > 0 ? (double) horse.getRacesWon() / horse.getRacesRun() : 0.0);

        return ResponseEntity.ok(stats);
    }

    public record TrainHorseRequest(String statType, Integer points) {}
    public record BreedHorsesRequest(UUID sireId, UUID damId, String foalName, UUID ownerId) {}
}
