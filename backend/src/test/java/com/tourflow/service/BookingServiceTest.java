package com.tourflow.service;

import com.tourflow.dto.BookingDTO;
import com.tourflow.exception.ResourceNotFoundException;
import com.tourflow.exception.TourNotAvailableException;
import com.tourflow.model.*;
import com.tourflow.repository.BookingRepository;
import com.tourflow.repository.TourRepository;
import com.tourflow.repository.UserRepository;
import com.tourflow.service.base.ServiceTestBase;
import org.junit.jupiter.api.BeforeEach;
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
public class BookingServiceTest extends ServiceTestBase {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private TourRepository tourRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookingService bookingService;

    private Booking mockBooking;
    private Tour mockTour;
    private User mockUser;

    @BeforeEach
    public void setUp() {
        super.setUp();

        // Setup mock tour
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

        // Setup mock user
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setFirstName("John");
        mockUser.setLastName("Doe");
        mockUser.setEmail("john.doe@example.com");
        mockUser.setRole(Role.USER);
        mockUser.setCreatedAt(LocalDateTime.now());
        mockUser.setUpdatedAt(LocalDateTime.now());

        // Setup mock booking
        mockBooking = new Booking();
        mockBooking.setId(1L);
        mockBooking.setUser(mockUser);
        mockBooking.setTour(mockTour);
        mockBooking.setParticipants(2);
        mockBooking.setTotalPrice(new BigDecimal("199.98"));
        mockBooking.setStatus(BookingStatus.PENDING);
        mockBooking.setBookingDate(LocalDateTime.now());
        mockBooking.setCreatedAt(LocalDateTime.now());
        mockBooking.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    public void testGetBookingById_Success() {
        // Given
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(mockBooking));

        // When
        Booking result = bookingService.getBookingById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(BookingStatus.PENDING, result.getStatus());
        verify(bookingRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetBookingById_NotFound() {
        // Given
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> bookingService.getBookingById(1L));
        verify(bookingRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetBookingsByUser_Success() {
        // Given
        List<Booking> bookings = Arrays.asList(mockBooking);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Booking> page = new PageImpl<>(bookings, pageable, 1);

        when(bookingRepository.findByUserId(anyLong(), any(Pageable.class))).thenReturn(page);

        // When
        Page<Booking> result = bookingService.getBookingsByUser(1L, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1L, result.getContent().get(0).getId());
        verify(bookingRepository, times(1)).findByUserId(1L, pageable);
    }

    @Test
    public void testGetBookingsByTour_Success() {
        // Given
        List<Booking> bookings = Arrays.asList(mockBooking);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Booking> page = new PageImpl<>(bookings, pageable, 1);

        when(bookingRepository.findByTourId(anyLong(), any(Pageable.class))).thenReturn(page);

        // When
        Page<Booking> result = bookingService.getBookingsByTour(1L, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1L, result.getContent().get(0).getId());
        verify(bookingRepository, times(1)).findByTourId(1L, pageable);
    }

    @Test
    public void testCreateBooking_Success() {
        // Given
        BookingDTO bookingDTO = new BookingDTO();
        bookingDTO.setUserId(1L);
        bookingDTO.setTourId(1L);
        bookingDTO.setParticipants(2);
        bookingDTO.setBookingDate(LocalDateTime.now().plusDays(7));

        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(tourRepository.findById(1L)).thenReturn(Optional.of(mockTour));
        when(tourRepository.isAvailable(anyLong(), any(), anyInt())).thenReturn(true);
        when(bookingRepository.save(any(Booking.class))).thenReturn(mockBooking);

        // When
        Booking result = bookingService.createBooking(bookingDTO);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(userRepository, times(1)).findById(1L);
        verify(tourRepository, times(1)).findById(1L);
        verify(tourRepository, times(1)).isAvailable(anyLong(), any(), anyInt());
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    public void testCreateBooking_TourNotAvailable() {
        // Given
        BookingDTO bookingDTO = new BookingDTO();
        bookingDTO.setUserId(1L);
        bookingDTO.setTourId(1L);
        bookingDTO.setParticipants(2);
        bookingDTO.setBookingDate(LocalDateTime.now().plusDays(7));

        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(tourRepository.findById(1L)).thenReturn(Optional.of(mockTour));
        when(tourRepository.isAvailable(anyLong(), any(), anyInt())).thenReturn(false);

        // When & Then
        assertThrows(TourNotAvailableException.class, () -> bookingService.createBooking(bookingDTO));
        verify(userRepository, times(1)).findById(1L);
        verify(tourRepository, times(1)).findById(1L);
        verify(tourRepository, times(1)).isAvailable(anyLong(), any(), anyInt());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    public void testUpdateBookingStatus_Success() {
        // Given
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(mockBooking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(mockBooking);

        // When
        Booking result = bookingService.updateBookingStatus(1L, BookingStatus.CONFIRMED);

        // Then
        assertNotNull(result);
        assertEquals(BookingStatus.CONFIRMED, result.getStatus());
        verify(bookingRepository, times(1)).findById(1L);
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    public void testUpdateBookingStatus_NotFound() {
        // Given
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> bookingService.updateBookingStatus(1L, BookingStatus.CONFIRMED));
        verify(bookingRepository, times(1)).findById(1L);
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    public void testCancelBooking_Success() {
        // Given
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(mockBooking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(mockBooking);

        // When
        Booking result = bookingService.cancelBooking(1L);

        // Then
        assertNotNull(result);
        assertEquals(BookingStatus.CANCELLED, result.getStatus());
        verify(bookingRepository, times(1)).findById(1L);
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    public void testCancelBooking_NotFound() {
        // Given
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> bookingService.cancelBooking(1L));
        verify(bookingRepository, times(1)).findById(1L);
        verify(bookingRepository, never()).save(any(Booking.class));
    }
}
