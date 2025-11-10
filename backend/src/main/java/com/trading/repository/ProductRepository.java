package com.trading.repository;

import com.trading.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByStatusOrderByCreateTimeDesc(Integer status);
    List<Product> findBySellerIdAndStatus(Long sellerId, Integer status);
    List<Product> findByCategoryAndStatus(String category, Integer status);
    
    @Query("SELECT p FROM Product p WHERE p.status = 1 AND (p.productName LIKE %:keyword% OR p.description LIKE %:keyword%)")
    List<Product> searchProducts(@Param("keyword") String keyword);
}

