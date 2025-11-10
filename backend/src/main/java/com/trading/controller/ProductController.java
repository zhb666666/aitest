package com.trading.controller;

import com.trading.common.Result;
import com.trading.entity.Product;
import com.trading.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public Result<List<Product>> getProducts(@RequestParam(required = false) Integer status) {
        if (status != null) {
            return Result.success(productService.getProductsByStatus(status));
        }
        return Result.success(productService.getProductsByStatus(1));
    }

    @GetMapping("/{id}")
    public Result<Product> getProductById(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(Result::success)
                .orElse(Result.error("商品不存在"));
    }

    @GetMapping("/search")
    public Result<List<Product>> searchProducts(@RequestParam String keyword) {
        return Result.success(productService.searchProducts(keyword));
    }

    @GetMapping("/category/{category}")
    public Result<List<Product>> getProductsByCategory(@PathVariable String category) {
        return Result.success(productService.getProductsByCategory(category));
    }

    @GetMapping("/seller/{sellerId}")
    @PreAuthorize("hasAuthority('product:list')")
    public Result<List<Product>> getProductsBySeller(@PathVariable Long sellerId) {
        return Result.success(productService.getProductsBySeller(sellerId));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('product:add')")
    public Result<Product> createProduct(@RequestBody Product product) {
        try {
            return Result.success(productService.createProduct(product));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('product:edit')")
    public Result<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        product.setId(id);
        return Result.success(productService.updateProduct(product));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('product:delete')")
    public Result<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return Result.success(null);
    }
}

