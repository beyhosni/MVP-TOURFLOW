package com.tourflow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tourflow.controller.base.ControllerTestBase;
import com.tourflow.dto.TourDTO;
import com.tourflow.model.Tour;
import com.tourflow.model.TourStatus;
import com.tourflow.service.TourService;
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

public class TourControllerTest extends ControllerTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TourService tourService;

    private Tour mockTour;

    @Override
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
    @WithMockUser(roles = {"USER"})
    public void testGetTourById_Success() throws Exception {
        // Given
        when(tourService.getTourById(1L)).thenReturn(mockTour);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get(API_PREFIX + "/tours/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Tour de Paris"));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void testGetAllTours_Success() throws Exception {
        // Given
        List<Tour> tours = Arrays.asList(mockTour);
        Page<Tour> page = new PageImpl<>(tours, PageRequest.of(0, 10), 1);

        when(tourService.getAllTours(any())).thenReturn(page);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get(API_PREFIX + "/tours"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Tour de Paris"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void testCreateTour_Success() throws Exception {
        // Given
        TourDTO tourDTO = new TourDTO();
        tourDTO.setTitle("Tour de Lyon");
        tourDTO.setDescription("Découvrez Lyon");
        tourDTO.setPrice(new BigDecimal("79.99"));
        tourDTO.setDuration(2);

        when(tourService.createTour(any(TourDTO.class))).thenReturn(mockTour);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post(API_PREFIX + "/tours")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(tourDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void testUpdateTour_Success() throws Exception {
        // Given
        TourDTO tourDTO = new TourDTO();
        tourDTO.setTitle("Tour de Marseille");
        tourDTO.setDescription("Découvrez Marseille");
        tourDTO.setPrice(new BigDecimal("89.99"));
        tourDTO.setDuration(4);

        when(tourService.updateTour(anyLong(), any(TourDTO.class))).thenReturn(mockTour);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.put(API_PREFIX + "/tours/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(tourDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void testDeleteTour_Success() throws Exception {
        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.delete(API_PREFIX + "/tours/1"))
                .andExpect(status().isNoContent());
    }
}
