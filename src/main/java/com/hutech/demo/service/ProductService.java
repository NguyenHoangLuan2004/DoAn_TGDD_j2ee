package com.hutech.demo.service;

import com.hutech.demo.model.Product;
import com.hutech.demo.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public Product getRequiredById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm"));
    }

    public List<Product> search(String keyword) {
        return productRepository.findByNameContainingIgnoreCaseOrderByIdDesc(keyword);
    }

    public Product save(Product product) {
        normalizeStock(product);
        return productRepository.save(product);
    }

    @Transactional
    public Product reduceStock(Long productId, int quantity) {
        Product product = getRequiredById(productId);
        int safeQuantity = Math.max(quantity, 0);
        int currentStock = getSafeStock(product);
        if (safeQuantity > currentStock) {
            throw new IllegalArgumentException("Tồn kho không đủ cho sản phẩm: " + product.getName());
        }
        product.setStock(currentStock - safeQuantity);
        return productRepository.save(product);
    }

    @Transactional
    public Product increaseStock(Long productId, int quantity) {
        Product product = getRequiredById(productId);
        int safeQuantity = Math.max(quantity, 0);
        product.setStock(getSafeStock(product) + safeQuantity);
        return productRepository.save(product);
    }

    public boolean hasEnoughStock(Long productId, int quantity) {
        Product product = getRequiredById(productId);
        return getSafeStock(product) >= Math.max(quantity, 0);
    }

    public boolean isAvailable(Long productId) {
        Product product = getRequiredById(productId);
        return getSafeStock(product) > 0;
    }

    public void delete(Long id) {
        productRepository.deleteById(id);
    }

    public void deleteAll() {
        productRepository.deleteAll();
    }

    private void normalizeStock(Product product) {
        product.setStock(getSafeStock(product));
    }

    private int getSafeStock(Product product) {
        return Math.max(product.getStock() == null ? 0 : product.getStock(), 0);
    }

    // ===== Thêm 3 hàm này để khớp code API của thầy =====

    public Optional<Product> getProductById(Long id) {
        return getById(id);
    }

    public Product addProduct(Product product) {
        return save(product);
    }

    public void deleteProductById(Long id) {
        delete(id);
    }
}