package com.trading.controller;

import com.trading.common.Result;
import com.trading.entity.OrderItem;
import com.trading.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders/{orderId}/items")
public class OrderItemController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    public Result<List<OrderItem>> getOrderItems(@PathVariable Long orderId) {
        return Result.success(orderService.getOrderItems(orderId));
    }
}

