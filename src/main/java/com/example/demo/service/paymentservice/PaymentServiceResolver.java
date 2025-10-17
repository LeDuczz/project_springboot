package com.example.demo.service.paymentservice;

import com.example.demo.enums.PaymentMethod;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class PaymentServiceResolver {
    private final Map<PaymentMethod, PaymentService> paymentServices;

    public PaymentServiceResolver(List<PaymentService> services) {
        this.paymentServices = services.stream()
                .collect(Collectors.toMap(PaymentService::supportPaymentMethod, Function.identity()));

    }

    public PaymentService resolve(PaymentMethod paymentMethod) {
        if (paymentMethod == null) {
            throw new IllegalArgumentException("Payment method must be provided");
        }
        PaymentService service = paymentServices.get(paymentMethod);
        if (service == null) {
            throw new UnsupportedOperationException("No payment service found for method: " + paymentMethod);
        }
        return service;
    }
    
}
