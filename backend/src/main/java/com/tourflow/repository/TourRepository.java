package com.tourflow.repository;

import com.tourflow.model.Tour;
import com.tourflow.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface TourRepository extends JpaRepository<Tour, UUID> {

    List<Tour> findByGuideAndActiveTrue(User guide);

    List<Tour> findByActiveTrue();

    @Query("SELECT t FROM Tour t WHERE t.active = true AND " +
           "NOT EXISTS (SELECT 1 FROM Booking b WHERE b.tour = t AND b.status = 'CONFIRMED' AND " +
           "((b.startDate <= :endDate AND b.endDate >= :startDate)))")
    List<Tour> findAvailableTours(@Param("startDate") LocalDateTime startDate, 
                                 @Param("endDate") LocalDateTime endDate);

    @Query("SELECT t FROM Tour t WHERE t.guide.id = :guideId AND t.active = true")
    List<Tour> findByGuideId(@Param("guideId") UUID guideId);
}
