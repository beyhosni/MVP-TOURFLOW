package com.tourflow.service;

import com.tourflow.model.Tour;
import com.tourflow.model.TourStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class PrivateMethodTest {

    private TourService tourService;
    private Tour mockTour;

    @BeforeEach
    public void setUp() {
        tourService = new TourService();

        mockTour = new Tour();
        mockTour.setId(1L);
        mockTour.setTitle("Tour de Paris");
        mockTour.setDescription("Découvrez les merveilles de Paris");
        mockTour.setPrice(new BigDecimal("99.99"));
        mockTour.setDuration(3);
        mockTour.setMaxParticipants(20);
        mockTour.setStatus(TourStatus.ACTIVE);
        mockTour.setCreatedAt(LocalDateTime.now());
        mockTour.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    public void testValidateTourData_PrivateMethod() throws Exception {
        // Given
        Method validateTourData = TourService.class.getDeclaredMethod("validateTourData", Tour.class);
        validateTourData.setAccessible(true);

        // When & Then - Valid tour
        assertDoesNotThrow(() -> validateTourData.invoke(tourService, mockTour));

        // When & Then - Invalid tour (null title)
        mockTour.setTitle(null);
        Exception exception = assertThrows(Exception.class, () -> validateTourData.invoke(tourService, mockTour));
        assertTrue(exception.getCause() instanceof IllegalArgumentException);
        assertEquals("Le titre du tour ne peut pas être vide", exception.getCause().getMessage());

        // When & Then - Invalid tour (negative price)
        mockTour.setTitle("Tour de Paris");
        mockTour.setPrice(new BigDecimal("-10.00"));
        exception = assertThrows(Exception.class, () -> validateTourData.invoke(tourService, mockTour));
        assertTrue(exception.getCause() instanceof IllegalArgumentException);
        assertEquals("Le prix du tour doit être positif", exception.getCause().getMessage());

        // When & Then - Invalid tour (negative duration)
        mockTour.setPrice(new BigDecimal("99.99"));
        mockTour.setDuration(-1);
        exception = assertThrows(Exception.class, () -> validateTourData.invoke(tourService, mockTour));
        assertTrue(exception.getCause() instanceof IllegalArgumentException);
        assertEquals("La durée du tour doit être positive", exception.getCause().getMessage());

        // When & Then - Invalid tour (negative max participants)
        mockTour.setDuration(3);
        mockTour.setMaxParticipants(-1);
        exception = assertThrows(Exception.class, () -> validateTourData.invoke(tourService, mockTour));
        assertTrue(exception.getCause() instanceof IllegalArgumentException);
        assertEquals("Le nombre maximum de participants doit être positif", exception.getCause().getMessage());
    }

    @Test
    public void testCalculateTotalPrice_PrivateMethod() throws Exception {
        // Given
        Method calculateTotalPrice = TourService.class.getDeclaredMethod("calculateTotalPrice", Tour.class, Integer.class);
        calculateTotalPrice.setAccessible(true);

        // When & Then
        BigDecimal result = (BigDecimal) calculateTotalPrice.invoke(tourService, mockTour, 2);
        assertEquals(new BigDecimal("199.98"), result);

        // When & Then - Zero participants
        result = (BigDecimal) calculateTotalPrice.invoke(tourService, mockTour, 0);
        assertEquals(BigDecimal.ZERO, result);

        // When & Then - Large number of participants
        result = (BigDecimal) calculateTotalPrice.invoke(tourService, mockTour, 20);
        assertEquals(new BigDecimal("1999.80"), result);
    }

    @Test
    public void testIsTourAvailable_PrivateMethod() throws Exception {
        // Given
        Method isTourAvailable = TourService.class.getDeclaredMethod("isTourAvailable", Tour.class, LocalDateTime.class, Integer.class);
        isTourAvailable.setAccessible(true);

        // When & Then - Tour active and within max participants
        Boolean result = (Boolean) isTourAvailable.invoke(tourService, mockTour, LocalDateTime.now().plusDays(7), 10);
        assertTrue(result);

        // When & Then - Tour inactive
        mockTour.setStatus(TourStatus.INACTIVE);
        result = (Boolean) isTourAvailable.invoke(tourService, mockTour, LocalDateTime.now().plusDays(7), 10);
        assertFalse(result);

        // When & Then - Exceeds max participants
        mockTour.setStatus(TourStatus.ACTIVE);
        result = (Boolean) isTourAvailable.invoke(tourService, mockTour, LocalDateTime.now().plusDays(7), 25);
        assertFalse(result);

        // When & Then - Past date
        result = (Boolean) isTourAvailable.invoke(tourService, mockTour, LocalDateTime.now().minusDays(7), 10);
        assertFalse(result);
    }
}
