package com.anuj.booking.service;

import com.anuj.booking.entity.Booking;
import com.anuj.booking.repo.BookingRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository repo;

    public BookingServiceImpl(BookingRepository repo) {
        this.repo = repo;
    }

    @Override
    public Booking createBooking(Long userId, Long flightId) {

        // These statuses block new bookings
        List<String> blockedStatuses = Arrays.asList("PENDING", "CONFIRMED", "CHECKED_IN");

        List<Booking> existing = repo.findByUserIdAndStatusIn(userId, blockedStatuses);

        if (!existing.isEmpty()) {
            throw new RuntimeException("A booking already exists for this user.");
        }

        Booking b = new Booking();
        b.setUserId(userId);
        b.setFlightId(flightId);

        // generate PNR
        String pnr = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        b.setPnr(pnr);

        b.setStatus("PENDING");
        b.setCreatedAt(LocalDateTime.now());

        return repo.save(b);
    }

    @Override
    public Booking getBooking(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
    }

    @Override
    public Booking updateStatus(Long id, String status) {
        Booking b = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        b.setStatus(status);
        return repo.save(b);
    }

    @Override
    public List<Booking> getAllBookings() {
        return repo.findAll();
    }

    @Override
    public Optional<Booking> getBookingByUserIdAndStatus(Long userId, List<String> statuses) {
        List<Booking> b = repo.findByUserIdAndStatusIn(userId, statuses);
        return b.isEmpty() ? Optional.empty() : Optional.of(b.get(0));
    }

    @Override
    public List<Booking> getBookingsByUserId(Long userId) {
        return repo.findByUserId(userId);
    }
}