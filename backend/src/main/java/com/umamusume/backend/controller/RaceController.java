package com.umamusume.backend.controller;

import com.umamusume.backend.entity.Race;
import com.umamusume.backend.repository.RaceRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/races")
public class RaceController {

    private final RaceRepository raceRepository;

    public RaceController(RaceRepository raceRepository) {
        this.raceRepository = raceRepository;
    }

    @GetMapping
    public List<Race> getAllRaces() {
        return raceRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Race> getRaceById(@PathVariable UUID id) {
        Optional<Race> race = raceRepository.findById(id);
        return race.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/upcoming")
    public List<Race> getUpcomingRaces() {
        return raceRepository.findByScheduledAtAfter(OffsetDateTime.now());
    }

    @PostMapping
    public ResponseEntity<Race> createRace(@RequestBody CreateRaceRequest request) {
        Race race = new Race(request.name(), request.scheduledAt());
        Race savedRace = raceRepository.save(race);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedRace);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Race> updateRace(@PathVariable UUID id, @RequestBody UpdateRaceRequest request) {
        Optional<Race> existingRace = raceRepository.findById(id);
        if (existingRace.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Race race = existingRace.get();
        race.setName(request.name());
        race.setScheduledAt(request.scheduledAt());
        Race updatedRace = raceRepository.save(race);
        return ResponseEntity.ok(updatedRace);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRace(@PathVariable UUID id) {
        if (!raceRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        raceRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    public record CreateRaceRequest(String name, OffsetDateTime scheduledAt) {}
    public record UpdateRaceRequest(String name, OffsetDateTime scheduledAt) {}
}
