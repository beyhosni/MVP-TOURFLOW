package com.tourflow.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tourflow.dto.LoginRequest;
import com.tourflow.dto.UserDTO;
import com.tourflow.model.Role;
import com.tourflow.model.User;
import com.tourflow.repository.UserRepository;
import com.tourflow.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JwtService jwtService;

    private User testUser;

    @BeforeEach
    public void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setEmail("john.doe@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setRole(Role.USER);
    }

    @Test
    public void testLoginSuccess() throws Exception {
        // Given
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("john.doe@example.com");
        loginRequest.setPassword("password123");

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                testUser.getEmail(), testUser.getPassword());

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userRepository.findByEmail("john.doe@example.com"))
                .thenReturn(Optional.of(testUser));
        when(jwtService.generateToken(testUser))
                .thenReturn("test-jwt-token");

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").value("test-jwt-token"))
                .andExpect(jsonPath("$.type").value("Bearer"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    public void testLoginFailure() throws Exception {
        // Given
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("john.doe@example.com");
        loginRequest.setPassword("wrongPassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    public void testRegisterSuccess() throws Exception {
        // Given
        UserDTO userDTO = new UserDTO();
        userDTO.setFirstName("Jane");
        userDTO.setLastName("Smith");
        userDTO.setEmail("jane.smith@example.com");
        userDTO.setPassword("password123");
        userDTO.setRole(Role.USER);

        when(userRepository.existsByEmail("jane.smith@example.com"))
                .thenReturn(false);
        when(passwordEncoder.encode("password123"))
                .thenReturn("encodedPassword");
        when(userRepository.save(any(User.class)))
                .thenReturn(testUser);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    @Test
    public void testRegisterEmailAlreadyExists() throws Exception {
        // Given
        UserDTO userDTO = new UserDTO();
        userDTO.setFirstName("Jane");
        userDTO.setLastName("Smith");
        userDTO.setEmail("john.doe@example.com"); // Email déjà existant
        userDTO.setPassword("password123");
        userDTO.setRole(Role.USER);

        when(userRepository.existsByEmail("john.doe@example.com"))
                .thenReturn(true);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    public void testRegisterInvalidEmail() throws Exception {
        // Given
        UserDTO userDTO = new UserDTO();
        userDTO.setFirstName("Jane");
        userDTO.setLastName("Smith");
        userDTO.setEmail("invalid-email"); // Email invalide
        userDTO.setPassword("password123");
        userDTO.setRole(Role.USER);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"));
    }

    @Test
    public void testRegisterWeakPassword() throws Exception {
        // Given
        UserDTO userDTO = new UserDTO();
        userDTO.setFirstName("Jane");
        userDTO.setLastName("Smith");
        userDTO.setEmail("jane.smith@example.com");
        userDTO.setPassword("123"); // Mot de passe trop court
        userDTO.setRole(Role.USER);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"));
    }

    @Test
    public void testJwtTokenValidation() throws Exception {
        // Given
        String validToken = "Bearer valid-jwt-token";

        when(jwtService.extractUsername("valid-jwt-token"))
                .thenReturn("john.doe@example.com");
        when(userRepository.findByEmail("john.doe@example.com"))
                .thenReturn(Optional.of(testUser));
        when(jwtService.isTokenValid("valid-jwt-token", testUser))
                .thenReturn(true);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/users/1")
                .header("Authorization", validToken))
                .andExpect(status().isOk());
    }

    @Test
    public void testJwtTokenInvalid() throws Exception {
        // Given
        String invalidToken = "Bearer invalid-jwt-token";

        when(jwtService.extractUsername("invalid-jwt-token"))
                .thenReturn("john.doe@example.com");
        when(userRepository.findByEmail("john.doe@example.com"))
                .thenReturn(Optional.of(testUser));
        when(jwtService.isTokenValid("invalid-jwt-token", testUser))
                .thenReturn(false);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/users/1")
                .header("Authorization", invalidToken))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testJwtTokenMissing() throws Exception {
        // When & Then - Pas de token d'authentification
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/users/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testJwtTokenMalformed() throws Exception {
        // Given
        String malformedToken = "invalid-format-token";

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/users/1")
                .header("Authorization", malformedToken))
                .andExpect(status().isUnauthorized());
    }
}
