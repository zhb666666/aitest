package com.trading.service;

import com.trading.entity.CartItem;
import com.trading.entity.Product;
import com.trading.repository.CartItemRepository;
import com.trading.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
public class CartService {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String CART_CACHE_KEY = "cart:";

    public CartItem addToCart(Long userId, Long productId, Integer quantity) {
        Optional<Product> productOpt = productRepository.findById(productId);
        if (!productOpt.isPresent() || productOpt.get().getStatus() != 1) {
            throw new RuntimeException("商品不存在或已下架");
        }

        Product product = productOpt.get();
        if (product.getStock() < quantity) {
            throw new RuntimeException("库存不足");
        }

        Optional<CartItem> existingItem = cartItemRepository.findByUserIdAndProductId(userId, productId);
        CartItem cartItem;
        if (existingItem.isPresent()) {
            cartItem = existingItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        } else {
            cartItem = new CartItem();
            cartItem.setUserId(userId);
            cartItem.setProductId(productId);
            cartItem.setQuantity(quantity);
        }

        CartItem saved = cartItemRepository.save(cartItem);
        clearCartCache(userId);
        return saved;
    }

    public void updateCartItem(Long cartItemId, Integer quantity) {
        Optional<CartItem> cartItemOpt = cartItemRepository.findById(cartItemId);
        if (!cartItemOpt.isPresent()) {
            throw new RuntimeException("购物车项不存在");
        }

        CartItem cartItem = cartItemOpt.get();
        Optional<Product> productOpt = productRepository.findById(cartItem.getProductId());
        if (!productOpt.isPresent() || productOpt.get().getStock() < quantity) {
            throw new RuntimeException("库存不足");
        }

        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);
        clearCartCache(cartItem.getUserId());
    }

    public void removeCartItem(Long cartItemId) {
        Optional<CartItem> cartItemOpt = cartItemRepository.findById(cartItemId);
        if (cartItemOpt.isPresent()) {
            Long userId = cartItemOpt.get().getUserId();
            cartItemRepository.deleteById(cartItemId);
            clearCartCache(userId);
        }
    }

    public List<CartItem> getCartItems(Long userId) {
        String cacheKey = CART_CACHE_KEY + userId;
        @SuppressWarnings("unchecked")
        List<CartItem> items = (List<CartItem>) redisTemplate.opsForValue().get(cacheKey);
        if (items != null) {
            return items;
        }
        items = cartItemRepository.findByUserId(userId);
        redisTemplate.opsForValue().set(cacheKey, items, 30, TimeUnit.MINUTES);
        return items;
    }

    public void clearCart(Long userId) {
        cartItemRepository.deleteByUserId(userId);
        clearCartCache(userId);
    }

    private void clearCartCache(Long userId) {
        redisTemplate.delete(CART_CACHE_KEY + userId);
    }
}

