package com.umamusume.backend.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "horses")
public class Horse {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 128)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    // Horse Stats (0-100 scale)
    @Column(nullable = false)
    private Integer speed = 50;

    @Column(nullable = false)
    private Integer stamina = 50;

    @Column(nullable = false)
    private Integer power = 50;

    @Column(nullable = false)
    private Integer guts = 50;

    @Column(nullable = false)
    private Integer intelligence = 50;

    // Breeding Information
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sire_id")
    private Horse sire;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dam_id")
    private Horse dam;

    // Training and Experience
    @Column(nullable = false)
    private Integer level = 1;

    @Column(nullable = false)
    private Integer experience = 0;

    @Column(nullable = false)
    private Integer trainingPoints = 0;

    // Race Performance
    @Column(nullable = false)
    private Integer racesWon = 0;

    @Column(nullable = false)
    private Integer racesRun = 0;

    @Column(nullable = false)
    private Integer totalEarnings = 0;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    // Constructors
    public Horse() {}

    public Horse(String name, User owner) {
        this.name = name;
        this.owner = owner;
    }

    public Horse(String name, User owner, Horse sire, Horse dam) {
        this.name = name;
        this.owner = owner;
        this.sire = sire;
        this.dam = dam;
        // Inherit some stats from parents
        if (sire != null && dam != null) {
            this.speed = (sire.speed + dam.speed) / 2 + (int)(Math.random() * 20 - 10);
            this.stamina = (sire.stamina + dam.stamina) / 2 + (int)(Math.random() * 20 - 10);
            this.power = (sire.power + dam.power) / 2 + (int)(Math.random() * 20 - 10);
            this.guts = (sire.guts + dam.guts) / 2 + (int)(Math.random() * 20 - 10);
            this.intelligence = (sire.intelligence + dam.intelligence) / 2 + (int)(Math.random() * 20 - 10);
            
            // Ensure stats stay within bounds
            this.speed = Math.max(1, Math.min(100, this.speed));
            this.stamina = Math.max(1, Math.min(100, this.stamina));
            this.power = Math.max(1, Math.min(100, this.power));
            this.guts = Math.max(1, Math.min(100, this.guts));
            this.intelligence = Math.max(1, Math.min(100, this.intelligence));
        }
    }

    // Game Logic Methods
    public int getTotalStats() {
        return speed + stamina + power + guts + intelligence;
    }

    public double getRacePerformance() {
        // Weighted performance calculation
        return (speed * 0.3 + stamina * 0.25 + power * 0.2 + guts * 0.15 + intelligence * 0.1);
    }

    public void addExperience(int exp) {
        this.experience += exp;
        // Level up logic
        int requiredExp = this.level * 100;
        while (this.experience >= requiredExp) {
            this.experience -= requiredExp;
            this.level++;
            this.trainingPoints += 5; // Gain training points on level up
            requiredExp = this.level * 100;
        }
    }

    public boolean trainStat(String statType, int points) {
        if (trainingPoints < points) {
            return false;
        }
        
        trainingPoints -= points;
        switch (statType.toLowerCase()) {
            case "speed":
                this.speed = Math.min(100, this.speed + points);
                break;
            case "stamina":
                this.stamina = Math.min(100, this.stamina + points);
                break;
            case "power":
                this.power = Math.min(100, this.power + points);
                break;
            case "guts":
                this.guts = Math.min(100, this.guts + points);
                break;
            case "intelligence":
                this.intelligence = Math.min(100, this.intelligence + points);
                break;
            default:
                return false;
        }
        return true;
    }

    public void recordRaceResult(boolean won, int earnings) {
        this.racesRun++;
        if (won) {
            this.racesWon++;
        }
        this.totalEarnings += earnings;
        this.addExperience(won ? 50 : 20); // More exp for winning
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

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Integer getSpeed() {
        return speed;
    }

    public void setSpeed(Integer speed) {
        this.speed = speed;
    }

    public Integer getStamina() {
        return stamina;
    }

    public void setStamina(Integer stamina) {
        this.stamina = stamina;
    }

    public Integer getPower() {
        return power;
    }

    public void setPower(Integer power) {
        this.power = power;
    }

    public Integer getGuts() {
        return guts;
    }

    public void setGuts(Integer guts) {
        this.guts = guts;
    }

    public Integer getIntelligence() {
        return intelligence;
    }

    public void setIntelligence(Integer intelligence) {
        this.intelligence = intelligence;
    }

    public Horse getSire() {
        return sire;
    }

    public void setSire(Horse sire) {
        this.sire = sire;
    }

    public Horse getDam() {
        return dam;
    }

    public void setDam(Horse dam) {
        this.dam = dam;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getExperience() {
        return experience;
    }

    public void setExperience(Integer experience) {
        this.experience = experience;
    }

    public Integer getTrainingPoints() {
        return trainingPoints;
    }

    public void setTrainingPoints(Integer trainingPoints) {
        this.trainingPoints = trainingPoints;
    }

    public Integer getRacesWon() {
        return racesWon;
    }

    public void setRacesWon(Integer racesWon) {
        this.racesWon = racesWon;
    }

    public Integer getRacesRun() {
        return racesRun;
    }

    public void setRacesRun(Integer racesRun) {
        this.racesRun = racesRun;
    }

    public Integer getTotalEarnings() {
        return totalEarnings;
    }

    public void setTotalEarnings(Integer totalEarnings) {
        this.totalEarnings = totalEarnings;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
