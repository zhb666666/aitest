package com.trading.controller;

import com.trading.common.Result;
import com.trading.entity.CartItem;
import com.trading.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private com.trading.service.UserService userService;

    @GetMapping
    public Result<List<CartItem>> getCartItems(Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        return Result.success(cartService.getCartItems(userId));
    }

    @PostMapping("/add")
    public Result<CartItem> addToCart(@RequestBody Map<String, Object> request, Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        Long productId = Long.valueOf(request.get("productId").toString());
        Integer quantity = Integer.valueOf(request.get("quantity").toString());
        try {
            return Result.success(cartService.addToCart(userId, productId, quantity));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Result<Void> updateCartItem(@PathVariable Long id, @RequestBody Map<String, Integer> request, Authentication authentication) {
        try {
            cartService.updateCartItem(id, request.get("quantity"));
            return Result.success(null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result<Void> removeCartItem(@PathVariable Long id, Authentication authentication) {
        cartService.removeCartItem(id);
        return Result.success(null);
    }

    @DeleteMapping("/clear")
    public Result<Void> clearCart(Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        cartService.clearCart(userId);
        return Result.success(null);
    }

    private Long getCurrentUserId(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userService.getUserByUsername(userDetails.getUsername())
                .map(com.trading.entity.User::getId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
    }
}

