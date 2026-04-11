package com.anuj.booking.controller;

import java.util.List;
import java.util.Arrays;

import com.anuj.booking.entity.Booking;
import com.anuj.booking.service.BookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService service;

    public BookingController(BookingService service) {
        this.service = service;
    }

    // -------------------- CREATE BOOKING ----------------------
    @PostMapping
    public Booking create(@RequestParam Long userId,
                          @RequestParam Long flightId) {
        return service.createBooking(userId, flightId);
    }

    // -------------------- UPDATE STATUS ----------------------
    @PutMapping("/{id}/status")
    public Booking updateStatus(@PathVariable Long id,
                                @RequestParam String value) {
        return service.updateStatus(id, value);
    }

    // -------------------- GET ALL BOOKINGS (ADMIN) ----------------------
    @GetMapping
    public List<Booking> getAllBookings() {
        return service.getAllBookings();
    }

    // -------------------- GET LOGGED-IN USER BOOKING ----------------------
    // Updated to include CONFIRMED + CHECKED_IN
    @GetMapping("/user/{userId}")
    public Booking getByUserId(@PathVariable Long userId) {

        List<String> statuses = Arrays.asList(
                "PENDING",
                "CONFIRMED",
                "CHECKED_IN"
        );

        return service.getBookingByUserIdAndStatus(userId, statuses)
                .orElse(null);
    }

    // -------------------- GET BY BOOKING ID ----------------------
    @GetMapping("/{id}")
    public Booking get(@PathVariable Long id) {
        return service.getBooking(id);
    }

    // -------------------- GLOBAL ERROR HANDLER (IMPORTANT!) ----------------------
    // Converts RuntimeException → HTTP 400 instead of HTTP 500
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntime(RuntimeException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}