package com.anuj.checkin.controller;

import com.anuj.checkin.service.CheckInService;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/checkin")
public class CheckInController {

    private static final Logger logger = LoggerFactory.getLogger(CheckInController.class);

    private final CheckInService service;

    public CheckInController(CheckInService service) {
        this.service = service;
    }

    @PostMapping
    public String checkIn(@RequestParam Long bookingId) {
        logger.info("Entering checkIn method with bookingId={}", bookingId);

        String response = service.checkIn(bookingId);

        logger.info("Exiting checkIn method with response={}", response);
        return response;
    }
}