package com.tourflow.validation;

import com.tourflow.dto.TourDTO;
import com.tourflow.dto.UserDTO;
import com.tourflow.model.PaymentMethod;
import com.tourflow.model.TourStatus;
import com.tourflow.model.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ValidationTest {

    @Autowired
    private Validator validator;

    private TourDTO tourDTO;
    private UserDTO userDTO;

    @BeforeEach
    public void setUp() {
        // Initialiser les DTO de test
        tourDTO = new TourDTO();
        tourDTO.setTitle("Test Tour");
        tourDTO.setDescription("A test tour for validation testing");
        tourDTO.setPrice(new BigDecimal("99.99"));
        tourDTO.setDuration(3);
        tourDTO.setMaxParticipants(20);
        tourDTO.setStatus(TourStatus.ACTIVE);
        tourDTO.setStartDate(LocalDateTime.now().plusDays(7));
        tourDTO.setEndDate(LocalDateTime.now().plusDays(10));

        userDTO = new UserDTO();
        userDTO.setFirstName("Test");
        userDTO.setLastName("User");
        userDTO.setEmail("test.user@example.com");
        userDTO.setPassword("password123");
        userDTO.setRole(UserRole.CUSTOMER);
    }

    @Test
    public void testValidTourDTO() {
        // Given
        Errors errors = new BeanPropertyBindingResult(tourDTO, "tourDTO");

        // When
        validator.validate(tourDTO, errors);

        // Then
        assertFalse(errors.hasErrors());
    }

    @Test
    public void testInvalidTourDTOMissingTitle() {
        // Given
        tourDTO.setTitle(null);
        Errors errors = new BeanPropertyBindingResult(tourDTO, "tourDTO");

        // When
        validator.validate(tourDTO, errors);

        // Then
        assertTrue(errors.hasErrors());
        assertTrue(errors.hasFieldErrors("title"));
        assertEquals("Le titre du tour est obligatoire", errors.getFieldError("title").getDefaultMessage());
    }

    @Test
    public void testInvalidTourDTOEmptyTitle() {
        // Given
        tourDTO.setTitle("");
        Errors errors = new BeanPropertyBindingResult(tourDTO, "tourDTO");

        // When
        validator.validate(tourDTO, errors);

        // Then
        assertTrue(errors.hasErrors());
        assertTrue(errors.hasFieldErrors("title"));
        assertEquals("Le titre du tour est obligatoire", errors.getFieldError("title").getDefaultMessage());
    }

    @Test
    public void testInvalidTourDTOTitleTooLong() {
        // Given
        tourDTO.setTitle("a".repeat(101)); // 101 caractères
        Errors errors = new BeanPropertyBindingResult(tourDTO, "tourDTO");

        // When
        validator.validate(tourDTO, errors);

        // Then
        assertTrue(errors.hasErrors());
        assertTrue(errors.hasFieldErrors("title"));
        assertEquals("Le titre du tour ne doit pas dépasser 100 caractères", errors.getFieldError("title").getDefaultMessage());
    }

    @Test
    public void testInvalidTourDTONegativePrice() {
        // Given
        tourDTO.setPrice(new BigDecimal("-10.00"));
        Errors errors = new BeanPropertyBindingResult(tourDTO, "tourDTO");

        // When
        validator.validate(tourDTO, errors);

        // Then
        assertTrue(errors.hasErrors());
        assertTrue(errors.hasFieldErrors("price"));
        assertEquals("Le prix du tour doit être positif", errors.getFieldError("price").getDefaultMessage());
    }

    @Test
    public void testInvalidTourDTOZeroPrice() {
        // Given
        tourDTO.setPrice(BigDecimal.ZERO);
        Errors errors = new BeanPropertyBindingResult(tourDTO, "tourDTO");

        // When
        validator.validate(tourDTO, errors);

        // Then
        assertTrue(errors.hasErrors());
        assertTrue(errors.hasFieldErrors("price"));
        assertEquals("Le prix du tour doit être positif", errors.getFieldError("price").getDefaultMessage());
    }

    @Test
    public void testInvalidTourDTONegativeDuration() {
        // Given
        tourDTO.setDuration(-1);
        Errors errors = new BeanPropertyBindingResult(tourDTO, "tourDTO");

        // When
        validator.validate(tourDTO, errors);

        // Then
        assertTrue(errors.hasErrors());
        assertTrue(errors.hasFieldErrors("duration"));
        assertEquals("La durée du tour doit être positive", errors.getFieldError("duration").getDefaultMessage());
    }

    @Test
    public void testInvalidTourDTOZeroDuration() {
        // Given
        tourDTO.setDuration(0);
        Errors errors = new BeanPropertyBindingResult(tourDTO, "tourDTO");

        // When
        validator.validate(tourDTO, errors);

        // Then
        assertTrue(errors.hasErrors());
        assertTrue(errors.hasFieldErrors("duration"));
        assertEquals("La durée du tour doit être positive", errors.getFieldError("duration").getDefaultMessage());
    }

    @Test
    public void testInvalidTourDTOPastStartDate() {
        // Given
        tourDTO.setStartDate(LocalDateTime.now().minusDays(1));
        Errors errors = new BeanPropertyBindingResult(tourDTO, "tourDTO");

        // When
        validator.validate(tourDTO, errors);

        // Then
        assertTrue(errors.hasErrors());
        assertTrue(errors.hasFieldErrors("startDate"));
        assertEquals("La date de début doit être dans le futur", errors.getFieldError("startDate").getDefaultMessage());
    }

    @Test
    public void testInvalidTourDTOEndDateBeforeStartDate() {
        // Given
        tourDTO.setStartDate(LocalDateTime.now().plusDays(10));
        tourDTO.setEndDate(LocalDateTime.now().plusDays(5));
        Errors errors = new BeanPropertyBindingResult(tourDTO, "tourDTO");

        // When
        validator.validate(tourDTO, errors);

        // Then
        assertTrue(errors.hasErrors());
        assertTrue(errors.hasFieldErrors("endDate"));
        assertEquals("La date de fin doit être après la date de début", errors.getFieldError("endDate").getDefaultMessage());
    }

    @Test
    public void testValidUserDTO() {
        // Given
        Errors errors = new BeanPropertyBindingResult(userDTO, "userDTO");

        // When
        validator.validate(userDTO, errors);

        // Then
        assertFalse(errors.hasErrors());
    }

    @Test
    public void testInvalidUserDTOMissingFirstName() {
        // Given
        userDTO.setFirstName(null);
        Errors errors = new BeanPropertyBindingResult(userDTO, "userDTO");

        // When
        validator.validate(userDTO, errors);

        // Then
        assertTrue(errors.hasErrors());
        assertTrue(errors.hasFieldErrors("firstName"));
        assertEquals("Le prénom est obligatoire", errors.getFieldError("firstName").getDefaultMessage());
    }

    @Test
    public void testInvalidUserDTOMissingLastName() {
        // Given
        userDTO.setLastName(null);
        Errors errors = new BeanPropertyBindingResult(userDTO, "userDTO");

        // When
        validator.validate(userDTO, errors);

        // Then
        assertTrue(errors.hasErrors());
        assertTrue(errors.hasFieldErrors("lastName"));
        assertEquals("Le nom de famille est obligatoire", errors.getFieldError("lastName").getDefaultMessage());
    }

    @Test
    public void testInvalidUserDTOMissingEmail() {
        // Given
        userDTO.setEmail(null);
        Errors errors = new BeanPropertyBindingResult(userDTO, "userDTO");

        // When
        validator.validate(userDTO, errors);

        // Then
        assertTrue(errors.hasErrors());
        assertTrue(errors.hasFieldErrors("email"));
        assertEquals("L'email est obligatoire", errors.getFieldError("email").getDefaultMessage());
    }

    @Test
    public void testInvalidUserDTOInvalidEmail() {
        // Given
        userDTO.setEmail("invalid-email");
        Errors errors = new BeanPropertyBindingResult(userDTO, "userDTO");

        // When
        validator.validate(userDTO, errors);

        // Then
        assertTrue(errors.hasErrors());
        assertTrue(errors.hasFieldErrors("email"));
        assertEquals("L'email doit être valide", errors.getFieldError("email").getDefaultMessage());
    }

    @Test
    public void testInvalidUserDTOMissingPassword() {
        // Given
        userDTO.setPassword(null);
        Errors errors = new BeanPropertyBindingResult(userDTO, "userDTO");

        // When
        validator.validate(userDTO, errors);

        // Then
        assertTrue(errors.hasErrors());
        assertTrue(errors.hasFieldErrors("password"));
        assertEquals("Le mot de passe est obligatoire", errors.getFieldError("password").getDefaultMessage());
    }

    @Test
    public void testInvalidUserDTOShortPassword() {
        // Given
        userDTO.setPassword("123");
        Errors errors = new BeanPropertyBindingResult(userDTO, "userDTO");

        // When
        validator.validate(userDTO, errors);

        // Then
        assertTrue(errors.hasErrors());
        assertTrue(errors.hasFieldErrors("password"));
        assertEquals("Le mot de passe doit contenir au moins 8 caractères", errors.getFieldError("password").getDefaultMessage());
    }
}
