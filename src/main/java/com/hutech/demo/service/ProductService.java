package com.hutech.demo.service;

import com.hutech.demo.model.Product;
import com.hutech.demo.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getFeaturedProducts() {
        return productRepository.findTop10ByFeaturedTrueOrderByIdDesc();
    }

    public List<Product> getLatestProducts() {
        return productRepository.findTop6ByOrderByIdDesc();
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public List<Product> getByCategory(Long categoryId) {
        return productRepository.findByCategoryIdOrderByIdDesc(categoryId);
    }

    public Optional<Product> getById(Long id) {
        return productRepository.findById(id);
    }

    public List<Product> search(String keyword) {
        return productRepository.findByNameContainingIgnoreCaseOrderByIdDesc(keyword);
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public void delete(Long id) {
        productRepository.deleteById(id);
    }
}
