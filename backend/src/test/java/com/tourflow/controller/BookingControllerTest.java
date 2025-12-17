package com.tourflow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tourflow.controller.base.ControllerTestBase;
import com.tourflow.dto.BookingDTO;
import com.tourflow.model.*;
import com.tourflow.service.BookingService;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class BookingControllerTest extends ControllerTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    private Booking mockBooking;
    private Tour mockTour;
    private User mockUser;

    @Override
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
    @WithMockUser(roles = {"USER"})
    public void testGetBookingById_Success() throws Exception {
        // Given
        when(bookingService.getBookingById(1L)).thenReturn(mockBooking);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get(API_PREFIX + "/bookings/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void testGetBookingsByUser_Success() throws Exception {
        // Given
        List<Booking> bookings = Arrays.asList(mockBooking);
        Page<Booking> page = new PageImpl<>(bookings, PageRequest.of(0, 10), 1);

        when(bookingService.getBookingsByUser(anyLong(), any())).thenReturn(page);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get(API_PREFIX + "/bookings/user/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].status").value("PENDING"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void testGetBookingsByTour_Success() throws Exception {
        // Given
        List<Booking> bookings = Arrays.asList(mockBooking);
        Page<Booking> page = new PageImpl<>(bookings, PageRequest.of(0, 10), 1);

        when(bookingService.getBookingsByTour(anyLong(), any())).thenReturn(page);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get(API_PREFIX + "/bookings/tour/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].status").value("PENDING"));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void testCreateBooking_Success() throws Exception {
        // Given
        BookingDTO bookingDTO = new BookingDTO();
        bookingDTO.setUserId(1L);
        bookingDTO.setTourId(1L);
        bookingDTO.setParticipants(2);
        bookingDTO.setBookingDate(LocalDateTime.now().plusDays(7));

        when(bookingService.createBooking(any(BookingDTO.class))).thenReturn(mockBooking);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post(API_PREFIX + "/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(bookingDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void testUpdateBookingStatus_Success() throws Exception {
        // Given
        when(bookingService.updateBookingStatus(anyLong(), any(BookingStatus.class))).thenReturn(mockBooking);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.put(API_PREFIX + "/bookings/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(BookingStatus.CONFIRMED)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void testCancelBooking_Success() throws Exception {
        // Given
        when(bookingService.cancelBooking(anyLong())).thenReturn(mockBooking);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.put(API_PREFIX + "/bookings/1/cancel"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void testGetBookingById_AccessDenied() throws Exception {
        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get(API_PREFIX + "/bookings/2"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void testUpdateBookingStatus_AccessDenied() throws Exception {
        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.put(API_PREFIX + "/bookings/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(BookingStatus.CONFIRMED)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void testGetBookingsByTour_AccessDenied() throws Exception {
        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get(API_PREFIX + "/bookings/tour/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testGetBookingById_Unauthorized() throws Exception {
        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get(API_PREFIX + "/bookings/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void testCreateBooking_ValidationError() throws Exception {
        // Given
        BookingDTO bookingDTO = new BookingDTO();
        bookingDTO.setUserId(1L);
        bookingDTO.setTourId(1L);
        // Missing required fields

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post(API_PREFIX + "/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(bookingDTO)))
                .andExpect(status().isBadRequest());
    }
}
