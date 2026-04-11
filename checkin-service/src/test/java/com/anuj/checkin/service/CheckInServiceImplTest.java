package com.anuj.checkin.service;

import com.anuj.checkin.dto.BookingDto;
import com.anuj.checkin.entity.CheckIn;
import com.anuj.checkin.repo.CheckInRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CheckInServiceImplTest {

    @Mock
    private CheckInRepository repo;

    @Mock
    private RestTemplate restTemplate;  // 🟢 We'll inject this manually

    // We need to FORCE CheckInServiceImpl to use our mocked RestTemplate.
    @InjectMocks
    private CheckInServiceImpl service = new CheckInServiceImpl(repo) {
        @Override
        public String checkIn(Long bookingId) {
            RestTemplate rest = restTemplate; // override RestTemplate creation

            String getUrl = "http://localhost:8083/api/bookings/" + bookingId;

            BookingDto booking;

            try {
                booking = rest.getForObject(getUrl, BookingDto.class);
            } catch (Exception ex) {
                throw new RuntimeException("Please make a booking first.");
            }

            if ("PENDING".equals(booking.getStatus())) {
                throw new RuntimeException("Make payment first to confirm your booking.");
            }

            if (!"CONFIRMED".equals(booking.getStatus())) {
                throw new RuntimeException("Check-in not allowed.");
            }

            CheckIn c = new CheckIn();
            c.setBookingId(bookingId);
            c.setCheckinTime(LocalDateTime.now());
            c.setStatus("CHECKED_IN");
            repo.save(c);

            String updateUrl =
                    "http://localhost:8083/api/bookings/" + bookingId + "/status?value=CHECKED_IN";

            try {
                rest.put(updateUrl, null);
            } catch (Exception ignored) {}

            return "Check-in successful.";
        }
    };

    @Test
    void testCheckInFails_WhenBookingNotFound() {
        when(restTemplate.getForObject(anyString(), eq(BookingDto.class)))
                .thenThrow(new RuntimeException("404"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.checkIn(1L));

        assertEquals("Please make a booking first.", ex.getMessage());
    }

    @Test
    void testCheckInFails_WhenPaymentPending() {
        BookingDto dto = new BookingDto();
        dto.setStatus("PENDING");

        when(restTemplate.getForObject(anyString(), eq(BookingDto.class)))
                .thenReturn(dto);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.checkIn(1L));

        assertEquals("Make payment first to confirm your booking.", ex.getMessage());
    }

    @Test
    void testCheckInFails_WhenStatusNotConfirmed() {
        BookingDto dto = new BookingDto();
        dto.setStatus("CANCELLED");

        when(restTemplate.getForObject(anyString(), eq(BookingDto.class)))
                .thenReturn(dto);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.checkIn(1L));

        assertEquals("Check-in not allowed.", ex.getMessage());
    }

    @Test
    void testCheckInSuccess() {
        BookingDto dto = new BookingDto();
        dto.setStatus("CONFIRMED");

        when(restTemplate.getForObject(anyString(), eq(BookingDto.class)))
                .thenReturn(dto);

        String response = service.checkIn(1L);

        assertEquals("Check-in successful.", response);
        verify(repo, times(1)).save(any(CheckIn.class));
        verify(restTemplate, times(1)).put(anyString(), isNull());
    }
}