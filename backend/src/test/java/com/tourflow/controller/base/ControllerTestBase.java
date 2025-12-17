package com.tourflow.controller.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tourflow.config.SecurityConfig;
import com.tourflow.security.JwtAuthenticationFilter;
import com.tourflow.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(MockitoExtension.class)
@WebMvcTest
@Import({SecurityConfig.class, JwtTokenProvider.class})
@ActiveProfiles("test")
public abstract class ControllerTestBase {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    protected JwtAuthenticationFilter jwtAuthenticationFilter;

    protected static final String API_PREFIX = "/api";

    @BeforeEach
    public void setUp() {
        // Configuration commune pour tous les tests de contrôleurs
    }

    protected String asJsonString(final Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
