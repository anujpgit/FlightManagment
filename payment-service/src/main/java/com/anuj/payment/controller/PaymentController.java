package com.anuj.payment.controller;

import com.anuj.payment.service.PaymentService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService service;

    public PaymentController(PaymentService service) {
        this.service = service;
    }

    @PostMapping
    public String pay(@RequestParam Long bookingId,
                      @RequestParam Double amount) {
        return service.processPayment(bookingId, amount);
    }
}