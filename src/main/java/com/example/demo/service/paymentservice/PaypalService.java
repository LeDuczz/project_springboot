package com.example.demo.service.paymentservice;

import com.example.demo.enums.PaymentMethod;
import org.springframework.stereotype.Service;

@Service("paypal")
public class PaypalService implements PaymentService{
    @Override
    public PaymentMethod supportPaymentMethod() {
        return PaymentMethod.PAYPAL;
    }

    @Override
    public String processPayment() {
        return "Processing payment with PayPal";
    }
}
