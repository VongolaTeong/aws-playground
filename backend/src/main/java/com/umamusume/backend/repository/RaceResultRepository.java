package com.umamusume.backend.repository;

import com.umamusume.backend.entity.Race;
import com.umamusume.backend.entity.RaceResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RaceResultRepository extends JpaRepository<RaceResult, UUID> {
    List<RaceResult> findByRace(Race race);
    List<RaceResult> findByRaceId(UUID raceId);
    List<RaceResult> findByHorseId(UUID horseId);
}

