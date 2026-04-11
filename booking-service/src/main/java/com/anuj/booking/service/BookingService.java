package com.anuj.booking.service;

import com.anuj.booking.entity.Booking;
import java.util.List;
import java.util.Optional;

public interface BookingService {

    Booking createBooking(Long userId, Long flightId);

    Booking getBooking(Long id);

    Booking updateStatus(Long id, String status);

    List<Booking> getAllBookings();

    Optional<Booking> getBookingByUserIdAndStatus(Long userId, List<String> statuses);

    List<Booking> getBookingsByUserId(Long userId);
}