package com.umamusume.backend.controller;

import com.umamusume.backend.dto.HorseResponseDto;
import com.umamusume.backend.entity.Horse;
import com.umamusume.backend.entity.User;
import com.umamusume.backend.repository.HorseRepository;
import com.umamusume.backend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/horses")
public class HorseController {

    private final HorseRepository horseRepository;
    private final UserRepository userRepository;

    public HorseController(HorseRepository horseRepository, UserRepository userRepository) {
        this.horseRepository = horseRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public List<HorseResponseDto> getAllHorses() {
        return horseRepository.findAll().stream()
                .map(HorseResponseDto::fromEntity)
                .toList();
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<HorseResponseDto> getHorseById(@PathVariable UUID id) {
        Optional<Horse> horse = horseRepository.findById(id);
        return horse.map(h -> ResponseEntity.ok(HorseResponseDto.fromEntity(h)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/owner/{ownerId}")
    @Transactional(readOnly = true)
    public List<HorseResponseDto> getHorsesByOwner(@PathVariable UUID ownerId) {
        return horseRepository.findByOwnerId(ownerId).stream()
                .map(HorseResponseDto::fromEntity)
                .toList();
    }

    @PostMapping
    @Transactional
    public ResponseEntity<HorseResponseDto> createHorse(@RequestBody CreateHorseRequest request) {
        Optional<User> owner = userRepository.findById(request.ownerId());
        if (owner.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        Horse horse = new Horse(request.name(), owner.get());
        Horse savedHorse = horseRepository.save(horse);
        return ResponseEntity.status(HttpStatus.CREATED).body(HorseResponseDto.fromEntity(savedHorse));
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<HorseResponseDto> updateHorse(@PathVariable UUID id, @RequestBody UpdateHorseRequest request) {
        Optional<Horse> existingHorse = horseRepository.findById(id);
        if (existingHorse.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Optional<User> owner = userRepository.findById(request.ownerId());
        if (owner.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        Horse horse = existingHorse.get();
        horse.setName(request.name());
        horse.setOwner(owner.get());
        Horse updatedHorse = horseRepository.save(horse);
        return ResponseEntity.ok(HorseResponseDto.fromEntity(updatedHorse));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHorse(@PathVariable UUID id) {
        if (!horseRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        horseRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    public record CreateHorseRequest(String name, UUID ownerId) {}
    public record UpdateHorseRequest(String name, UUID ownerId) {}
}
