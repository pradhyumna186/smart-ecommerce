package com.ecommerce.product.controller;

import com.ecommerce.product.model.Product;
import com.ecommerce.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ProductController {
    @Autowired
    ProductService productService;
    
    @GetMapping("products")
    public List<Product> getProducts(){
        return productService.getAllProducts();
    }

    @GetMapping("products/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable String id){
        return productService.getProductById(id)
                .map(product->ResponseEntity.ok(product))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("products")
    public ResponseEntity<Product> createProduct(@RequestBody Product product){
        Product createdProduct = productService.createProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }
    
    @PutMapping("products/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable String id, @RequestBody Product product){
        try {
            Product updatedProduct = productService.updateProduct(id, product);
            return ResponseEntity.ok(updatedProduct);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id){
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("products/category/{category}")
    public List<Product> getProductsByCategory(@PathVariable String category){
        return productService.getProductsByCategory(category);
    }
    
    @GetMapping("products/search")
    public List<Product> searchProductsByName(@RequestParam String name){
        return productService.getProductsByName(name);
    }
    
    @GetMapping("products/price-range")
    public List<Product> getProductsByPriceRange(@RequestParam BigDecimal min, @RequestParam BigDecimal max){
        return productService.getProductsByPriceRange(min, max);
    }
    
    @GetMapping("products/in-stock")
    public List<Product> getProductsInStock(){
        return productService.getProductsInStock();
    }
}
