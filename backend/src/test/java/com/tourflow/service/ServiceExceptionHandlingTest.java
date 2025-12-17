package com.tourflow.service;

import com.tourflow.exception.*;
import com.tourflow.model.*;
import com.tourflow.repository.*;
import com.tourflow.service.base.ServiceTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ServiceExceptionHandlingTest extends ServiceTestBase {

    @Mock
    private TourRepository tourRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private TourService tourService;

    @InjectMocks
    private UserService userService;

    @InjectMocks
    private BookingService bookingService;

    @InjectMocks
    private PaymentService paymentService;

    private Tour mockTour;
    private User mockUser;
    private Booking mockBooking;
    private Payment mockPayment;

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
        mockUser.setPassword("encodedPassword");
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

        // Setup mock payment
        mockPayment = new Payment();
        mockPayment.setId(1L);
        mockPayment.setBooking(mockBooking);
        mockPayment.setAmount(new BigDecimal("199.98"));
        mockPayment.setCurrency("EUR");
        mockPayment.setPaymentMethod(PaymentMethod.CARD);
        mockPayment.setStatus(PaymentStatus.PENDING);
        mockPayment.setStripePaymentId("pi_test123");
        mockPayment.setCreatedAt(LocalDateTime.now());
        mockPayment.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    public void testTourService_NullTourTitle() {
        // Given
        mockTour.setTitle(null);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> tourService.updateTour(1L, mockTour));
    }

    @Test
    public void testTourService_EmptyTourTitle() {
        // Given
        mockTour.setTitle("");

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> tourService.updateTour(1L, mockTour));
    }

    @Test
    public void testTourService_NegativeTourPrice() {
        // Given
        mockTour.setPrice(new BigDecimal("-10.00"));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> tourService.updateTour(1L, mockTour));
    }

    @Test
    public void testTourService_ZeroTourPrice() {
        // Given
        mockTour.setPrice(BigDecimal.ZERO);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> tourService.updateTour(1L, mockTour));
    }

    @Test
    public void testTourService_NegativeTourDuration() {
        // Given
        mockTour.setDuration(-1);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> tourService.updateTour(1L, mockTour));
    }

    @Test
    public void testTourService_ZeroTourDuration() {
        // Given
        mockTour.setDuration(0);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> tourService.updateTour(1L, mockTour));
    }

    @Test
    public void testTourService_NegativeMaxParticipants() {
        // Given
        mockTour.setMaxParticipants(-1);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> tourService.updateTour(1L, mockTour));
    }

    @Test
    public void testTourService_ZeroMaxParticipants() {
        // Given
        mockTour.setMaxParticipants(0);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> tourService.updateTour(1L, mockTour));
    }

    @Test
    public void testTourService_TourAlreadyStarted() {
        // Given
        mockTour.setStartDate(LocalDateTime.now().minusDays(1));
        when(tourRepository.findById(1L)).thenReturn(Optional.of(mockTour));

        // When & Then
        assertThrows(TourAlreadyStartedException.class, () -> tourService.cancelTour(1L));
    }

    @Test
    public void testUserService_NullUserEmail() {
        // Given
        mockUser.setEmail(null);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> userService.updateUser(1L, 
                com.tourflow.dto.UserDTO.fromEntity(mockUser)));
    }

    @Test
    public void testUserService_EmptyUserEmail() {
        // Given
        mockUser.setEmail("");

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> userService.updateUser(1L, 
                com.tourflow.dto.UserDTO.fromEntity(mockUser)));
    }

    @Test
    public void testUserService_InvalidUserEmail() {
        // Given
        mockUser.setEmail("invalid-email");

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> userService.updateUser(1L, 
                com.tourflow.dto.UserDTO.fromEntity(mockUser)));
    }

    @Test
    public void testUserService_NullUserPassword() {
        // Given
        mockUser.setPassword(null);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> userService.updateUser(1L, 
                com.tourflow.dto.UserDTO.fromEntity(mockUser)));
    }

    @Test
    public void testUserService_ShortUserPassword() {
        // Given
        mockUser.setPassword("123");

        // When & Then
        assertThrows(InvalidPasswordException.class, () -> userService.updateUser(1L, 
                com.tourflow.dto.UserDTO.fromEntity(mockUser)));
    }

    @Test
    public void testBookingService_ZeroParticipants() {
        // Given
        mockBooking.setParticipants(0);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> bookingService.updateBooking(1L, 
                com.tourflow.dto.BookingDTO.fromEntity(mockBooking)));
    }

    @Test
    public void testBookingService_NegativeParticipants() {
        // Given
        mockBooking.setParticipants(-1);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> bookingService.updateBooking(1L, 
                com.tourflow.dto.BookingDTO.fromEntity(mockBooking)));
    }

    @Test
    public void testBookingService_NullBookingDate() {
        // Given
        mockBooking.setBookingDate(null);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> bookingService.updateBooking(1L, 
                com.tourflow.dto.BookingDTO.fromEntity(mockBooking)));
    }

    @Test
    public void testBookingService_PastBookingDate() {
        // Given
        mockBooking.setBookingDate(LocalDateTime.now().minusDays(1));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> bookingService.updateBooking(1L, 
                com.tourflow.dto.BookingDTO.fromEntity(mockBooking)));
    }

    @Test
    public void testPaymentService_ZeroAmount() {
        // Given
        mockPayment.setAmount(BigDecimal.ZERO);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> paymentService.updatePayment(1L, 
                com.tourflow.dto.PaymentDTO.fromEntity(mockPayment)));
    }

    @Test
    public void testPaymentService_NegativeAmount() {
        // Given
        mockPayment.setAmount(new BigDecimal("-10.00"));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> paymentService.updatePayment(1L, 
                com.tourflow.dto.PaymentDTO.fromEntity(mockPayment)));
    }

    @Test
    public void testPaymentService_NullCurrency() {
        // Given
        mockPayment.setCurrency(null);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> paymentService.updatePayment(1L, 
                com.tourflow.dto.PaymentDTO.fromEntity(mockPayment)));
    }

    @Test
    public void testPaymentService_EmptyCurrency() {
        // Given
        mockPayment.setCurrency("");

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> paymentService.updatePayment(1L, 
                com.tourflow.dto.PaymentDTO.fromEntity(mockPayment)));
    }

    @Test
    public void testPaymentService_NullPaymentMethod() {
        // Given
        mockPayment.setPaymentMethod(null);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> paymentService.updatePayment(1L, 
                com.tourflow.dto.PaymentDTO.fromEntity(mockPayment)));
    }
}
