package com.tourflow.service;

import com.tourflow.model.Tour;
import com.tourflow.repository.TourRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CacheService {

    @Autowired
    private TourRepository tourRepository;

    @Cacheable(value = "tours", key = "#id")
    public Tour getTourById(UUID id) {
        return tourRepository.findById(id).orElse(null);
    }

    @Cacheable(value = "activeTours")
    public List<Tour> getAllActiveTours() {
        return tourRepository.findByActiveTrue();
    }

    @Cacheable(value = "guideTours", key = "#guide.id")
    public List<Tour> getToursByGuide(com.tourflow.model.User guide) {
        return tourRepository.findByGuideAndActiveTrue(guide);
    }

    @CacheEvict(value = {"tours", "activeTours", "guideTours"}, allEntries = true)
    public void clearAllToursCache() {
        // Cette méthode vide tous les caches liés aux tours
    }

    @CacheEvict(value = "tours", key = "#tour.id")
    public void evictTourCache(Tour tour) {
        // Cette méthode supprime du cache le tour spécifié
    }

    @CacheEvict(value = "guideTours", key = "#guide.id")
    public void evictGuideToursCache(com.tourflow.model.User guide) {
        // Cette méthode supprime du cache les tours du guide spécifié
    }
}
