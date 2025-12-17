package com.tourflow.repository;

import com.tourflow.model.AvailabilityRule;
import com.tourflow.model.Tour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AvailabilityRuleRepository extends JpaRepository<AvailabilityRule, UUID> {

    List<AvailabilityRule> findByTourAndActiveTrue(Tour tour);

    List<AvailabilityRule> findByTourId(UUID tourId);
}
