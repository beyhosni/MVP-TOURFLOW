package com.tourflow.cache;

import com.tourflow.model.Tour;
import com.tourflow.repository.TourRepository;
import com.tourflow.service.TourService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
public class CacheTest {

    @Autowired
    private TourService tourService;

    @Autowired
    private CacheManager cacheManager;

    @MockBean
    private TourRepository tourRepository;

    private Tour testTour;

    @BeforeEach
    public void setUp() {
        // Initialiser l'objet de test
        testTour = new Tour();
        testTour.setId(1L);
        testTour.setTitle("Cache Test Tour");
        testTour.setDescription("A tour for cache testing");
        testTour.setPrice(new BigDecimal("99.99"));
        testTour.setDuration(3);
        testTour.setMaxParticipants(20);
        testTour.setStatus(TourStatus.ACTIVE);
        testTour.setStartDate(LocalDateTime.now().plusDays(7));
        testTour.setEndDate(LocalDateTime.now().plusDays(10));
        testTour.setCreatedAt(LocalDateTime.now());
        testTour.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    public void testCachePut() {
        // Given
        when(tourRepository.findById(1L)).thenReturn(Optional.of(testTour));

        // When - Premier appel, la méthode doit être exécutée
        Tour firstCall = tourService.getTourById(1L);

        // Then
        assertNotNull(firstCall);
        verify(tourRepository, times(1)).findById(1L);

        // When - Deuxième appel, la méthode ne doit pas être exécutée (résultat en cache)
        Tour secondCall = tourService.getTourById(1L);

        // Then
        assertNotNull(secondCall);
        assertEquals(firstCall, secondCall);
        verify(tourRepository, times(1)).findById(1L); // Toujours appelé une seule fois

        // Vérifier que le cache contient bien le résultat
        assertNotNull(cacheManager.getCache("tours").get(1L));
    }

    @Test
    public void testCacheEvict() {
        // Given
        when(tourRepository.findById(1L)).thenReturn(Optional.of(testTour));
        when(tourRepository.save(any(Tour.class))).thenReturn(testTour);

        // When - Premier appel pour mettre en cache
        tourService.getTourById(1L);

        // Then
        verify(tourRepository, times(1)).findById(1L);
        assertNotNull(cacheManager.getCache("tours").get(1L));

        // When - Mise à jour, qui doit vider le cache
        tourService.updateTour(1L, testTour);

        // Then
        verify(tourRepository, times(1)).save(any(Tour.class));
        assertNull(cacheManager.getCache("tours").get(1L));
    }

    @Test
    public void testCacheEvictOnDelete() {
        // Given
        when(tourRepository.findById(1L)).thenReturn(Optional.of(testTour));
        doNothing().when(tourRepository).delete(any(Tour.class));

        // When - Premier appel pour mettre en cache
        tourService.getTourById(1L);

        // Then
        verify(tourRepository, times(1)).findById(1L);
        assertNotNull(cacheManager.getCache("tours").get(1L));

        // When - Suppression, qui doit vider le cache
        tourService.deleteTour(1L);

        // Then
        verify(tourRepository, times(1)).delete(any(Tour.class));
        assertNull(cacheManager.getCache("tours").get(1L));
    }

    @Test
    public void testCacheableMethod() {
        // Given
        when(tourRepository.findByStatus(any(TourStatus.class))).thenReturn(List.of(testTour));

        // When - Premier appel
        List<Tour> firstCall = tourService.getActiveTours();

        // Then
        assertNotNull(firstCall);
        verify(tourRepository, times(1)).findByStatus(any(TourStatus.class));

        // When - Deuxième appel
        List<Tour> secondCall = tourService.getActiveTours();

        // Then
        assertNotNull(secondCall);
        assertEquals(firstCall, secondCall);
        verify(tourRepository, times(1)).findByStatus(any(TourStatus.class)); // Toujours appelé une seule fois
    }
}
