package com.example.demo.service.paymentservice;

import com.example.demo.enums.PaymentMethod;
import org.springframework.stereotype.Service;

@Service("vnpay")
public class VnpayService implements PaymentService{
    @Override
    public PaymentMethod supportPaymentMethod() {
        return PaymentMethod.VNPAY;
    }

    @Override
    public String processPayment() {
        return "Processing payment with VNPAY";
    }
}
