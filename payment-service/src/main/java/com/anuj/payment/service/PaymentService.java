package com.anuj.payment.service;

public interface PaymentService {
    String processPayment(Long bookingId, Double amount);
}