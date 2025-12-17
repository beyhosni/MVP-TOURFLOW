package com.tourflow.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class ConfigurationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void testApplicationContext() {
        assertNotNull(applicationContext);
    }

    @Test
    public void testSecurityConfiguration() {
        assertNotNull(applicationContext.getBean("securityFilterChain"));
    }

    @Test
    public void testJwtConfiguration() {
        assertNotNull(applicationContext.getBean("jwtService"));
    }

    @Test
    public void testDatabaseConfiguration() {
        assertNotNull(applicationContext.getBean("entityManagerFactory"));
    }

    @Test
    public void testSwaggerConfiguration() {
        assertNotNull(applicationContext.getBean("openAPI"));
    }

    @Test
    public void testCorsConfiguration() {
        assertNotNull(applicationContext.getBean("corsConfigurationSource"));
    }

    @Test
    public void testPasswordEncoder() {
        assertNotNull(applicationContext.getBean("passwordEncoder"));
    }

    @Test
    public void testAuthenticationManager() {
        assertNotNull(applicationContext.getBean("authenticationManager"));
    }
}
