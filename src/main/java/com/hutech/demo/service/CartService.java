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
        Product product = getRequiredProduct(productId);
        int safeQuantity = Math.max(quantity, 1);
        int stock = getSafeStock(product);

        if (stock <= 0) {
            throw new IllegalArgumentException("Sản phẩm hiện đã hết hàng");
        }

        for (CartItem item : items) {
            if (item.getProduct().getId().equals(productId)) {
                int nextQuantity = item.getQuantity() + safeQuantity;
                if (nextQuantity > stock) {
                    throw new IllegalArgumentException("Số lượng vượt quá tồn kho hiện tại");
                }
                item.setQuantity(nextQuantity);
                item.setProduct(product);
                return;
            }
        }

        if (safeQuantity > stock) {
            throw new IllegalArgumentException("Số lượng vượt quá tồn kho hiện tại");
        }

        items.add(new CartItem(product, safeQuantity));
    }

    public void update(Long productId, int quantity) {
        Product product = getRequiredProduct(productId);
        int stock = getSafeStock(product);
        int safeQuantity = Math.max(quantity, 1);

        if (stock <= 0) {
            remove(productId);
            throw new IllegalArgumentException("Sản phẩm hiện đã hết hàng");
        }

        if (safeQuantity > stock) {
            throw new IllegalArgumentException("Số lượng vượt quá tồn kho hiện tại");
        }

        for (CartItem item : items) {
            if (item.getProduct().getId().equals(productId)) {
                item.setProduct(product);
                item.setQuantity(safeQuantity);
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

    public int getTotalQuantity() {
        return getCartCount();
    }

    public double getSubtotal() {
        refreshProductInfo();
        return items.stream().mapToDouble(CartItem::getTotal).sum();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public boolean containsProduct(Long productId) {
        return items.stream().anyMatch(item -> item.getProduct().getId().equals(productId));
    }

    public CartItem getItem(Long productId) {
        return items.stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);
    }

    public void validateCartStock() {
        refreshProductInfo();
        for (CartItem item : items) {
            int stock = getSafeStock(item.getProduct());
            if (stock <= 0) {
                throw new IllegalArgumentException("Sản phẩm '" + item.getProduct().getName() + "' hiện đã hết hàng");
            }
            if (item.getQuantity() > stock) {
                throw new IllegalArgumentException("Sản phẩm '" + item.getProduct().getName() + "' chỉ còn " + stock + " sản phẩm");
            }
        }
    }

    public void refreshProductInfo() {
        for (CartItem item : items) {
            Product latest = getRequiredProduct(item.getProduct().getId());
            item.setProduct(latest);
        }
    }

    public void clear() {
        items.clear();
    }

    private Product getRequiredProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm"));
    }

    private int getSafeStock(Product product) {
        return Math.max(product.getStock() == null ? 0 : product.getStock(), 0);
    }
}
