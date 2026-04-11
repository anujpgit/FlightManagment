package com.anuj.flight.service;

import com.anuj.flight.entity.Flight;
import com.anuj.flight.repo.FlightRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FlightServiceImplTest {

    @Mock
    private FlightRepository repo;

    @InjectMocks
    private FlightServiceImpl service;

    @Test
    void testAddFlight() {
        Flight f = new Flight();
        f.setId(1L);
        when(repo.save(any(Flight.class))).thenReturn(f);

        Flight result = service.addFlight(f);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testSearchFlights() {
        LocalDate date = LocalDate.of(2024, 1, 1);
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(23, 59, 59);

        List<Flight> flights = List.of(new Flight());
        when(repo.findByFromAirportAndToAirportAndDepartureTimeBetween(
                eq("DEL"), eq("MUM"), eq(start), eq(end)
        )).thenReturn(flights);

        List<Flight> result = service.searchFlights("DEL", "MUM", date);

        assertEquals(1, result.size());
    }

    @Test
    void testGetAllFlights() {
        when(repo.findAll()).thenReturn(List.of(new Flight(), new Flight()));
        List<Flight> result = service.getAllFlights();
        assertEquals(2, result.size());
    }

    @Test
    void testGetFlightByIdFound() {
        Flight f = new Flight();
        f.setId(1L);

        when(repo.findById(1L)).thenReturn(Optional.of(f));

        Flight result = service.getFlightById(1L);

        assertEquals(1L, result.getId());
    }

    @Test
    void testGetFlightByIdNotFound() {
        when(repo.findById(5L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> service.getFlightById(5L)
        );

        assertEquals("Flight not found", ex.getMessage());
    }

    @Test
    void testDeleteFlightSuccess() {
        when(repo.existsById(1L)).thenReturn(true);
        doNothing().when(repo).deleteById(1L);

        assertDoesNotThrow(() -> service.deleteFlight(1L));
        verify(repo).deleteById(1L);
    }

    @Test
    void testDeleteFlightNotFound() {
        when(repo.existsById(10L)).thenReturn(false);

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> service.deleteFlight(10L)
        );

        assertEquals("Flight not found", ex.getMessage());
    }
}