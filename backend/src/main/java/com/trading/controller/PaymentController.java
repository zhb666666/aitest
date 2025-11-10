package com.trading.controller;

import com.trading.common.Result;
import com.trading.entity.Payment;
import com.trading.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping
    public Result<Payment> createPayment(@RequestBody Map<String, Object> request) {
        Long orderId = Long.valueOf(request.get("orderId").toString());
        String paymentMethod = (String) request.get("paymentMethod");
        try {
            return Result.success(paymentService.createPayment(orderId, paymentMethod));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/process")
    public Result<Payment> processPayment(@RequestBody Map<String, String> request) {
        String paymentNo = request.get("paymentNo");
        String transactionId = request.get("transactionId");
        try {
            return Result.success(paymentService.processPayment(paymentNo, transactionId));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/{paymentNo}")
    public Result<Payment> getPaymentByNo(@PathVariable String paymentNo) {
        return paymentService.getPaymentByNo(paymentNo)
                .map(Result::success)
                .orElse(Result.error("支付记录不存在"));
    }

    @GetMapping("/order/{orderId}")
    public Result<Payment> getPaymentByOrderId(@PathVariable Long orderId) {
        return paymentService.getPaymentByOrderId(orderId)
                .map(Result::success)
                .orElse(Result.error("支付记录不存在"));
    }
}

