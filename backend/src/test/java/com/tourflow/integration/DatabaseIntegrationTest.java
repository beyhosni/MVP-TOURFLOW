package com.tourflow.integration;

import com.tourflow.model.*;
import com.tourflow.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class DatabaseIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

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

    private Tour testTour;
    private User testUser;
    private Booking testBooking;
    private Payment testPayment;
    private Review testReview;

    @BeforeEach
    public void setUp() {
        // Initialiser les objets de test
        setupTestTour();
        setupTestUser();
        setupTestBooking();
        setupTestPayment();
        setupTestReview();
    }

    private void setupTestTour() {
        testTour = new Tour();
        testTour.setTitle("Test Tour");
        testTour.setDescription("A test tour for integration testing");
        testTour.setPrice(new BigDecimal("99.99"));
        testTour.setDuration(3);
        testTour.setMaxParticipants(20);
        testTour.setStatus(TourStatus.ACTIVE);
        testTour.setStartDate(LocalDateTime.now().plusDays(7));
        testTour.setEndDate(LocalDateTime.now().plusDays(10));
        testTour.setCreatedAt(LocalDateTime.now());
        testTour.setUpdatedAt(LocalDateTime.now());
    }

    private void setupTestUser() {
        testUser = new User();
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setEmail("test.user@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setRole(Role.USER);
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());
    }

    private void setupTestBooking() {
        testBooking = new Booking();
        testBooking.setUser(testUser);
        testBooking.setTour(testTour);
        testBooking.setParticipants(2);
        testBooking.setTotalPrice(new BigDecimal("199.98"));
        testBooking.setStatus(BookingStatus.PENDING);
        testBooking.setBookingDate(LocalDateTime.now().plusDays(7));
        testBooking.setCreatedAt(LocalDateTime.now());
        testBooking.setUpdatedAt(LocalDateTime.now());
    }

    private void setupTestPayment() {
        testPayment = new Payment();
        testPayment.setBooking(testBooking);
        testPayment.setAmount(new BigDecimal("199.98"));
        testPayment.setCurrency("EUR");
        testPayment.setPaymentMethod(PaymentMethod.CARD);
        testPayment.setStatus(PaymentStatus.PENDING);
        testPayment.setStripePaymentId("pi_test123");
        testPayment.setCreatedAt(LocalDateTime.now());
        testPayment.setUpdatedAt(LocalDateTime.now());
    }

    private void setupTestReview() {
        testReview = new Review();
        testReview.setUser(testUser);
        testReview.setTour(testTour);
        testReview.setRating(5);
        testReview.setComment("Excellent tour!");
        testReview.setCreatedAt(LocalDateTime.now());
        testReview.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    public void testTourRepository() {
        // Given
        Tour savedTour = tourRepository.save(testTour);

        // When
        Optional<Tour> foundTour = tourRepository.findById(savedTour.getId());

        // Then
        assertTrue(foundTour.isPresent());
        assertEquals(savedTour.getTitle(), foundTour.get().getTitle());
        assertEquals(savedTour.getDescription(), foundTour.get().getDescription());
        assertEquals(savedTour.getPrice(), foundTour.get().getPrice());
    }

    @Test
    public void testUserRepository() {
        // Given
        User savedUser = userRepository.save(testUser);

        // When
        Optional<User> foundUser = userRepository.findById(savedUser.getId());

        // Then
        assertTrue(foundUser.isPresent());
        assertEquals(savedUser.getEmail(), foundUser.get().getEmail());
        assertEquals(savedUser.getFirstName(), foundUser.get().getFirstName());
        assertEquals(savedUser.getLastName(), foundUser.get().getLastName());
    }

    @Test
    public void testBookingRepository() {
        // Given
        User savedUser = userRepository.save(testUser);
        Tour savedTour = tourRepository.save(testTour);

        testBooking.setUser(savedUser);
        testBooking.setTour(savedTour);
        Booking savedBooking = bookingRepository.save(testBooking);

        // When
        Optional<Booking> foundBooking = bookingRepository.findById(savedBooking.getId());

        // Then
        assertTrue(foundBooking.isPresent());
        assertEquals(savedBooking.getParticipants(), foundBooking.get().getParticipants());
        assertEquals(savedBooking.getTotalPrice(), foundBooking.get().getTotalPrice());
        assertEquals(savedBooking.getStatus(), foundBooking.get().getStatus());
    }

    @Test
    public void testPaymentRepository() {
        // Given
        User savedUser = userRepository.save(testUser);
        Tour savedTour = tourRepository.save(testTour);

        testBooking.setUser(savedUser);
        testBooking.setTour(savedTour);
        Booking savedBooking = bookingRepository.save(testBooking);

        testPayment.setBooking(savedBooking);
        Payment savedPayment = paymentRepository.save(testPayment);

        // When
        Optional<Payment> foundPayment = paymentRepository.findById(savedPayment.getId());

        // Then
        assertTrue(foundPayment.isPresent());
        assertEquals(savedPayment.getAmount(), foundPayment.get().getAmount());
        assertEquals(savedPayment.getCurrency(), foundPayment.get().getCurrency());
        assertEquals(savedPayment.getPaymentMethod(), foundPayment.get().getPaymentMethod());
        assertEquals(savedPayment.getStatus(), foundPayment.get().getStatus());
    }

    @Test
    public void testReviewRepository() {
        // Given
        User savedUser = userRepository.save(testUser);
        Tour savedTour = tourRepository.save(testTour);

        testReview.setUser(savedUser);
        testReview.setTour(savedTour);
        Review savedReview = reviewRepository.save(testReview);

        // When
        Optional<Review> foundReview = reviewRepository.findById(savedReview.getId());

        // Then
        assertTrue(foundReview.isPresent());
        assertEquals(savedReview.getRating(), foundReview.get().getRating());
        assertEquals(savedReview.getComment(), foundReview.get().getComment());
    }

    @Test
    public void testTourRepositoryPagination() {
        // Given
        for (int i = 0; i < 25; i++) {
            Tour tour = new Tour();
            tour.setTitle("Test Tour " + i);
            tour.setDescription("A test tour for pagination testing " + i);
            tour.setPrice(new BigDecimal("99.99"));
            tour.setDuration(3);
            tour.setMaxParticipants(20);
            tour.setStatus(TourStatus.ACTIVE);
            tour.setStartDate(LocalDateTime.now().plusDays(7));
            tour.setEndDate(LocalDateTime.now().plusDays(10));
            tour.setCreatedAt(LocalDateTime.now());
            tour.setUpdatedAt(LocalDateTime.now());
            tourRepository.save(tour);
        }

        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Tour> tours = tourRepository.findAll(pageable);

        // Then
        assertEquals(10, tours.getContent().size());
        assertEquals(25, tours.getTotalElements());
        assertEquals(3, tours.getTotalPages());
    }

    @Test
    public void testTourRepositoryFindByStatus() {
        // Given
        Tour activeTour = new Tour();
        activeTour.setTitle("Active Tour");
        activeTour.setStatus(TourStatus.ACTIVE);
        tourRepository.save(activeTour);

        Tour inactiveTour = new Tour();
        inactiveTour.setTitle("Inactive Tour");
        inactiveTour.setStatus(TourStatus.INACTIVE);
        tourRepository.save(inactiveTour);

        // When
        List<Tour> activeTours = tourRepository.findByStatus(TourStatus.ACTIVE);
        List<Tour> inactiveTours = tourRepository.findByStatus(TourStatus.INACTIVE);

        // Then
        assertEquals(1, activeTours.size());
        assertEquals("Active Tour", activeTours.get(0).getTitle());

        assertEquals(1, inactiveTours.size());
        assertEquals("Inactive Tour", inactiveTours.get(0).getTitle());
    }

    @Test
    public void testBookingRepositoryFindByUserId() {
        // Given
        User savedUser = userRepository.save(testUser);
        Tour savedTour = tourRepository.save(testTour);

        testBooking.setUser(savedUser);
        testBooking.setTour(savedTour);
        bookingRepository.save(testBooking);

        // When
        Pageable pageable = PageRequest.of(0, 10);
        Page<Booking> bookings = bookingRepository.findByUserId(savedUser.getId(), pageable);

        // Then
        assertEquals(1, bookings.getContent().size());
        assertEquals(savedUser.getId(), bookings.getContent().get(0).getUser().getId());
    }

    @Test
    public void testBookingRepositoryFindByTourId() {
        // Given
        User savedUser = userRepository.save(testUser);
        Tour savedTour = tourRepository.save(testTour);

        testBooking.setUser(savedUser);
        testBooking.setTour(savedTour);
        bookingRepository.save(testBooking);

        // When
        Pageable pageable = PageRequest.of(0, 10);
        Page<Booking> bookings = bookingRepository.findByTourId(savedTour.getId(), pageable);

        // Then
        assertEquals(1, bookings.getContent().size());
        assertEquals(savedTour.getId(), bookings.getContent().get(0).getTour().getId());
    }

    @Test
    public void testPaymentRepositoryFindByBookingId() {
        // Given
        User savedUser = userRepository.save(testUser);
        Tour savedTour = tourRepository.save(testTour);

        testBooking.setUser(savedUser);
        testBooking.setTour(savedTour);
        Booking savedBooking = bookingRepository.save(testBooking);

        testPayment.setBooking(savedBooking);
        paymentRepository.save(testPayment);

        // When
        Pageable pageable = PageRequest.of(0, 10);
        Page<Payment> payments = paymentRepository.findByBookingId(savedBooking.getId(), pageable);

        // Then
        assertEquals(1, payments.getContent().size());
        assertEquals(savedBooking.getId(), payments.getContent().get(0).getBooking().getId());
    }

    @Test
    public void testReviewRepositoryFindByTourId() {
        // Given
        User savedUser = userRepository.save(testUser);
        Tour savedTour = tourRepository.save(testTour);

        testReview.setUser(savedUser);
        testReview.setTour(savedTour);
        reviewRepository.save(testReview);

        // When
        Pageable pageable = PageRequest.of(0, 10);
        Page<Review> reviews = reviewRepository.findByTourId(savedTour.getId(), pageable);

        // Then
        assertEquals(1, reviews.getContent().size());
        assertEquals(savedTour.getId(), reviews.getContent().get(0).getTour().getId());
    }

    @Test
    public void testReviewRepositoryFindByUserId() {
        // Given
        User savedUser = userRepository.save(testUser);
        Tour savedTour = tourRepository.save(testTour);

        testReview.setUser(savedUser);
        testReview.setTour(savedTour);
        reviewRepository.save(testReview);

        // When
        Pageable pageable = PageRequest.of(0, 10);
        Page<Review> reviews = reviewRepository.findByUserId(savedUser.getId(), pageable);

        // Then
        assertEquals(1, reviews.getContent().size());
        assertEquals(savedUser.getId(), reviews.getContent().get(0).getUser().getId());
    }
}
