package com.example.demo.controller;

import com.example.demo.base.BaseResponse;
import com.example.demo.enums.PaymentMethod;
import com.example.demo.service.paymentservice.PaymentServiceResolver;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payment")
@Tag(name = "Payment Controller")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentServiceResolver paymentServiceResolver;

    @GetMapping("/process/{method}")
    public BaseResponse<String> processPayment(@PathVariable PaymentMethod method) {
        String res = paymentServiceResolver.resolve(method).processPayment();
        return new BaseResponse<>(HttpStatus.OK.value(), "Payment processed with " + method, res);
    }

}
