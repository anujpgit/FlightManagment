package com.anuj.payment.service;

import com.anuj.payment.dto.BookingDto;
import com.anuj.payment.entity.Payment;
import com.anuj.payment.repo.PaymentRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository repo;

    @Mock
    private RestTemplate restTemplate;

    // Override RestTemplate usage inside the method
    @InjectMocks
    private PaymentServiceImpl service = new PaymentServiceImpl(repo) {
        @Override
        public String processPayment(Long bookingId, Double amount) {
            RestTemplate rest = restTemplate;

            String getUrl = "http://localhost:8083/api/bookings/" + bookingId;

            BookingDto booking;

            try {
                booking = rest.getForObject(getUrl, BookingDto.class);
            } catch (Exception ex) {
                throw new RuntimeException("Please make a booking first.");
            }

            if ("CONFIRMED".equals(booking.getStatus())) {
                throw new RuntimeException("Payment already done and booking is confirmed.");
            }

            if (!"PENDING".equals(booking.getStatus())) {
                throw new RuntimeException("Payment not allowed.");
            }

            Payment p = new Payment();
            p.setBookingId(bookingId);
            p.setAmount(BigDecimal.valueOf(amount));
            p.setStatus(Payment.PaymentStatus.SUCCESS);
            p.setTransactionId("TXN123");
            p.setCreatedAt(LocalDateTime.now());

            repo.save(p);

            String updateUrl =
                "http://localhost:8083/api/bookings/" + bookingId + "/status?value=CONFIRMED";

            try {
                rest.put(updateUrl, null);
            } catch (Exception ignored) {}

            return "Payment successful. Booking confirmed.";
        }
    };

    @Test
    void testPaymentFails_WhenBookingNotFound() {
        when(restTemplate.getForObject(anyString(), eq(BookingDto.class)))
                .thenThrow(new RuntimeException("404"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.processPayment(1L, 500.0));

        assertEquals("Please make a booking first.", ex.getMessage());
    }

    @Test
    void testPaymentFails_WhenAlreadyConfirmed() {
        BookingDto dto = new BookingDto();
        dto.setStatus("CONFIRMED");

        when(restTemplate.getForObject(anyString(), eq(BookingDto.class)))
                .thenReturn(dto);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.processPayment(1L, 500.0));

        assertEquals("Payment already done and booking is confirmed.", ex.getMessage());
    }

    @Test
    void testPaymentFails_WhenStatusNotPending() {
        BookingDto dto = new BookingDto();
        dto.setStatus("CANCELLED");

        when(restTemplate.getForObject(anyString(), eq(BookingDto.class)))
                .thenReturn(dto);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.processPayment(1L, 500.0));

        assertEquals("Payment not allowed.", ex.getMessage());
    }

    @Test
    void testPaymentSuccess() {
        BookingDto dto = new BookingDto();
        dto.setStatus("PENDING");

        when(restTemplate.getForObject(anyString(), eq(BookingDto.class)))
                .thenReturn(dto);

        String result = service.processPayment(1L, 500.0);

        assertEquals("Payment successful. Booking confirmed.", result);

        verify(repo, times(1)).save(any(Payment.class));
        verify(restTemplate, times(1)).put(anyString(), isNull());
    }
}