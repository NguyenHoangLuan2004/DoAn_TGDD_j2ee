package com.hutech.demo.controller;

import com.hutech.demo.model.CustomerOrder;
import com.hutech.demo.repository.CustomerOrderRepository;
import com.hutech.demo.service.VnPayService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/payment")
public class PaymentController {

    private final VnPayService vnPayService;
    private final CustomerOrderRepository customerOrderRepository;

    public PaymentController(VnPayService vnPayService,
                             CustomerOrderRepository customerOrderRepository) {
        this.vnPayService = vnPayService;
        this.customerOrderRepository = customerOrderRepository;
    }

    @GetMapping("/vnpay/create")
    public String createVnPayPayment(@RequestParam String orderCode,
                                     HttpServletRequest request,
                                     RedirectAttributes redirectAttributes) {
        Optional<CustomerOrder> orderOptional = customerOrderRepository.findByOrderCode(orderCode);
        if (orderOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy đơn hàng để thanh toán.");
            return "redirect:/cart";
        }

        CustomerOrder order = orderOptional.get();
        long amount = Math.round(order.getGrandTotal());
        if (amount <= 0) {
            redirectAttributes.addFlashAttribute("error", "Đơn hàng không hợp lệ để thanh toán VNPay.");
            return "redirect:/cart";
        }

        order.setPaymentMethod(CustomerOrder.PAYMENT_METHOD_VNPAY);
        order.setPaymentStatus(CustomerOrder.PAYMENT_STATUS_PENDING);
        customerOrderRepository.save(order);

        String paymentUrl = vnPayService.createPaymentUrl(order.getOrderCode(), amount, request);
        return "redirect:" + paymentUrl;
    }

    @GetMapping("/vnpay-return")
    public String vnpayReturn(HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
        Map<String, String[]> rawParams = request.getParameterMap();
        String orderCode = request.getParameter("vnp_TxnRef");

        Optional<CustomerOrder> orderOptional = customerOrderRepository.findByOrderCode(orderCode);
        if (orderOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy đơn hàng sau khi VNPay trả về.");
            return "redirect:/";
        }

        CustomerOrder order = orderOptional.get();
        boolean validSignature = vnPayService.validateSignature(rawParams);
        boolean success = validSignature && vnPayService.isPaymentSuccess(request);

        order.setPaymentMethod(CustomerOrder.PAYMENT_METHOD_VNPAY);
        order.setPaymentTransactionNo(request.getParameter("vnp_TransactionNo"));

        if (success) {
            order.setStatus("Đã thanh toán");
            order.setPaymentStatus(CustomerOrder.PAYMENT_STATUS_PAID);
            order.setPaidAt(LocalDateTime.now());
            model.addAttribute("paymentMessage", "Thanh toán VNPay thành công.");
        } else {
            order.setPaymentStatus(CustomerOrder.PAYMENT_STATUS_FAILED);
            model.addAttribute("paymentMessage", vnPayService.resolveReturnMessage(request));
        }

        customerOrderRepository.save(order);

        model.addAttribute("order", order);
        model.addAttribute("isPaymentReturn", true);
        return "cart/order-confirmation";
    }
}
