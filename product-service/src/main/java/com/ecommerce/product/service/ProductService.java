package com.ecommerce.product.service;

import com.ecommerce.product.model.Product;
import com.ecommerce.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    @Autowired
    ProductRepository productRepository;

    public List<Product> getAllProducts(){

        return productRepository.findAll();
    };
    public Optional<Product> getProductById(String id){

        return productRepository.findById(id);
    };

    public Product createProduct(Product product){
        return productRepository.save(product);
    };
    public Product updateProduct(String id, Product productDetails){
        return productRepository.findById(id)
                .map(existingProduct -> {
                    // Update fields if they're not null
                    if (productDetails.getName() != null) {
                        existingProduct.setName(productDetails.getName());
                    }
                    if (productDetails.getDescription() != null) {
                        existingProduct.setDescription(productDetails.getDescription());
                    }
                    if (productDetails.getPrice() != null) {
                        existingProduct.setPrice(productDetails.getPrice());
                    }
                    if (productDetails.getCategory() != null) {
                        existingProduct.setCategory(productDetails.getCategory());
                    }
                    if (productDetails.getStockQuantity() != null) {
                        existingProduct.setStockQuantity(productDetails.getStockQuantity());
                    }
                    if (productDetails.getImageUrl() != null) {
                        existingProduct.setImageUrl(productDetails.getImageUrl());
                    }

                    return productRepository.save(existingProduct);
                })
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }
    public void deleteProduct(String id){
        productRepository.deleteById(id);
    };
    public List<Product> getProductsByCategory(String category){

        return productRepository.findByCategory(category);
    };
    public List<Product> getProductsByName(String name){

        return productRepository.findByNameContainingIgnoreCase(name);
    };
    public List<Product> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice){

        return productRepository.findByPriceBetween(minPrice, maxPrice);
    };
    public List<Product> getProductsInStock(){

        return productRepository.findByStockQuantityGreaterThan(0);
    };





}
