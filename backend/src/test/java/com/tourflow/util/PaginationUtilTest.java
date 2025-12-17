package com.tourflow.util;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PaginationUtilTest {

    @Test
    public void testToPaginatedResponse() {
        // Given
        List<String> items = Arrays.asList("Item 1", "Item 2", "Item 3");
        Pageable pageable = PageRequest.of(0, 10);
        Page<String> page = new PageImpl<>(items, pageable, 3);

        // When
        PaginatedResponse<String> response = PaginationUtil.toPaginatedResponse(page);

        // Then
        assertNotNull(response);
        assertEquals(3, response.getContent().size());
        assertEquals(0, response.getPageNumber());
        assertEquals(10, response.getPageSize());
        assertEquals(3, response.getTotalElements());
        assertEquals(1, response.getTotalPages());
        assertTrue(response.isFirst());
        assertTrue(response.isLast());
    }

    @Test
    public void testToPaginatedResponse_EmptyPage() {
        // Given
        List<String> items = Arrays.asList();
        Pageable pageable = PageRequest.of(0, 10);
        Page<String> page = new PageImpl<>(items, pageable, 0);

        // When
        PaginatedResponse<String> response = PaginationUtil.toPaginatedResponse(page);

        // Then
        assertNotNull(response);
        assertTrue(response.getContent().isEmpty());
        assertEquals(0, response.getPageNumber());
        assertEquals(10, response.getPageSize());
        assertEquals(0, response.getTotalElements());
        assertEquals(0, response.getTotalPages());
        assertTrue(response.isFirst());
        assertTrue(response.isLast());
    }

    @Test
    public void testToPaginatedResponse_MiddlePage() {
        // Given
        List<String> items = Arrays.asList("Item 11", "Item 12", "Item 13");
        Pageable pageable = PageRequest.of(1, 10);
        Page<String> page = new PageImpl<>(items, pageable, 30);

        // When
        PaginatedResponse<String> response = PaginationUtil.toPaginatedResponse(page);

        // Then
        assertNotNull(response);
        assertEquals(3, response.getContent().size());
        assertEquals(1, response.getPageNumber());
        assertEquals(10, response.getPageSize());
        assertEquals(30, response.getTotalElements());
        assertEquals(3, response.getTotalPages());
        assertFalse(response.isFirst());
        assertFalse(response.isLast());
    }

    @Test
    public void testCreateOptimizedQuery() {
        // Given
        String entityName = "Tour";
        String[] fetchJoins = {"reviews", "bookings"};

        // When
        String query = PaginationUtil.createOptimizedQuery(entityName, fetchJoins);

        // Then
        assertNotNull(query);
        assertTrue(query.contains("SELECT DISTINCT e FROM Tour e"));
        assertTrue(query.contains("LEFT JOIN FETCH e.reviews"));
        assertTrue(query.contains("LEFT JOIN FETCH e.bookings"));
    }

    @Test
    public void testCreateOptimizedQuery_NoFetchJoins() {
        // Given
        String entityName = "Tour";
        String[] fetchJoins = {};

        // When
        String query = PaginationUtil.createOptimizedQuery(entityName, fetchJoins);

        // Then
        assertNotNull(query);
        assertEquals("SELECT DISTINCT e FROM Tour e", query);
    }

    @Test
    public void testCreateCountQuery() {
        // Given
        String entityName = "Tour";

        // When
        String query = PaginationUtil.createCountQuery(entityName);

        // Then
        assertNotNull(query);
        assertEquals("SELECT COUNT(DISTINCT e) FROM Tour e", query);
    }
}
