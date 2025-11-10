package com.trading.service;

import com.trading.entity.*;
import com.trading.repository.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.trading.config.RabbitMQConfig.ORDER_EXCHANGE;
import static com.trading.config.RabbitMQConfig.ORDER_ROUTING_KEY;

@Service
@Transactional
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public Order createOrder(Long userId, List<Long> cartItemIds, String address, String receiver, String phone) {
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setStatus(0); // 待支付
        order.setAddress(address);
        order.setReceiver(receiver);
        order.setPhone(phone);

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<CartItem> cartItems = cartItemRepository.findAllById(cartItemIds);

        // 验证购物车项并计算总金额
        for (CartItem cartItem : cartItems) {
            if (!cartItem.getUserId().equals(userId)) {
                throw new RuntimeException("购物车项不属于当前用户");
            }

            Optional<Product> productOpt = productRepository.findById(cartItem.getProductId());
            if (!productOpt.isPresent() || productOpt.get().getStatus() != 1) {
                throw new RuntimeException("商品不存在或已下架");
            }

            Product product = productOpt.get();
            if (product.getStock() < cartItem.getQuantity()) {
                throw new RuntimeException("商品库存不足: " + product.getProductName());
            }

            BigDecimal subtotal = product.getPrice().multiply(new BigDecimal(cartItem.getQuantity()));
            totalAmount = totalAmount.add(subtotal);
        }

        order.setTotalAmount(totalAmount);
        Order savedOrder = orderRepository.save(order);

        // 保存订单项
        for (CartItem cartItem : cartItems) {
            Product product = productRepository.findById(cartItem.getProductId()).get();
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(savedOrder.getId());
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getProductName());
            orderItem.setPrice(product.getPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setSubtotal(product.getPrice().multiply(new BigDecimal(cartItem.getQuantity())));
            orderItemRepository.save(orderItem);
        }

        // 发送订单创建消息到RabbitMQ
        rabbitTemplate.convertAndSend(ORDER_EXCHANGE, ORDER_ROUTING_KEY, savedOrder.getId());

        return savedOrder;
    }

    public List<Order> getUserOrders(Long userId) {
        return orderRepository.findByUserIdOrderByCreateTimeDesc(userId);
    }

    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    public Optional<Order> getOrderByNo(String orderNo) {
        return orderRepository.findByOrderNo(orderNo);
    }

    public void cancelOrder(Long orderId) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            if (order.getStatus() == 0) {
                order.setStatus(4); // 已取消
                orderRepository.save(order);
            }
        }
    }

    private String generateOrderNo() {
        return "ORD" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}

