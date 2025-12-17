package com.tourflow.repository;

import com.tourflow.model.AvailabilityException;
import com.tourflow.model.Tour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AvailabilityExceptionRepository extends JpaRepository<AvailabilityException, UUID> {

    List<AvailabilityException> findByTour(Tour tour);

    List<AvailabilityException> findByTourId(UUID tourId);

    @Query("SELECT e FROM AvailabilityException e WHERE e.tour.id = :tourId AND " +
           "((e.startDate <= :endDate AND e.endDate >= :startDate))")
    List<AvailabilityException> findConflictingExceptions(@Param("tourId") UUID tourId, 
                                                       @Param("startDate") LocalDateTime startDate, 
                                                       @Param("endDate") LocalDateTime endDate);
}
