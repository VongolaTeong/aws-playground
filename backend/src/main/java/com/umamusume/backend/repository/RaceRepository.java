package com.umamusume.backend.repository;

import com.umamusume.backend.entity.Race;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface RaceRepository extends JpaRepository<Race, UUID> {
    List<Race> findByScheduledAtAfter(OffsetDateTime scheduledAt);
    List<Race> findByScheduledAtBetween(OffsetDateTime start, OffsetDateTime end);
}
