package com.hutech.demo.service;

import com.hutech.demo.model.*;
import com.hutech.demo.repository.AppUserRepository;
import com.hutech.demo.repository.CustomerOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final CustomerOrderRepository orderRepository;
    private final AppUserRepository appUserRepository;

    public OrderService(CustomerOrderRepository orderRepository, AppUserRepository appUserRepository) {
        this.orderRepository = orderRepository;
        this.appUserRepository = appUserRepository;
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
        CustomerOrder order = new CustomerOrder();
        order.setFullName(fullName);
        order.setPhone(phone);
        order.setAddress(address);
        order.setNote(note);
        order.setUser(user);

        double subtotal = cartItems.stream().mapToDouble(CartItem::getTotal).sum();
        int allowedPoints = user == null ? 0 : Math.min(pointsToUse, user.getPoints());
        double discount = allowedPoints * 10000d;
        if (discount > subtotal) {
            allowedPoints = (int) Math.floor(subtotal / 10000d);
            discount = allowedPoints * 10000d;
        }

        order.setSubtotal(subtotal);
        order.setShippingFee(shippingFee);
        order.setPointsUsed(allowedPoints);
        double grandTotal = subtotal + shippingFee - discount;
        if (grandTotal < 0) {
            grandTotal = 0;
        }
        order.setGrandTotal(grandTotal);
        int pointsEarned = (int) Math.floor(grandTotal / 10000d);
        order.setPointsEarned(pointsEarned);
        order.setOrderCode("TGDD-" + java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddHHmmss")));

        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProductName(cartItem.getProduct().getName());
            item.setProductImage(cartItem.getProduct().getImage());
            item.setUnitPrice(cartItem.getProduct().getPrice());
            item.setQuantity(cartItem.getQuantity());
            item.setLineTotal(cartItem.getTotal());
            orderItems.add(item);
        }
        order.setItems(orderItems);

        CustomerOrder saved = orderRepository.save(order);

        if (user != null) {
            user.setPoints(user.getPoints() - allowedPoints + pointsEarned);
            appUserRepository.save(user);
        }
        return saved;
    }
}
