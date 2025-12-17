package com.tourflow.model;

import jakarta.persistence.*;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "availability_rules")
public class AvailabilityRule {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private boolean active = true;

    @ElementCollection
    @CollectionTable(name = "availability_rule_days", joinColumns = @JoinColumn(name = "availability_rule_id"))
    @Column(name = "day_of_week")
    @Enumerated(EnumType.STRING)
    private List<DayOfWeek> daysOfWeek;

    @ElementCollection
    @CollectionTable(name = "availability_rule_times", joinColumns = @JoinColumn(name = "availability_rule_id"))
    @Column(name = "start_time")
    private List<LocalTime> startTimes;

    @Column(nullable = false)
    private int minBookingHours = 12;

    @Column(nullable = false)
    private int maxCapacity;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // Relations
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_id", nullable = false)
    private Tour tour;

    // Constructeurs
    public AvailabilityRule() {}

    public AvailabilityRule(List<DayOfWeek> daysOfWeek, List<LocalTime> startTimes, 
                           int minBookingHours, int maxCapacity, Tour tour) {
        this.daysOfWeek = daysOfWeek;
        this.startTimes = startTimes;
        this.minBookingHours = minBookingHours;
        this.maxCapacity = maxCapacity;
        this.tour = tour;
    }

    // Getters et Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<DayOfWeek> getDaysOfWeek() {
        return daysOfWeek;
    }

    public void setDaysOfWeek(List<DayOfWeek> daysOfWeek) {
        this.daysOfWeek = daysOfWeek;
    }

    public List<LocalTime> getStartTimes() {
        return startTimes;
    }

    public void setStartTimes(List<LocalTime> startTimes) {
        this.startTimes = startTimes;
    }

    public int getMinBookingHours() {
        return minBookingHours;
    }

    public void setMinBookingHours(int minBookingHours) {
        this.minBookingHours = minBookingHours;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Tour getTour() {
        return tour;
    }

    public void setTour(Tour tour) {
        this.tour = tour;
    }
}
