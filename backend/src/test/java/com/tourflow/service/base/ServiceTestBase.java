package com.tourflow.service.base;

import com.tourflow.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public abstract class ServiceTestBase {

    @Mock
    protected UserRepository userRepository;

    @Mock
    protected TourRepository tourRepository;

    @Mock
    protected BookingRepository bookingRepository;

    @Mock
    protected ReviewRepository reviewRepository;

    @Mock
    protected PaymentRepository paymentRepository;

    @BeforeEach
    public void setUp() {
        // Configuration commune pour tous les tests de services
        // Peut être surchargée dans les classes enfants
    }
}
