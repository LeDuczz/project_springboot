package com.example.demo.service.paymentservice;

import com.example.demo.enums.PaymentMethod;

public interface PaymentService {
    PaymentMethod supportPaymentMethod();
    String processPayment();
}
