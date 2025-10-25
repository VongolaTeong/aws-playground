package com.umamusume.backend.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "races")
public class Race {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 128)
    private String name;

    @Column(nullable = false)
    private OffsetDateTime scheduledAt;

    @Column(nullable = false)
    private Integer basePrize = 1000;

    @Column(nullable = false)
    private Integer minLevel = 1;

    @Column(nullable = false)
    private Integer maxLevel = 10;

    @Column(nullable = false)
    private Integer maxParticipants = 8;

    @Column(nullable = false)
    private String raceType = "NORMAL"; // NORMAL, STAKES, HANDICAP, etc.

    @Column(nullable = false)
    private Integer distance = 1200; // in meters

    @Column(nullable = false)
    private String trackCondition = "GOOD"; // GOOD, FIRM, SOFT, HEAVY

    @Column(nullable = false)
    private Boolean isCompleted = false;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    // Constructors
    public Race() {}

    public Race(String name, OffsetDateTime scheduledAt) {
        this.name = name;
        this.scheduledAt = scheduledAt;
    }

    public Race(String name, OffsetDateTime scheduledAt, Integer basePrize, Integer minLevel, Integer maxLevel) {
        this.name = name;
        this.scheduledAt = scheduledAt;
        this.basePrize = basePrize;
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
    }

    // Game Logic Methods
    public boolean isEligibleForHorse(Horse horse) {
        return horse.getLevel() >= minLevel && 
               horse.getLevel() <= maxLevel && 
               !isCompleted;
    }

    public boolean isUpcoming() {
        return scheduledAt.isAfter(OffsetDateTime.now()) && !isCompleted;
    }

    public boolean isPast() {
        return scheduledAt.isBefore(OffsetDateTime.now()) || isCompleted;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OffsetDateTime getScheduledAt() {
        return scheduledAt;
    }

    public void setScheduledAt(OffsetDateTime scheduledAt) {
        this.scheduledAt = scheduledAt;
    }

    public Integer getBasePrize() {
        return basePrize;
    }

    public void setBasePrize(Integer basePrize) {
        this.basePrize = basePrize;
    }

    public Integer getMinLevel() {
        return minLevel;
    }

    public void setMinLevel(Integer minLevel) {
        this.minLevel = minLevel;
    }

    public Integer getMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(Integer maxLevel) {
        this.maxLevel = maxLevel;
    }

    public Integer getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(Integer maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public String getRaceType() {
        return raceType;
    }

    public void setRaceType(String raceType) {
        this.raceType = raceType;
    }

    public Integer getDistance() {
        return distance;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }

    public String getTrackCondition() {
        return trackCondition;
    }

    public void setTrackCondition(String trackCondition) {
        this.trackCondition = trackCondition;
    }

    public Boolean getIsCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(Boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
