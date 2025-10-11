package com.umamusume.backend.repository;

import com.umamusume.backend.entity.Horse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface HorseRepository extends JpaRepository<Horse, UUID> {
    List<Horse> findByOwnerId(UUID ownerId);
}
