package com.tourflow.service;

import com.tourflow.dto.TourDTO;
import com.tourflow.exception.ResourceNotFoundException;
import com.tourflow.model.Tour;
import com.tourflow.model.TourStatus;
import com.tourflow.repository.TourRepository;
import com.tourflow.service.base.ServiceTestBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TourServiceTest extends ServiceTestBase {

    @Mock
    private TourRepository tourRepository;

    @InjectMocks
    private TourService tourService;

    private Tour mockTour;

    @BeforeEach
    public void setUp() {
        super.setUp();
        mockTour = new Tour();
        mockTour.setId(1L);
        mockTour.setTitle("Tour de Paris");
        mockTour.setDescription("Découvrez les merveilles de Paris");
        mockTour.setPrice(new BigDecimal("99.99"));
        mockTour.setDuration(3);
        mockTour.setStatus(TourStatus.ACTIVE);
        mockTour.setCreatedAt(LocalDateTime.now());
        mockTour.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    public void testGetTourById_Success() {
        // Given
        when(tourRepository.findById(1L)).thenReturn(Optional.of(mockTour));

        // When
        Tour result = tourService.getTourById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Tour de Paris", result.getTitle());
        verify(tourRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetTourById_NotFound() {
        // Given
        when(tourRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> tourService.getTourById(1L));
        verify(tourRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetAllTours_Success() {
        // Given
        List<Tour> tours = Arrays.asList(mockTour);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Tour> page = new PageImpl<>(tours, pageable, 1);

        when(tourRepository.findAll(any(Pageable.class))).thenReturn(page);

        // When
        Page<Tour> result = tourService.getAllTours(pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Tour de Paris", result.getContent().get(0).getTitle());
        verify(tourRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    public void testCreateTour_Success() {
        // Given
        TourDTO tourDTO = new TourDTO();
        tourDTO.setTitle("Tour de Lyon");
        tourDTO.setDescription("Découvrez Lyon");
        tourDTO.setPrice(new BigDecimal("79.99"));
        tourDTO.setDuration(2);

        when(tourRepository.save(any(Tour.class))).thenReturn(mockTour);

        // When
        Tour result = tourService.createTour(tourDTO);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(tourRepository, times(1)).save(any(Tour.class));
    }

    @Test
    public void testUpdateTour_Success() {
        // Given
        TourDTO tourDTO = new TourDTO();
        tourDTO.setTitle("Tour de Marseille");
        tourDTO.setDescription("Découvrez Marseille");
        tourDTO.setPrice(new BigDecimal("89.99"));
        tourDTO.setDuration(4);

        when(tourRepository.findById(1L)).thenReturn(Optional.of(mockTour));
        when(tourRepository.save(any(Tour.class))).thenReturn(mockTour);

        // When
        Tour result = tourService.updateTour(1L, tourDTO);

        // Then
        assertNotNull(result);
        verify(tourRepository, times(1)).findById(1L);
        verify(tourRepository, times(1)).save(any(Tour.class));
    }

    @Test
    public void testDeleteTour_Success() {
        // Given
        when(tourRepository.findById(1L)).thenReturn(Optional.of(mockTour));
        doNothing().when(tourRepository).delete(mockTour);

        // When
        tourService.deleteTour(1L);

        // Then
        verify(tourRepository, times(1)).findById(1L);
        verify(tourRepository, times(1)).delete(mockTour);
    }
}
