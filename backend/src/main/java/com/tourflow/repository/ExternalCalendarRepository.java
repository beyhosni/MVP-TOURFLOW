package com.tourflow.repository;

import com.tourflow.model.ExternalCalendar;
import com.tourflow.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ExternalCalendarRepository extends JpaRepository<ExternalCalendar, UUID> {

    List<ExternalCalendar> findByGuideAndActiveTrue(User guide);

    List<ExternalCalendar> findByGuideId(UUID guideId);
}
