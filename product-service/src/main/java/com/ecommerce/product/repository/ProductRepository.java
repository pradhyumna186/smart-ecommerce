package com.ecommerce.product.repository;

import com.ecommerce.product.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.math.BigDecimal;
import java.util.List;

    public interface ProductRepository extends MongoRepository<Product, String> {
        List<Product> findByCategory(String category);
        List<Product> findByNameContainingIgnoreCase(String name);
        List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
        List<Product> findByStockQuantityGreaterThan(Integer quantity);



}
