package com.anuj.checkin.service;

import com.anuj.checkin.dto.BookingDto;
import com.anuj.checkin.entity.CheckIn;
import com.anuj.checkin.repo.CheckInRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

@Service
public class CheckInServiceImpl implements CheckInService {

    private static final Logger logger = LoggerFactory.getLogger(CheckInServiceImpl.class);

    private final CheckInRepository repo;

    public CheckInServiceImpl(CheckInRepository repo) {
        this.repo = repo;
    }

    @Override
    public String checkIn(Long bookingId) {

        logger.info("Entering checkIn service method with bookingId={}", bookingId);

        RestTemplate rest = new RestTemplate();
        String getUrl = "http://localhost:8083/api/bookings/" + bookingId;

        BookingDto booking;

        try {
            logger.debug("Calling Booking-Service GET {}", getUrl);
            booking = rest.getForObject(getUrl, BookingDto.class);
            logger.info("Fetched booking details successfully for bookingId={}", bookingId);
        } catch (Exception ex) {
            logger.error("Failed to fetch booking from Booking-Service for bookingId={}. Error={}",
                    bookingId, ex.getMessage());
            throw new RuntimeException("Please make a booking first.");
        }

        // Rule: Payment pending
        if ("PENDING".equals(booking.getStatus())) {
            logger.warn("Attempted check-in but payment is still pending for bookingId={}", bookingId);
            throw new RuntimeException("Make payment first to confirm your booking.");
        }

        // Rule: Only confirmed can check-in
        if (!"CONFIRMED".equals(booking.getStatus())) {
            logger.warn("Check-in not allowed. Booking is not CONFIRMED. bookingId={}, status={}",
                    bookingId, booking.getStatus());
            throw new RuntimeException("Check-in not allowed.");
        }

        // Save check-in record
        CheckIn c = new CheckIn();
        c.setBookingId(bookingId);
        c.setCheckinTime(LocalDateTime.now());
        c.setStatus("CHECKED_IN");

        repo.save(c);
        logger.info("Check-in record saved successfully for bookingId={}", bookingId);

        // Update booking status
        String updateUrl =
                "http://localhost:8083/api/bookings/" + bookingId + "/status?value=CHECKED_IN";

        try {
            logger.debug("Calling Booking-Service PUT {}", updateUrl);
            rest.put(updateUrl, null);
            logger.info("Successfully updated Booking-Service status to CHECKED_IN for bookingId={}", bookingId);
        } catch (Exception ex) {
            logger.error("Failed to update booking status for bookingId={}. Error={}",
                    bookingId, ex.getMessage());
        }

        logger.info("Exiting checkIn service method with bookingId={}", bookingId);
        return "Check-in successful.";
    }
}