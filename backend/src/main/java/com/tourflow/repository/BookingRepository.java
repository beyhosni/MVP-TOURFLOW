package com.tourflow.repository;

import com.tourflow.model.Booking;
import com.tourflow.model.BookingStatus;
import com.tourflow.model.Tour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {

    List<Booking> findByTourAndStatus(Tour tour, BookingStatus status);

    List<Booking> findByTour(Tour tour);

    @Query("SELECT b FROM Booking b WHERE b.tour.id = :tourId AND b.status IN (:statuses) AND " +
           "((b.startDate <= :endDate AND b.endDate >= :startDate))")
    List<Booking> findConflictingBookings(@Param("tourId") UUID tourId, 
                                         @Param("startDate") LocalDateTime startDate, 
                                         @Param("endDate") LocalDateTime endDate,
                                         @Param("statuses") List<BookingStatus> statuses);

    @Query("SELECT b FROM Booking b WHERE b.status = 'PENDING' AND b.expiresAt < :now")
    List<Booking> findExpiredPendingBookings(@Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.customerEmail = :email ORDER BY b.createdAt DESC")
    List<Booking> findByCustomerEmail(@Param("email") String email);
}
