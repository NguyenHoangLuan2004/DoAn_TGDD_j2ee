package com.hutech.demo.service;

import com.hutech.demo.model.AppUser;
import com.hutech.demo.model.CartItem;
import com.hutech.demo.model.CustomerOrder;
import com.hutech.demo.model.OrderItem;
import com.hutech.demo.model.PointRedemption;
import com.hutech.demo.model.Product;
import com.hutech.demo.repository.AppUserRepository;
import com.hutech.demo.repository.CustomerOrderRepository;
import com.hutech.demo.repository.PointRedemptionRepository;
import com.hutech.demo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final CustomerOrderRepository orderRepository;
    private final AppUserRepository appUserRepository;
    private final ProductRepository productRepository;
    private final PointRedemptionRepository pointRedemptionRepository;

    @Value("${app.reward.points-per-1000-vnd:1}")
    private int pointsPer1000Vnd;

    @Value("${app.shipping.standard-fee:30000}")
    private double standardShippingFee;

    @Value("${app.shipping.free-ship-min-total:1000000}")
    private double freeShipMinTotal;

    @Value("${app.shipping.free-ship-min-items:2}")
    private int freeShipMinItems;

    public OrderService(CustomerOrderRepository orderRepository,
                        AppUserRepository appUserRepository,
                        ProductRepository productRepository,
                        PointRedemptionRepository pointRedemptionRepository) {
        this.orderRepository = orderRepository;
        this.appUserRepository = appUserRepository;
        this.productRepository = productRepository;
        this.pointRedemptionRepository = pointRedemptionRepository;
    }

    @Transactional
    public CustomerOrder placeOrder(AppUser user,
                                    String fullName,
                                    String phone,
                                    String address,
                                    String note,
                                    int pointsToUse,
                                    List<CartItem> cartItems,
                                    double shippingFee) {
        return placeOrder(user, fullName, phone, address, note, pointsToUse, cartItems, shippingFee, null, CustomerOrder.PAYMENT_METHOD_COD);
    }

    @Transactional
    public CustomerOrder placeOrder(AppUser user,
                                    String fullName,
                                    String phone,
                                    String address,
                                    String note,
                                    int pointsToUse,
                                    List<CartItem> cartItems,
                                    double shippingFee,
                                    Long redemptionId) {
        return placeOrder(user, fullName, phone, address, note, pointsToUse, cartItems, shippingFee, redemptionId, CustomerOrder.PAYMENT_METHOD_COD);
    }

    @Transactional
    public CustomerOrder placeOrder(AppUser user,
                                    String fullName,
                                    String phone,
                                    String address,
                                    String note,
                                    int pointsToUse,
                                    List<CartItem> cartItems,
                                    double shippingFee,
                                    Long redemptionId,
                                    String paymentMethod) {
        if (cartItems == null || cartItems.isEmpty()) {
            throw new IllegalArgumentException("Giỏ hàng đang trống");
        }

        List<CartItem> validatedCartItems = new ArrayList<>(cartItems);
        validateStock(validatedCartItems);

        CustomerOrder order = new CustomerOrder();
        order.setFullName(fullName);
        order.setPhone(phone);
        order.setAddress(address);
        order.setNote(note);
        order.setUser(user);
        order.setStatus("Đã tiếp nhận");

        String resolvedPaymentMethod = CustomerOrder.PAYMENT_METHOD_VNPAY.equalsIgnoreCase(paymentMethod)
                ? CustomerOrder.PAYMENT_METHOD_VNPAY
                : CustomerOrder.PAYMENT_METHOD_COD;
        order.setPaymentMethod(resolvedPaymentMethod);
        order.setPaymentStatus(CustomerOrder.PAYMENT_METHOD_VNPAY.equalsIgnoreCase(resolvedPaymentMethod)
                ? CustomerOrder.PAYMENT_STATUS_PENDING
                : CustomerOrder.PAYMENT_STATUS_UNPAID);

        double subtotal = validatedCartItems.stream().mapToDouble(CartItem::getTotal).sum();
        int totalQuantity = validatedCartItems.stream().mapToInt(CartItem::getQuantity).sum();

        double computedShippingFee = shippingFee > 0 ? shippingFee : calculateShippingFee(subtotal, totalQuantity);

        int allowedPoints = user == null ? 0 : Math.min(Math.max(pointsToUse, 0), user.getPoints());
        double pointsDiscount = allowedPoints * 10000d;
        if (pointsDiscount > subtotal) {
            allowedPoints = (int) Math.floor(subtotal / 10000d);
            pointsDiscount = allowedPoints * 10000d;
        }

        PointRedemption redemption = resolveVerifiedRedemption(user, redemptionId);
        double voucherDiscount = redemption == null ? 0d : normalizeMoney(redemption.getDiscountAmount());

        double totalDiscount = pointsDiscount + voucherDiscount;
        double maxDiscount = subtotal + computedShippingFee;
        if (totalDiscount > maxDiscount) {
            double remainingForVoucher = Math.max(maxDiscount - pointsDiscount, 0d);
            voucherDiscount = Math.min(voucherDiscount, remainingForVoucher);
            totalDiscount = pointsDiscount + voucherDiscount;
        }

        double grandTotal = subtotal + computedShippingFee - totalDiscount;
        if (grandTotal < 0) {
            grandTotal = 0;
        }

        order.setSubtotal(subtotal);
        order.setShippingFee(computedShippingFee);
        order.setPointsUsed(allowedPoints);
        order.setDiscountAmount(totalDiscount);
        order.setVoucherDiscount(voucherDiscount);
        order.setTotalQuantity(totalQuantity);
        order.setGrandTotal(grandTotal);
        int pointsEarned = calculateEarnedPoints(grandTotal);
        order.setPointsEarned(pointsEarned);
        order.setOrderCode(generateOrderCode());

        if (redemption != null) {
            order.setVoucherCode(redemption.getVoucherCode());
            order.setVoucherName(redemption.getVoucherName());
        }

        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : validatedCartItems) {
            Product product = productRepository.findById(cartItem.getProduct().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm"));

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProductId(product.getId());
            item.setProductName(product.getName());
            item.setProductBrand(product.getBrand());
            item.setProductImage(product.getImage());
            item.setUnitPrice(product.getPrice());
            item.setQuantity(cartItem.getQuantity());
            item.setLineTotal(cartItem.getQuantity() * product.getPrice());
            orderItems.add(item);

            product.setStock(Math.max(product.getSafeStock() - cartItem.getQuantity(), 0));
            productRepository.save(product);
        }
        order.setItems(orderItems);

        CustomerOrder saved = orderRepository.save(order);

        if (user != null) {
            user.setPoints(Math.max(user.getPoints() - allowedPoints, 0) + pointsEarned);
            appUserRepository.save(user);
        }

        if (redemption != null) {
            redemption.setStatus(PointRedemption.STATUS_USED);
            redemption.setUsedAt(LocalDateTime.now());
            redemption.setUsedOrderCode(saved.getOrderCode());
            pointRedemptionRepository.save(redemption);
        }

        return saved;
    }

    public double calculateShippingFee(double subtotal, int totalQuantity) {
        if (subtotal >= freeShipMinTotal && totalQuantity >= freeShipMinItems) {
            return 0d;
        }
        return Math.max(standardShippingFee, 0d);
    }

    private int calculateEarnedPoints(double grandTotal) {
        int safeRate = Math.max(pointsPer1000Vnd, 0);
        if (safeRate == 0) {
            return 0;
        }
        return (int) Math.floor(grandTotal / 1000d) * safeRate;
    }

    private void validateStock(List<CartItem> cartItems) {
        for (CartItem cartItem : cartItems) {
            Product product = productRepository.findById(cartItem.getProduct().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm"));

            int stock = product.getSafeStock();
            if (stock <= 0) {
                throw new IllegalArgumentException("Sản phẩm '" + product.getName() + "' hiện đã hết hàng");
            }
            if (cartItem.getQuantity() > stock) {
                throw new IllegalArgumentException("Sản phẩm '" + product.getName() + "' chỉ còn " + stock + " sản phẩm");
            }
        }
    }

    private PointRedemption resolveVerifiedRedemption(AppUser user, Long redemptionId) {
        if (user == null || redemptionId == null) {
            return null;
        }

        PointRedemption redemption = pointRedemptionRepository.findById(redemptionId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy voucher đã đổi"));

        if (!user.getId().equals(redemption.getUserId())) {
            throw new IllegalArgumentException("Voucher không thuộc về tài khoản hiện tại");
        }

        if (!PointRedemption.STATUS_VERIFIED.equals(redemption.getStatus())) {
            throw new IllegalArgumentException("Voucher chưa sẵn sàng để sử dụng");
        }

        return redemption;
    }

    private String generateOrderCode() {
        return "TGDD-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddHHmmss"));
    }

    private double normalizeMoney(Double value) {
        return value == null ? 0d : Math.max(value, 0d);
    }
}
