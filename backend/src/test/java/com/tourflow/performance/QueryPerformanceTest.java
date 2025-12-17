package com.tourflow.performance;

import com.tourflow.model.*;
import com.tourflow.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class QueryPerformanceTest {

    @Autowired
    private TourRepository tourRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    private List<Tour> testTours;
    private List<User> testUsers;
    private List<Booking> testBookings;
    private List<Payment> testPayments;
    private List<Review> testReviews;
    private Random random = new Random();

    @BeforeEach
    public void setUp() {
        // Nettoyer la base de données
        bookingRepository.deleteAll();
        paymentRepository.deleteAll();
        reviewRepository.deleteAll();
        userRepository.deleteAll();
        tourRepository.deleteAll();

        // Initialiser les données de test
        setupTestData();
    }

    private void setupTestData() {
        // Créer 1000 tours
        testTours = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            Tour tour = new Tour();
            tour.setTitle("Performance Test Tour " + i);
            tour.setDescription("A tour for performance testing " + i);
            tour.setPrice(new BigDecimal("99.99").add(BigDecimal.valueOf(random.nextDouble() * 100)));
            tour.setDuration(1 + random.nextInt(10));
            tour.setMaxParticipants(10 + random.nextInt(40));
            tour.setStatus(random.nextBoolean() ? TourStatus.ACTIVE : TourStatus.INACTIVE);
            tour.setStartDate(LocalDateTime.now().plusDays(random.nextInt(365)));
            tour.setEndDate(LocalDateTime.now().plusDays(random.nextInt(365)).plusDays(1));
            tour.setCreatedAt(LocalDateTime.now().minusDays(random.nextInt(365)));
            tour.setUpdatedAt(LocalDateTime.now().minusDays(random.nextInt(30)));
            testTours.add(tour);
        }
        tourRepository.saveAll(testTours);

        // Créer 500 utilisateurs
        testUsers = new ArrayList<>();
        for (int i = 0; i < 500; i++) {
            User user = new User();
            user.setFirstName("Performance");
            user.setLastName("Test User " + i);
            user.setEmail("performance.test.user" + i + "@example.com");
            user.setPassword("encodedPassword");
            user.setRole(random.nextBoolean() ? Role.USER : Role.ADMIN);
            user.setCreatedAt(LocalDateTime.now().minusDays(random.nextInt(365)));
            user.setUpdatedAt(LocalDateTime.now().minusDays(random.nextInt(30)));
            testUsers.add(user);
        }
        userRepository.saveAll(testUsers);

        // Créer 2000 réservations
        testBookings = new ArrayList<>();
        for (int i = 0; i < 2000; i++) {
            Booking booking = new Booking();
            booking.setUser(testUsers.get(random.nextInt(testUsers.size())));
            booking.setTour(testTours.get(random.nextInt(testTours.size())));
            booking.setParticipants(1 + random.nextInt(5));
            booking.setTotalPrice(booking.getTour().getPrice().multiply(BigDecimal.valueOf(booking.getParticipants())));
            booking.setStatus(BookingStatus.values()[random.nextInt(BookingStatus.values().length)]);
            booking.setBookingDate(LocalDateTime.now().plusDays(random.nextInt(365)));
            booking.setCreatedAt(LocalDateTime.now().minusDays(random.nextInt(365)));
            booking.setUpdatedAt(LocalDateTime.now().minusDays(random.nextInt(30)));
            testBookings.add(booking);
        }
        bookingRepository.saveAll(testBookings);

        // Créer 1500 paiements
        testPayments = new ArrayList<>();
        for (int i = 0; i < 1500; i++) {
            Payment payment = new Payment();
            payment.setBooking(testBookings.get(random.nextInt(testBookings.size())));
            payment.setAmount(payment.getBooking().getTotalPrice());
            payment.setCurrency("EUR");
            payment.setPaymentMethod(PaymentMethod.values()[random.nextInt(PaymentMethod.values().length)]);
            payment.setStatus(PaymentStatus.values()[random.nextInt(PaymentStatus.values().length)]);
            payment.setStripePaymentId("pi_performance_test" + i);
            payment.setCreatedAt(LocalDateTime.now().minusDays(random.nextInt(365)));
            payment.setUpdatedAt(LocalDateTime.now().minusDays(random.nextInt(30)));
            testPayments.add(payment);
        }
        paymentRepository.saveAll(testPayments);

        // Créer 1000 avis
        testReviews = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            Review review = new Review();
            review.setUser(testUsers.get(random.nextInt(testUsers.size())));
            review.setTour(testTours.get(random.nextInt(testTours.size())));
            review.setRating(1 + random.nextInt(5));
            review.setComment("Performance test review " + i);
            review.setCreatedAt(LocalDateTime.now().minusDays(random.nextInt(365)));
            review.setUpdatedAt(LocalDateTime.now().minusDays(random.nextInt(30)));
            testReviews.add(review);
        }
        reviewRepository.saveAll(testReviews);
    }

    @Test
    public void testTourRepositoryQueryPerformance() {
        // Given
        long startTime = System.currentTimeMillis();

        // When - Exécuter des requêtes complexes sur les tours
        Pageable pageable = PageRequest.of(0, 20);
        Page<Tour> activeTours = tourRepository.findByStatus(TourStatus.ACTIVE, pageable);
        List<Tour> toursByPriceRange = tourRepository.findByPriceBetween(new BigDecimal("50"), new BigDecimal("150"));
        List<Tour> toursByDuration = tourRepository.findByDurationGreaterThanEqual(5);

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // Then - Vérifier que les requêtes sont rapides (moins de 500ms)
        assertTrue(duration < 500, "Les requêtes sur les tours ont pris " + duration + "ms, ce qui dépasse la limite de 500ms");
        assertFalse(activeTours.isEmpty());
        assertFalse(toursByPriceRange.isEmpty());
        assertFalse(toursByDuration.isEmpty());
    }

    @Test
    public void testUserRepositoryQueryPerformance() {
        // Given
        long startTime = System.currentTimeMillis();

        // When - Exécuter des requêtes complexes sur les utilisateurs
        List<User> usersByRole = userRepository.findByRole(Role.USER);
        List<User> usersCreatedRecently = userRepository.findByCreatedAtAfter(LocalDateTime.now().minusDays(30));

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // Then - Vérifier que les requêtes sont rapides (moins de 500ms)
        assertTrue(duration < 500, "Les requêtes sur les utilisateurs ont pris " + duration + "ms, ce qui dépasse la limite de 500ms");
        assertFalse(usersByRole.isEmpty());
        assertFalse(usersCreatedRecently.isEmpty());
    }

    @Test
    public void testBookingRepositoryQueryPerformance() {
        // Given
        long startTime = System.currentTimeMillis();

        // When - Exécuter des requêtes complexes sur les réservations
        Pageable pageable = PageRequest.of(0, 20);
        Page<Booking> bookingsByStatus = bookingRepository.findByStatus(BookingStatus.PENDING, pageable);
        List<Booking> bookingsByDateRange = bookingRepository.findByBookingDateBetween(
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(30));

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // Then - Vérifier que les requêtes sont rapides (moins de 500ms)
        assertTrue(duration < 500, "Les requêtes sur les réservations ont pris " + duration + "ms, ce qui dépasse la limite de 500ms");
        assertFalse(bookingsByStatus.isEmpty());
        assertFalse(bookingsByDateRange.isEmpty());
    }

    @Test
    public void testPaymentRepositoryQueryPerformance() {
        // Given
        long startTime = System.currentTimeMillis();

        // When - Exécuter des requêtes complexes sur les paiements
        Pageable pageable = PageRequest.of(0, 20);
        Page<Payment> paymentsByStatus = paymentRepository.findByStatus(PaymentStatus.COMPLETED, pageable);
        List<Payment> paymentsByAmountRange = paymentRepository.findByAmountBetween(
                new BigDecimal("50"), new BigDecimal("200"));

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // Then - Vérifier que les requêtes sont rapides (moins de 500ms)
        assertTrue(duration < 500, "Les requêtes sur les paiements ont pris " + duration + "ms, ce qui dépasse la limite de 500ms");
        assertFalse(paymentsByStatus.isEmpty());
        assertFalse(paymentsByAmountRange.isEmpty());
    }

    @Test
    public void testReviewRepositoryQueryPerformance() {
        // Given
        long startTime = System.currentTimeMillis();

        // When - Exécuter des requêtes complexes sur les avis
        Pageable pageable = PageRequest.of(0, 20);
        Page<Review> reviewsByRating = reviewRepository.findByRatingGreaterThanEqual(4, pageable);
        List<Review> reviewsByDateRange = reviewRepository.findByCreatedAtBetween(
                LocalDateTime.now().minusDays(30), LocalDateTime.now());

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // Then - Vérifier que les requêtes sont rapides (moins de 500ms)
        assertTrue(duration < 500, "Les requêtes sur les avis ont pris " + duration + "ms, ce qui dépasse la limite de 500ms");
        assertFalse(reviewsByRating.isEmpty());
        assertFalse(reviewsByDateRange.isEmpty());
    }

    @Test
    public void testJoinQueryPerformance() {
        // Given
        long startTime = System.currentTimeMillis();

        // When - Exécuter des requêtes avec jointures complexes
        Pageable pageable = PageRequest.of(0, 20);
        Page<Booking> bookingsWithTourAndUser = bookingRepository.findAllWithTourAndUser(pageable);
        List<Review> reviewsWithTourAndUser = reviewRepository.findAllWithTourAndUser();

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // Then - Vérifier que les requêtes avec jointures sont rapides (moins de 1000ms)
        assertTrue(duration < 1000, "Les requêtes avec jointures ont pris " + duration + "ms, ce qui dépasse la limite de 1000ms");
        assertFalse(bookingsWithTourAndUser.isEmpty());
        assertFalse(reviewsWithTourAndUser.isEmpty());
    }
}
