package com.trading.controller;

import com.trading.common.Result;
import com.trading.entity.Order;
import com.trading.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private com.trading.service.UserService userService;

    @GetMapping
    public Result<List<Order>> getUserOrders(Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        return Result.success(orderService.getUserOrders(userId));
    }

    @GetMapping("/{id}")
    public Result<Order> getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id)
                .map(Result::success)
                .orElse(Result.error("订单不存在"));
    }

    @PostMapping
    public Result<Order> createOrder(@RequestBody Map<String, Object> request, Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        @SuppressWarnings("unchecked")
        List<Integer> cartItemIds = (List<Integer>) request.get("cartItemIds");
        List<Long> cartItemIdList = cartItemIds.stream().map(Integer::longValue).collect(java.util.stream.Collectors.toList());
        String address = (String) request.get("address");
        String receiver = (String) request.get("receiver");
        String phone = (String) request.get("phone");
        try {
            return Result.success(orderService.createOrder(userId, cartItemIdList, address, receiver, phone));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{id}/cancel")
    public Result<Void> cancelOrder(@PathVariable Long id) {
        orderService.cancelOrder(id);
        return Result.success(null);
    }

    private Long getCurrentUserId(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userService.getUserByUsername(userDetails.getUsername())
                .map(com.trading.entity.User::getId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
    }
}

