package com.tourflow.service;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.tourflow.dto.PaymentDTO;
import com.tourflow.exception.PaymentException;
import com.tourflow.exception.ResourceNotFoundException;
import com.tourflow.model.*;
import com.tourflow.repository.BookingRepository;
import com.tourflow.repository.PaymentRepository;
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
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest extends ServiceTestBase {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private StripeService stripeService;

    @InjectMocks
    private PaymentService paymentService;

    private Payment mockPayment;
    private Booking mockBooking;

    @BeforeEach
    public void setUp() {
        super.setUp();

        // Setup mock booking
        mockBooking = new Booking();
        mockBooking.setId(1L);
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
    public void testGetPaymentById_Success() {
        // Given
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(mockPayment));

        // When
        Payment result = paymentService.getPaymentById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(PaymentStatus.PENDING, result.getStatus());
        verify(paymentRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetPaymentById_NotFound() {
        // Given
        when(paymentRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> paymentService.getPaymentById(1L));
        verify(paymentRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetPaymentsByBooking_Success() {
        // Given
        List<Payment> payments = Arrays.asList(mockPayment);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Payment> page = new PageImpl<>(payments, pageable, 1);

        when(paymentRepository.findByBookingId(anyLong(), any(Pageable.class))).thenReturn(page);

        // When
        Page<Payment> result = paymentService.getPaymentsByBooking(1L, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1L, result.getContent().get(0).getId());
        verify(paymentRepository, times(1)).findByBookingId(1L, pageable);
    }

    @Test
    public void testCreatePaymentIntent_Success() throws StripeException {
        // Given
        PaymentDTO paymentDTO = new PaymentDTO();
        paymentDTO.setBookingId(1L);
        paymentDTO.setAmount(new BigDecimal("199.98"));
        paymentDTO.setCurrency("EUR");
        paymentDTO.setPaymentMethod(PaymentMethod.CARD);

        PaymentIntent mockPaymentIntent = new PaymentIntent();
        mockPaymentIntent.setId("pi_test123");
        mockPaymentIntent.setAmount(19998L);
        mockPaymentIntent.setCurrency("eur");
        mockPaymentIntent.setStatus("requires_payment_method");

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(mockBooking));
        when(stripeService.createPaymentIntent(any(BigDecimal.class), anyString())).thenReturn(mockPaymentIntent);
        when(paymentRepository.save(any(Payment.class))).thenReturn(mockPayment);

        // When
        Map<String, Object> result = paymentService.createPaymentIntent(paymentDTO);

        // Then
        assertNotNull(result);
        assertTrue(result.containsKey("clientSecret"));
        verify(bookingRepository, times(1)).findById(1L);
        verify(stripeService, times(1)).createPaymentIntent(any(BigDecimal.class), anyString());
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    public void testCreatePaymentIntent_BookingNotFound() {
        // Given
        PaymentDTO paymentDTO = new PaymentDTO();
        paymentDTO.setBookingId(1L);
        paymentDTO.setAmount(new BigDecimal("199.98"));
        paymentDTO.setCurrency("EUR");
        paymentDTO.setPaymentMethod(PaymentMethod.CARD);

        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> paymentService.createPaymentIntent(paymentDTO));
        verify(bookingRepository, times(1)).findById(1L);
        verify(stripeService, never()).createPaymentIntent(any(BigDecimal.class), anyString());
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    public void testCreatePaymentIntent_StripeError() throws StripeException {
        // Given
        PaymentDTO paymentDTO = new PaymentDTO();
        paymentDTO.setBookingId(1L);
        paymentDTO.setAmount(new BigDecimal("199.98"));
        paymentDTO.setCurrency("EUR");
        paymentDTO.setPaymentMethod(PaymentMethod.CARD);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(mockBooking));
        when(stripeService.createPaymentIntent(any(BigDecimal.class), anyString()))
                .thenThrow(new StripeException("Stripe API error") {});

        // When & Then
        assertThrows(PaymentException.class, () -> paymentService.createPaymentIntent(paymentDTO));
        verify(bookingRepository, times(1)).findById(1L);
        verify(stripeService, times(1)).createPaymentIntent(any(BigDecimal.class), anyString());
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    public void testConfirmPayment_Success() throws StripeException {
        // Given
        String paymentIntentId = "pi_test123";

        PaymentIntent mockPaymentIntent = new PaymentIntent();
        mockPaymentIntent.setId(paymentIntentId);
        mockPaymentIntent.setStatus("succeeded");

        when(paymentRepository.findByStripePaymentId(paymentIntentId)).thenReturn(Optional.of(mockPayment));
        when(stripeService.retrievePaymentIntent(paymentIntentId)).thenReturn(mockPaymentIntent);
        when(paymentRepository.save(any(Payment.class))).thenReturn(mockPayment);

        // When
        Payment result = paymentService.confirmPayment(paymentIntentId);

        // Then
        assertNotNull(result);
        assertEquals(PaymentStatus.COMPLETED, result.getStatus());
        verify(paymentRepository, times(1)).findByStripePaymentId(paymentIntentId);
        verify(stripeService, times(1)).retrievePaymentIntent(paymentIntentId);
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    public void testConfirmPayment_PaymentNotFound() {
        // Given
        String paymentIntentId = "pi_test123";

        when(paymentRepository.findByStripePaymentId(paymentIntentId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> paymentService.confirmPayment(paymentIntentId));
        verify(paymentRepository, times(1)).findByStripePaymentId(paymentIntentId);
        verify(stripeService, never()).retrievePaymentIntent(anyString());
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    public void testConfirmPayment_StripeError() throws StripeException {
        // Given
        String paymentIntentId = "pi_test123";

        when(paymentRepository.findByStripePaymentId(paymentIntentId)).thenReturn(Optional.of(mockPayment));
        when(stripeService.retrievePaymentIntent(paymentIntentId))
                .thenThrow(new StripeException("Stripe API error") {});

        // When & Then
        assertThrows(PaymentException.class, () -> paymentService.confirmPayment(paymentIntentId));
        verify(paymentRepository, times(1)).findByStripePaymentId(paymentIntentId);
        verify(stripeService, times(1)).retrievePaymentIntent(paymentIntentId);
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    public void testCancelPayment_Success() throws StripeException {
        // Given
        String paymentIntentId = "pi_test123";

        PaymentIntent mockPaymentIntent = new PaymentIntent();
        mockPaymentIntent.setId(paymentIntentId);
        mockPaymentIntent.setStatus("canceled");

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(mockPayment));
        when(stripeService.cancelPaymentIntent(paymentIntentId)).thenReturn(mockPaymentIntent);
        when(paymentRepository.save(any(Payment.class))).thenReturn(mockPayment);

        // When
        Payment result = paymentService.cancelPayment(1L);

        // Then
        assertNotNull(result);
        assertEquals(PaymentStatus.CANCELLED, result.getStatus());
        verify(paymentRepository, times(1)).findById(1L);
        verify(stripeService, times(1)).cancelPaymentIntent(paymentIntentId);
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    public void testCancelPayment_PaymentNotFound() {
        // Given
        when(paymentRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> paymentService.cancelPayment(1L));
        verify(paymentRepository, times(1)).findById(1L);
        verify(stripeService, never()).cancelPaymentIntent(anyString());
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    public void testCancelPayment_StripeError() throws StripeException {
        // Given
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(mockPayment));
        when(stripeService.cancelPaymentIntent(mockPayment.getStripePaymentId()))
                .thenThrow(new StripeException("Stripe API error") {});

        // When & Then
        assertThrows(PaymentException.class, () -> paymentService.cancelPayment(1L));
        verify(paymentRepository, times(1)).findById(1L);
        verify(stripeService, times(1)).cancelPaymentIntent(mockPayment.getStripePaymentId());
        verify(paymentRepository, never()).save(any(Payment.class));
    }
}
