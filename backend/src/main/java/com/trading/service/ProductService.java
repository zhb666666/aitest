package com.trading.service;

import com.trading.entity.Product;
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
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String PRODUCT_CACHE_KEY = "product:";
    private static final String PRODUCT_LIST_CACHE_KEY = "product:list:";

    public Product createProduct(Product product) {
        if (product.getStatus() == null) {
            product.setStatus(1);
        }
        Product saved = productRepository.save(product);
        cacheProduct(saved);
        return saved;
    }

    public Product updateProduct(Product product) {
        Product saved = productRepository.save(product);
        cacheProduct(saved);
        return saved;
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
        redisTemplate.delete(PRODUCT_CACHE_KEY + id);
    }

    public Optional<Product> getProductById(Long id) {
        String cacheKey = PRODUCT_CACHE_KEY + id;
        Product product = (Product) redisTemplate.opsForValue().get(cacheKey);
        if (product != null) {
            return Optional.of(product);
        }
        Optional<Product> opt = productRepository.findById(id);
        opt.ifPresent(this::cacheProduct);
        return opt;
    }

    public List<Product> getProductsByStatus(Integer status) {
        String cacheKey = PRODUCT_LIST_CACHE_KEY + status;
        @SuppressWarnings("unchecked")
        List<Product> products = (List<Product>) redisTemplate.opsForValue().get(cacheKey);
        if (products != null) {
            return products;
        }
        products = productRepository.findByStatusOrderByCreateTimeDesc(status);
        redisTemplate.opsForValue().set(cacheKey, products, 30, TimeUnit.MINUTES);
        return products;
    }

    public List<Product> getProductsBySeller(Long sellerId) {
        return productRepository.findBySellerIdAndStatus(sellerId, 1);
    }

    public List<Product> searchProducts(String keyword) {
        return productRepository.searchProducts(keyword);
    }

    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategoryAndStatus(category, 1);
    }

    private void cacheProduct(Product product) {
        String cacheKey = PRODUCT_CACHE_KEY + product.getId();
        redisTemplate.opsForValue().set(cacheKey, product, 1, TimeUnit.HOURS);
    }
}

