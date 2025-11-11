package com.trading.service;

import com.trading.entity.Order;
import com.trading.entity.Payment;
import com.trading.entity.Product;
import com.trading.repository.OrderRepository;
import com.trading.repository.PaymentRepository;
import com.trading.repository.ProductRepository;
import com.trading.repository.OrderItemRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static com.trading.config.RabbitMQConfig.PAYMENT_EXCHANGE;
import static com.trading.config.RabbitMQConfig.PAYMENT_ROUTING_KEY;

@Service
@Transactional
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public Payment createPayment(Long orderId, String paymentMethod) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (!orderOpt.isPresent()) {
            throw new RuntimeException("订单不存在");
        }

        Order order = orderOpt.get();
        if (order.getStatus() != 0) {
            throw new RuntimeException("订单状态不正确");
        }

        Payment payment = new Payment();
        payment.setPaymentNo(generatePaymentNo());
        payment.setOrderId(orderId);
        payment.setUserId(order.getUserId());
        payment.setAmount(order.getTotalAmount());
        payment.setPaymentMethod(paymentMethod);
        payment.setStatus(0); // 待支付

        return paymentRepository.save(payment);
    }

    public Payment processPayment(String paymentNo, String transactionId) {
        Optional<Payment> paymentOpt = paymentRepository.findByPaymentNo(paymentNo);
        if (!paymentOpt.isPresent()) {
            throw new RuntimeException("支付记录不存在");
        }

        Payment payment = paymentOpt.get();
        if (payment.getStatus() != 0) {
            throw new RuntimeException("支付状态不正确");
        }

        payment.setStatus(1); // 已支付
        payment.setTransactionId(transactionId);
        Payment savedPayment = paymentRepository.save(payment);

        // 更新订单状态
        Optional<Order> orderOpt = orderRepository.findById(payment.getOrderId());
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            order.setStatus(1); // 已支付
            orderRepository.save(order);

            // 扣减库存
            orderItemRepository.findByOrderId(order.getId()).forEach(orderItem -> {
                Optional<Product> productOpt = productRepository.findById(orderItem.getProductId());
                if (productOpt.isPresent()) {
                    Product product = productOpt.get();
                    product.setStock(product.getStock() - orderItem.getQuantity());
                    product.setSales(product.getSales() + orderItem.getQuantity());
                    productRepository.save(product);
                }
            });
        }

        // 发送支付成功消息到RabbitMQ
        try {
            rabbitTemplate.convertAndSend(PAYMENT_EXCHANGE, PAYMENT_ROUTING_KEY, savedPayment.getId());
        } catch (Exception e) {
            // 如果RabbitMQ不可用，记录日志但不影响支付处理
            System.err.println("RabbitMQ消息发送失败: " + e.getMessage());
        }

        return savedPayment;
    }

    public Optional<Payment> getPaymentByNo(String paymentNo) {
        return paymentRepository.findByPaymentNo(paymentNo);
    }

    public Optional<Payment> getPaymentByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId);
    }

    private String generatePaymentNo() {
        return "PAY" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}

