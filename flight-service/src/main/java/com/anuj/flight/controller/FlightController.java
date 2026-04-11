package com.anuj.flight.controller;

import com.anuj.flight.entity.Flight;
import com.anuj.flight.service.FlightService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/flights")
public class FlightController {

    private final FlightService service;

    public FlightController(FlightService service) {
        this.service = service;
    }

    // View all flights (Passenger/Admin)
    @GetMapping
    public List<Flight> getAllFlights() {
        return service.getAllFlights();
    }

    // View flight by ID
    @GetMapping("/{id}")
    public Flight getFlightById(@PathVariable Long id) {
        return service.getFlightById(id);
    }

    // Create flight (Admin only)
    @PostMapping
    public Flight addFlight(@RequestBody Flight flight) {
        return service.addFlight(flight);
    }

    // Delete flight (Admin only)
    @DeleteMapping("/{id}")
    public void deleteFlight(@PathVariable Long id) {
        service.deleteFlight(id);
    }

    // Flight search (Passenger/Admin)
    @GetMapping("/search")
    public List<Flight> searchFlights(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam String date
    ) {
        return service.searchFlights(from, to, LocalDate.parse(date));
    }
}