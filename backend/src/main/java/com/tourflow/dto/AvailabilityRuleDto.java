package com.tourflow.dto;

import com.tourflow.model.DayOfWeek;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalTime;
import java.util.EnumSet;
import java.util.List;

public class AvailabilityRuleDto {

    private Long id;

    @NotNull(message = "Les jours de la semaine sont obligatoires")
    private EnumSet<DayOfWeek> daysOfWeek;

    @NotNull(message = "Les heures de début sont obligatoires")
    private List<LocalTime> startTimes;

    @NotNull(message = "Le délai minimum de réservation est obligatoire")
    @Positive(message = "Le délai minimum de réservation doit être positif")
    private Integer minBookingHours;

    @NotNull(message = "La capacité maximale est obligatoire")
    @Positive(message = "La capacité maximale doit être positive")
    private Integer maxCapacity;

    private Boolean active = true;

    private Long tourId;

    // Constructeurs
    public AvailabilityRuleDto() {
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EnumSet<DayOfWeek> getDaysOfWeek() {
        return daysOfWeek;
    }

    public void setDaysOfWeek(EnumSet<DayOfWeek> daysOfWeek) {
        this.daysOfWeek = daysOfWeek;
    }

    public List<LocalTime> getStartTimes() {
        return startTimes;
    }

    public void setStartTimes(List<LocalTime> startTimes) {
        this.startTimes = startTimes;
    }

    public Integer getMinBookingHours() {
        return minBookingHours;
    }

    public void setMinBookingHours(Integer minBookingHours) {
        this.minBookingHours = minBookingHours;
    }

    public Integer getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(Integer maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Long getTourId() {
        return tourId;
    }

    public void setTourId(Long tourId) {
        this.tourId = tourId;
    }
}
