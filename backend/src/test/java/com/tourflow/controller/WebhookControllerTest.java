package com.tourflow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tourflow.controller.base.ControllerTestBase;
import com.tourflow.model.*;
import com.tourflow.service.PaymentService;
import com.tourflow.service.StripeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class WebhookControllerTest extends ControllerTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PaymentService paymentService;

    @MockBean
    private StripeService stripeService;

    private Payment mockPayment;
    private Booking mockBooking;

    @Override
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
    public void testStripePaymentSucceededWebhook_Success() throws Exception {
        // Given
        String payload = "{ "type": "payment_intent.succeeded", "data": { "object": { "id": "pi_test123", "metadata": { "bookingId": "1" } } } }";
        String signature = "test_signature";

        when(stripeService.verifyWebhookSignature(anyString(), anyString())).thenReturn(true);
        when(paymentService.confirmPayment("pi_test123")).thenReturn(mockPayment);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post(API_PREFIX + "/webhooks/stripe")
                .header("Stripe-Signature", signature)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isOk());

        verify(stripeService, times(1)).verifyWebhookSignature(anyString(), anyString());
        verify(paymentService, times(1)).confirmPayment("pi_test123");
    }

    @Test
    public void testStripePaymentFailedWebhook_Success() throws Exception {
        // Given
        String payload = "{ "type": "payment_intent.payment_failed", "data": { "object": { "id": "pi_test123", "metadata": { "bookingId": "1" } } } }";
        String signature = "test_signature";

        when(stripeService.verifyWebhookSignature(anyString(), anyString())).thenReturn(true);
        when(paymentService.getPaymentByStripeId("pi_test123")).thenReturn(mockPayment);
        when(paymentService.updatePaymentStatus(1L, PaymentStatus.FAILED)).thenReturn(mockPayment);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post(API_PREFIX + "/webhooks/stripe")
                .header("Stripe-Signature", signature)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isOk());

        verify(stripeService, times(1)).verifyWebhookSignature(anyString(), anyString());
        verify(paymentService, times(1)).getPaymentByStripeId("pi_test123");
        verify(paymentService, times(1)).updatePaymentStatus(1L, PaymentStatus.FAILED);
    }

    @Test
    public void testStripeWebhook_InvalidSignature() throws Exception {
        // Given
        String payload = "{ "type": "payment_intent.succeeded", "data": { "object": { "id": "pi_test123" } } }";
        String signature = "invalid_signature";

        when(stripeService.verifyWebhookSignature(anyString(), anyString())).thenReturn(false);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post(API_PREFIX + "/webhooks/stripe")
                .header("Stripe-Signature", signature)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isUnauthorized());

        verify(stripeService, times(1)).verifyWebhookSignature(anyString(), anyString());
        verify(paymentService, never()).confirmPayment(anyString());
    }

    @Test
    public void testStripeWebhook_MissingSignature() throws Exception {
        // Given
        String payload = "{ "type": "payment_intent.succeeded", "data": { "object": { "id": "pi_test123" } } }";
        // Missing signature header

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post(API_PREFIX + "/webhooks/stripe")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isBadRequest());

        verify(stripeService, never()).verifyWebhookSignature(anyString(), anyString());
        verify(paymentService, never()).confirmPayment(anyString());
    }

    @Test
    public void testStripeWebhook_InvalidPayload() throws Exception {
        // Given
        String payload = "{ invalid json }";
        String signature = "test_signature";

        when(stripeService.verifyWebhookSignature(anyString(), anyString())).thenReturn(true);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post(API_PREFIX + "/webhooks/stripe")
                .header("Stripe-Signature", signature)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isBadRequest());

        verify(stripeService, times(1)).verifyWebhookSignature(anyString(), anyString());
        verify(paymentService, never()).confirmPayment(anyString());
    }

    @Test
    public void testStripeWebhook_UnknownEventType() throws Exception {
        // Given
        String payload = "{ "type": "unknown.event", "data": { "object": { "id": "pi_test123" } } }";
        String signature = "test_signature";

        when(stripeService.verifyWebhookSignature(anyString(), anyString())).thenReturn(true);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post(API_PREFIX + "/webhooks/stripe")
                .header("Stripe-Signature", signature)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isOk());

        verify(stripeService, times(1)).verifyWebhookSignature(anyString(), anyString());
        verify(paymentService, never()).confirmPayment(anyString());
    }

    @Test
    public void testEmailNotificationWebhook_Success() throws Exception {
        // Given
        Map<String, Object> payload = new HashMap<>();
        payload.put("type", "booking.confirmed");
        payload.put("data", Map.of(
            "bookingId", "1",
            "userId", "1",
            "tourId", "1"
        ));

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post(API_PREFIX + "/webhooks/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(payload)))
                .andExpect(status().isOk());
    }

    @Test
    public void testEmailNotificationWebhook_InvalidPayload() throws Exception {
        // Given
        Map<String, Object> payload = new HashMap<>();
        payload.put("type", "booking.confirmed");
        // Missing required data fields

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post(API_PREFIX + "/webhooks/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(payload)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testEmailNotificationWebhook_UnknownType() throws Exception {
        // Given
        Map<String, Object> payload = new HashMap<>();
        payload.put("type", "unknown.type");
        payload.put("data", Map.of(
            "bookingId", "1",
            "userId", "1",
            "tourId", "1"
        ));

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post(API_PREFIX + "/webhooks/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(payload)))
                .andExpect(status().isBadRequest());
    }
}
