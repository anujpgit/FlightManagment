package com.anuj.booking.service;

import com.anuj.booking.entity.Booking;
import com.anuj.booking.repo.BookingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {

    @Mock
    private BookingRepository repo;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    void testCreateBookingSuccess() {
        Long userId = 1L;
        Long flightId = 100L;

        // No existing pending bookings
        when(repo.findByUserIdAndStatusIn(eq(userId), anyList()))
                .thenReturn(Collections.emptyList());

        Booking saved = new Booking();
        saved.setId(1L);
        saved.setUserId(userId);
        saved.setFlightId(flightId);
        saved.setStatus("PENDING");
        saved.setCreatedAt(LocalDateTime.now());

        // Saving new booking
        when(repo.save(any(Booking.class))).thenReturn(saved);

        Booking result = bookingService.createBooking(userId, flightId);

        assertNotNull(result);
        assertEquals("PENDING", result.getStatus());
        assertEquals(userId, result.getUserId());
    }

    @Test
    void testCreateBookingFails_existingPendingBooking() {
        Long userId = 1L;
        Long flightId = 100L;

        List<Booking> existing = new ArrayList<>();
        Booking b = new Booking();
        b.setUserId(userId);
        b.setStatus("PENDING");
        existing.add(b);

        when(repo.findByUserIdAndStatusIn(eq(userId), anyList()))
                .thenReturn(existing);

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> bookingService.createBooking(userId, flightId)
        );

        assertEquals("A booking already exists for this user.", ex.getMessage());
    }

    @Test
    void testGetBookingFound() {
        Booking b = new Booking();
        b.setId(1L);

        when(repo.findById(1L)).thenReturn(Optional.of(b));

        Booking result = bookingService.getBooking(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testGetBookingNotFound() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> bookingService.getBooking(99L)
        );

        assertEquals("Booking not found", ex.getMessage());
    }

    @Test
    void testUpdateStatus() {
        Booking b = new Booking();
        b.setId(1L);
        b.setStatus("PENDING");

        when(repo.findById(1L)).thenReturn(Optional.of(b));

        Booking updated = new Booking();
        updated.setId(1L);
        updated.setStatus("CONFIRMED");

        when(repo.save(any(Booking.class))).thenReturn(updated);

        Booking result = bookingService.updateStatus(1L, "CONFIRMED");

        assertEquals("CONFIRMED", result.getStatus());
    }

    @Test
    void testGetAllBookings() {
        List<Booking> list = List.of(new Booking(), new Booking());

        when(repo.findAll()).thenReturn(list);

        List<Booking> result = bookingService.getAllBookings();

        assertEquals(2, result.size());
    }

    @Test
    void testGetBookingsByUserId() {
        List<Booking> list = List.of(new Booking());

        when(repo.findByUserId(1L)).thenReturn(list);

        List<Booking> result = bookingService.getBookingsByUserId(1L);

        assertEquals(1, result.size());
    }
}