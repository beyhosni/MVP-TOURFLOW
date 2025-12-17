package com.tourflow.service;

import com.tourflow.model.Tour;
import com.tourflow.model.User;
import com.tourflow.repository.TourRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TourService {

    @Autowired
    private TourRepository tourRepository;

    public Tour createTour(Tour tour, User guide) {
        tour.setGuide(guide);
        return tourRepository.save(tour);
    }

    public Tour updateTour(UUID tourId, Tour tourDetails, User guide) {
        Tour tour = getTourByIdAndGuide(tourId, guide);

        tour.setTitle(tourDetails.getTitle());
        tour.setDescription(tourDetails.getDescription());
        tour.setDurationMinutes(tourDetails.getDurationMinutes());
        tour.setLocation(tourDetails.getLocation());
        tour.setMaxCapacity(tourDetails.getMaxCapacity());
        tour.setPrice(tourDetails.getPrice());
        tour.setLanguage(tourDetails.getLanguage());
        tour.setPhotoUrls(tourDetails.getPhotoUrls());

        return tourRepository.save(tour);
    }

    public void deleteTour(UUID tourId, User guide) {
        Tour tour = getTourByIdAndGuide(tourId, guide);
        tour.setActive(false);
        tourRepository.save(tour);
    }

    public Tour getTourById(UUID tourId) {
        return tourRepository.findById(tourId)
                .orElseThrow(() -> new RuntimeException("Tour non trouvé avec l'ID : " + tourId));
    }

    public Tour getTourByIdAndGuide(UUID tourId, User guide) {
        Tour tour = getTourById(tourId);

        if (!tour.getGuide().getId().equals(guide.getId())) {
            throw new RuntimeException("Vous n'êtes pas autorisé à accéder à ce tour");
        }

        return tour;
    }

    public List<Tour> getAllActiveTours() {
        return tourRepository.findByActiveTrue();
    }

    public List<Tour> getToursByGuide(User guide) {
        return tourRepository.findByGuideAndActiveTrue(guide);
    }

    @Transactional
    public List<Tour> findAvailableTours(LocalDateTime startDate, LocalDateTime endDate) {
        return tourRepository.findAvailableTours(startDate, endDate);
    }
}
