package com.anuj.booking.repo;

import com.anuj.booking.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // Find bookings with ANY status in given list
    List<Booking> findByUserIdAndStatusIn(Long userId, List<String> statuses);

    // Find all bookings for a user
    List<Booking> findByUserId(Long userId);
}