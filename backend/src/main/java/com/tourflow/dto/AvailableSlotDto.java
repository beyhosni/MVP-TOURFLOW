package com.tourflow.dto;

import java.time.LocalDateTime;

public class AvailableSlotDto {

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Integer availablePlaces;

    private Long tourId;

    // Constructeurs
    public AvailableSlotDto() {
    }

    public AvailableSlotDto(LocalDateTime startTime, LocalDateTime endTime, Integer availablePlaces, Long tourId) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.availablePlaces = availablePlaces;
        this.tourId = tourId;
    }

    // Getters et Setters
    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Integer getAvailablePlaces() {
        return availablePlaces;
    }

    public void setAvailablePlaces(Integer availablePlaces) {
        this.availablePlaces = availablePlaces;
    }

    public Long getTourId() {
        return tourId;
    }

    public void setTourId(Long tourId) {
        this.tourId = tourId;
    }
}
