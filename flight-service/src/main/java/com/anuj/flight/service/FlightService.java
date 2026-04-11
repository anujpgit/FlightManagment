package com.anuj.flight.service;

import com.anuj.flight.entity.Flight;

import java.time.LocalDate;
import java.util.List;

public interface FlightService {

    Flight addFlight(Flight flight);

    List<Flight> searchFlights(String from, String to, LocalDate date);

    List<Flight> getAllFlights();

    Flight getFlightById(Long id);

    void deleteFlight(Long id);
}