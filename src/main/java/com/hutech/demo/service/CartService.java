package com.hutech.demo.service;

import com.hutech.demo.model.CartItem;
import com.hutech.demo.model.Product;
import com.hutech.demo.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.util.ArrayList;
import java.util.List;

@Service
@SessionScope
public class CartService {

    private final ProductRepository productRepository;
    private final List<CartItem> items = new ArrayList<>();

    public CartService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void add(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm"));
        for (CartItem item : items) {
            if (item.getProduct().getId().equals(productId)) {
                item.setQuantity(item.getQuantity() + quantity);
                return;
            }
        }
        items.add(new CartItem(product, Math.max(quantity, 1)));
    }

    public void update(Long productId, int quantity) {
        for (CartItem item : items) {
            if (item.getProduct().getId().equals(productId)) {
                item.setQuantity(Math.max(quantity, 1));
                return;
            }
        }
    }

    public void remove(Long productId) {
        items.removeIf(item -> item.getProduct().getId().equals(productId));
    }

    public int getCartCount() {
        return items.stream().mapToInt(CartItem::getQuantity).sum();
    }

    public double getSubtotal() {
        return items.stream().mapToDouble(CartItem::getTotal).sum();
    }

    public void clear() {
        items.clear();
    }
}
