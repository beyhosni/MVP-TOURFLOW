package com.tourflow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tourflow.controller.base.ControllerTestBase;
import com.tourflow.dto.PaymentDTO;
import com.tourflow.model.*;
import com.tourflow.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class PaymentControllerTest extends ControllerTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PaymentService paymentService;

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
    @WithMockUser(roles = {"USER"})
    public void testGetPaymentById_Success() throws Exception {
        // Given
        when(paymentService.getPaymentById(1L)).thenReturn(mockPayment);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get(API_PREFIX + "/payments/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void testGetPaymentsByBooking_Success() throws Exception {
        // Given
        List<Payment> payments = Arrays.asList(mockPayment);
        Page<Payment> page = new PageImpl<>(payments, PageRequest.of(0, 10), 1);

        when(paymentService.getPaymentsByBooking(anyLong(), any())).thenReturn(page);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get(API_PREFIX + "/payments/booking/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].status").value("PENDING"));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void testCreatePaymentIntent_Success() throws Exception {
        // Given
        PaymentDTO paymentDTO = new PaymentDTO();
        paymentDTO.setBookingId(1L);
        paymentDTO.setAmount(new BigDecimal("199.98"));
        paymentDTO.setCurrency("EUR");
        paymentDTO.setPaymentMethod(PaymentMethod.CARD);

        Map<String, Object> response = new HashMap<>();
        response.put("clientSecret", "pi_test123_secret_test456");

        when(paymentService.createPaymentIntent(any(PaymentDTO.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post(API_PREFIX + "/payments/create-intent")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(paymentDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.clientSecret").value("pi_test123_secret_test456"));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void testConfirmPayment_Success() throws Exception {
        // Given
        when(paymentService.confirmPayment(anyString())).thenReturn(mockPayment);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post(API_PREFIX + "/payments/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString("pi_test123")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void testCancelPayment_Success() throws Exception {
        // Given
        when(paymentService.cancelPayment(anyLong())).thenReturn(mockPayment);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.put(API_PREFIX + "/payments/1/cancel"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void testGetPaymentById_AccessDenied() throws Exception {
        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get(API_PREFIX + "/payments/2"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void testGetPaymentsByBooking_AccessDenied() throws Exception {
        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get(API_PREFIX + "/payments/booking/2"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testGetPaymentById_Unauthorized() throws Exception {
        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get(API_PREFIX + "/payments/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void testCreatePaymentIntent_ValidationError() throws Exception {
        // Given
        PaymentDTO paymentDTO = new PaymentDTO();
        paymentDTO.setBookingId(1L);
        // Missing required fields

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post(API_PREFIX + "/payments/create-intent")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(paymentDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void testConfirmPayment_ValidationError() throws Exception {
        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post(API_PREFIX + "/payments/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString("")))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void testStripeWebhook_Success() throws Exception {
        // Given
        String payload = "{ "type": "payment_intent.succeeded", "data": { "object": { "id": "pi_test123" } } }";
        String signature = "test_signature";

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post(API_PREFIX + "/payments/webhook")
                .header("Stripe-Signature", signature)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isOk());
    }

    @Test
    public void testStripeWebhook_Unauthorized() throws Exception {
        // Given
        String payload = "{ "type": "payment_intent.succeeded", "data": { "object": { "id": "pi_test123" } } }";
        // Missing signature header

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post(API_PREFIX + "/payments/webhook")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isUnauthorized());
    }
}
