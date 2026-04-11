package com.anuj.flight.service;

import com.anuj.flight.entity.Flight;
import com.anuj.flight.repo.FlightRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FlightServiceImpl implements FlightService {

    private final FlightRepository repo;

    public FlightServiceImpl(FlightRepository repo) {
        this.repo = repo;
    }

    @Override
    public Flight addFlight(Flight flight) {
        return repo.save(flight);
    }

    @Override
    public List<Flight> searchFlights(String from, String to, LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(23, 59, 59);

        return repo.findByFromAirportAndToAirportAndDepartureTimeBetween(from, to, start, end);
    }

    @Override
    public List<Flight> getAllFlights() {
        return repo.findAll();
    }

    @Override
    public Flight getFlightById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Flight not found"));
    }

    @Override
    public void deleteFlight(Long id) {
        if (!repo.existsById(id)) {
            throw new RuntimeException("Flight not found");
        }
        repo.deleteById(id);
    }
}