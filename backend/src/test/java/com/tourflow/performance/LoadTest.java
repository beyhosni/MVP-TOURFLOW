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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class LoadTest {

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
        // Créer 100 tours
        testTours = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Tour tour = new Tour();
            tour.setTitle("Load Test Tour " + i);
            tour.setDescription("A tour for load testing " + i);
            tour.setPrice(new BigDecimal("99.99"));
            tour.setDuration(3);
            tour.setMaxParticipants(20);
            tour.setStatus(TourStatus.ACTIVE);
            tour.setStartDate(LocalDateTime.now().plusDays(7));
            tour.setEndDate(LocalDateTime.now().plusDays(10));
            tour.setCreatedAt(LocalDateTime.now());
            tour.setUpdatedAt(LocalDateTime.now());
            testTours.add(tour);
        }
        tourRepository.saveAll(testTours);

        // Créer 100 utilisateurs
        testUsers = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            User user = new User();
            user.setFirstName("Load");
            user.setLastName("Test User " + i);
            user.setEmail("loadtest.user" + i + "@example.com");
            user.setPassword("encodedPassword");
            user.setRole(Role.USER);
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());
            testUsers.add(user);
        }
        userRepository.saveAll(testUsers);

        // Créer 500 réservations
        testBookings = new ArrayList<>();
        for (int i = 0; i < 500; i++) {
            Booking booking = new Booking();
            booking.setUser(testUsers.get(i % 100));
            booking.setTour(testTours.get(i % 100));
            booking.setParticipants((i % 5) + 1);
            booking.setTotalPrice(new BigDecimal("99.99").multiply(BigDecimal.valueOf((i % 5) + 1)));
            booking.setStatus(BookingStatus.PENDING);
            booking.setBookingDate(LocalDateTime.now().plusDays((i % 30) + 1));
            booking.setCreatedAt(LocalDateTime.now());
            booking.setUpdatedAt(LocalDateTime.now());
            testBookings.add(booking);
        }
        bookingRepository.saveAll(testBookings);

        // Créer 400 paiements
        testPayments = new ArrayList<>();
        for (int i = 0; i < 400; i++) {
            Payment payment = new Payment();
            payment.setBooking(testBookings.get(i));
            payment.setAmount(testBookings.get(i).getTotalPrice());
            payment.setCurrency("EUR");
            payment.setPaymentMethod(PaymentMethod.CARD);
            payment.setStatus(i % 2 == 0 ? PaymentStatus.COMPLETED : PaymentStatus.PENDING);
            payment.setStripePaymentId("pi_loadtest" + i);
            payment.setCreatedAt(LocalDateTime.now());
            payment.setUpdatedAt(LocalDateTime.now());
            testPayments.add(payment);
        }
        paymentRepository.saveAll(testPayments);

        // Créer 300 avis
        testReviews = new ArrayList<>();
        for (int i = 0; i < 300; i++) {
            Review review = new Review();
            review.setUser(testUsers.get(i % 100));
            review.setTour(testTours.get(i % 100));
            review.setRating((i % 5) + 1);
            review.setComment("Load test review " + i);
            review.setCreatedAt(LocalDateTime.now());
            review.setUpdatedAt(LocalDateTime.now());
            testReviews.add(review);
        }
        reviewRepository.saveAll(testReviews);
    }

    @Test
    public void testTourRepositoryLoad() throws InterruptedException {
        // Given
        ExecutorService executor = Executors.newFixedThreadPool(10);
        long startTime = System.currentTimeMillis();

        // When - Exécuter 100 requêtes simultanées
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            final int index = i;
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                Pageable pageable = PageRequest.of(0, 10);
                Page<Tour> tours = tourRepository.findAll(pageable);
                assertFalse(tours.isEmpty());
            }, executor);
            futures.add(future);
        }

        // Attendre que toutes les requêtes soient terminées
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // Then - Vérifier que toutes les requêtes ont été exécutées en moins de 5 secondes
        assertTrue(duration < 5000, "Les requêtes ont pris " + duration + "ms, ce qui dépasse la limite de 5000ms");
    }

    @Test
    public void testBookingRepositoryLoad() throws InterruptedException {
        // Given
        ExecutorService executor = Executors.newFixedThreadPool(10);
        long startTime = System.currentTimeMillis();

        // When - Exécuter 100 requêtes simultanées
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            final int index = i;
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                Pageable pageable = PageRequest.of(0, 10);
                Page<Booking> bookings = bookingRepository.findByUserId(testUsers.get(index % 100).getId(), pageable);
                assertFalse(bookings.isEmpty());
            }, executor);
            futures.add(future);
        }

        // Attendre que toutes les requêtes soient terminées
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // Then - Vérifier que toutes les requêtes ont été exécutées en moins de 5 secondes
        assertTrue(duration < 5000, "Les requêtes ont pris " + duration + "ms, ce qui dépasse la limite de 5000ms");
    }

    @Test
    public void testPaymentRepositoryLoad() throws InterruptedException {
        // Given
        ExecutorService executor = Executors.newFixedThreadPool(10);
        long startTime = System.currentTimeMillis();

        // When - Exécuter 100 requêtes simultanées
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            final int index = i;
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                Pageable pageable = PageRequest.of(0, 10);
                Page<Payment> payments = paymentRepository.findByBookingId(testBookings.get(index % 400).getId(), pageable);
                assertFalse(payments.isEmpty());
            }, executor);
            futures.add(future);
        }

        // Attendre que toutes les requêtes soient terminées
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // Then - Vérifier que toutes les requêtes ont été exécutées en moins de 5 secondes
        assertTrue(duration < 5000, "Les requêtes ont pris " + duration + "ms, ce qui dépasse la limite de 5000ms");
    }

    @Test
    public void testReviewRepositoryLoad() throws InterruptedException {
        // Given
        ExecutorService executor = Executors.newFixedThreadPool(10);
        long startTime = System.currentTimeMillis();

        // When - Exécuter 100 requêtes simultanées
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            final int index = i;
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                Pageable pageable = PageRequest.of(0, 10);
                Page<Review> reviews = reviewRepository.findByTourId(testTours.get(index % 100).getId(), pageable);
                assertFalse(reviews.isEmpty());
            }, executor);
            futures.add(future);
        }

        // Attendre que toutes les requêtes soient terminées
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // Then - Vérifier que toutes les requêtes ont été exécutées en moins de 5 secondes
        assertTrue(duration < 5000, "Les requêtes ont pris " + duration + "ms, ce qui dépasse la limite de 5000ms");
    }

    @Test
    public void testMixedLoad() throws InterruptedException {
        // Given
        ExecutorService executor = Executors.newFixedThreadPool(20);
        long startTime = System.currentTimeMillis();

        // When - Exécuter 200 requêtes simultanées sur différents repositories
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        // 50 requêtes sur les tours
        for (int i = 0; i < 50; i++) {
            final int index = i;
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                Pageable pageable = PageRequest.of(0, 10);
                Page<Tour> tours = tourRepository.findAll(pageable);
                assertFalse(tours.isEmpty());
            }, executor);
            futures.add(future);
        }

        // 50 requêtes sur les utilisateurs
        for (int i = 0; i < 50; i++) {
            final int index = i;
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                Optional<User> user = userRepository.findById(testUsers.get(index % 100).getId());
                assertTrue(user.isPresent());
            }, executor);
            futures.add(future);
        }

        // 50 requêtes sur les réservations
        for (int i = 0; i < 50; i++) {
            final int index = i;
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                Pageable pageable = PageRequest.of(0, 10);
                Page<Booking> bookings = bookingRepository.findByUserId(testUsers.get(index % 100).getId(), pageable);
                assertFalse(bookings.isEmpty());
            }, executor);
            futures.add(future);
        }

        // 50 requêtes sur les paiements
        for (int i = 0; i < 50; i++) {
            final int index = i;
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                Optional<Payment> payment = paymentRepository.findById(testPayments.get(index % 400).getId());
                assertTrue(payment.isPresent());
            }, executor);
            futures.add(future);
        }

        // Attendre que toutes les requêtes soient terminées
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // Then - Vérifier que toutes les requêtes ont été exécutées en moins de 10 secondes
        assertTrue(duration < 10000, "Les requêtes ont pris " + duration + "ms, ce qui dépasse la limite de 10000ms");
    }
}
