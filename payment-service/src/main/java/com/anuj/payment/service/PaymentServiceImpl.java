package com.anuj.payment.service;

import com.anuj.payment.dto.BookingDto;
import com.anuj.payment.entity.Payment;
import com.anuj.payment.repo.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository repo;

    public PaymentServiceImpl(PaymentRepository repo) {
        this.repo = repo;
    }

    @Override
    public String processPayment(Long bookingId, Double amount) {

        RestTemplate rest = new RestTemplate();
        String getUrl = "http://localhost:8083/api/bookings/" + bookingId;

        BookingDto booking;

        // Fetch booking details
        try {
            booking = rest.getForObject(getUrl, BookingDto.class);
        } catch (Exception ex) {
            throw new RuntimeException("Please make a booking first.");
        }

        // Validate booking status
        if ("CONFIRMED".equals(booking.getStatus())) {
            throw new RuntimeException("Payment already done and booking is confirmed.");
        }

        if (!"PENDING".equals(booking.getStatus())) {
            throw new RuntimeException("Payment not allowed.");
        }

        // Save payment as SUCCESS
        Payment p = new Payment();
        p.setBookingId(bookingId);
        p.setAmount(BigDecimal.valueOf(amount));
        p.setStatus(Payment.PaymentStatus.SUCCESS);
        p.setTransactionId(UUID.randomUUID().toString());
        p.setCreatedAt(LocalDateTime.now());

        repo.save(p);

        // Update booking status
        String updateUrl =
                "http://localhost:8083/api/bookings/" + bookingId + "/status?value=CONFIRMED";

        try {
            rest.put(updateUrl, null);
        } catch (Exception ex) {
            System.out.println("Booking status update failed: " + ex.getMessage());
        }

        return "Payment successful. Booking confirmed.";
    }
}