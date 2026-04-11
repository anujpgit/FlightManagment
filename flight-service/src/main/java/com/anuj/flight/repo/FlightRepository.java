package com.anuj.flight.repo;

import com.anuj.flight.entity.Flight;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface FlightRepository extends JpaRepository<Flight, Long> {

    List<Flight> findByFromAirportAndToAirportAndDepartureTimeBetween(
            String from,
            String to,
            LocalDateTime start,
            LocalDateTime end
    );
}