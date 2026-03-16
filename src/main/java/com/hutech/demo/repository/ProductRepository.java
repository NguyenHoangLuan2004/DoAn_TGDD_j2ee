package com.hutech.demo.repository;

import com.hutech.demo.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategoryIdOrderByIdDesc(Long categoryId);
    List<Product> findTop10ByFeaturedTrueOrderByIdDesc();
    List<Product> findTop6ByOrderByIdDesc();
    List<Product> findByNameContainingIgnoreCaseOrderByIdDesc(String keyword);
}
