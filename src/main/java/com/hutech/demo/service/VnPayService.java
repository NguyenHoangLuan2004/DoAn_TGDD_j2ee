package com.hutech.demo.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class VnPayService {

    @Value("${vnpay.tmn-code:}")
    private String tmnCode;

    @Value("${vnpay.hash-secret:}")
    private String hashSecret;

    @Value("${vnpay.pay-url:https://sandbox.vnpayment.vn/paymentv2/vpcpay.html}")
    private String payUrl;

    @Value("${vnpay.return-url:http://localhost:8080/payment/vnpay-return}")
    private String returnUrl;

    @Value("${vnpay.order-type:other}")
    private String orderType;

    @Value("${vnpay.locale:vn}")
    private String locale;

    @Value("${vnpay.version:2.1.0}")
    private String version;

    @Value("${vnpay.command:pay}")
    private String command;

    @Value("${vnpay.curr-code:VND}")
    private String currCode;

    public String createPaymentUrl(String orderCode, long amountVnd, HttpServletRequest request) {
        if (tmnCode == null || tmnCode.isBlank() || hashSecret == null || hashSecret.isBlank()) {
            throw new IllegalStateException("Thiếu cấu hình VNPay trong application.properties");
        }

        TimeZone timeZone = TimeZone.getTimeZone("Etc/GMT+7");
        Calendar calendar = Calendar.getInstance(timeZone);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        formatter.setTimeZone(timeZone);
        String createDate = formatter.format(calendar.getTime());

        calendar.add(Calendar.MINUTE, 15);
        String expireDate = formatter.format(calendar.getTime());

        Map<String, String> params = new TreeMap<>();
        params.put("vnp_Version", version);
        params.put("vnp_Command", command);
        params.put("vnp_TmnCode", tmnCode);
        params.put("vnp_Amount", String.valueOf(amountVnd * 100));
        params.put("vnp_CurrCode", currCode);
        params.put("vnp_TxnRef", orderCode);
        params.put("vnp_OrderInfo", "Thanh toan don hang " + orderCode);
        params.put("vnp_OrderType", orderType);
        params.put("vnp_Locale", locale);
        params.put("vnp_ReturnUrl", returnUrl);
        params.put("vnp_IpAddr", getClientIp(request));
        params.put("vnp_CreateDate", createDate);
        params.put("vnp_ExpireDate", expireDate);

        String query = buildQuery(params, false);
        String hashData = buildQuery(params, true);
        String secureHash = hmacSha512(hashSecret, hashData);

        return payUrl + "?" + query + "&vnp_SecureHash=" + secureHash;
    }

    public boolean validateSignature(Map<String, String[]> requestParams) {
        if (hashSecret == null || hashSecret.isBlank()) {
            return false;
        }

        String receivedHash = getSingle(requestParams, "vnp_SecureHash");
        if (receivedHash == null || receivedHash.isBlank()) {
            return false;
        }

        Map<String, String> fields = new TreeMap<>();
        for (Map.Entry<String, String[]> entry : requestParams.entrySet()) {
            String key = entry.getKey();
            if (key == null || key.isBlank()) {
                continue;
            }
            if ("vnp_SecureHash".equals(key) || "vnp_SecureHashType".equals(key)) {
                continue;
            }

            String value = getSingle(requestParams, key);
            if (value != null && !value.isBlank()) {
                fields.put(key, value);
            }
        }

        String signData = buildQuery(fields, true);
        String expectedHash = hmacSha512(hashSecret, signData);
        return expectedHash.equalsIgnoreCase(receivedHash);
    }

    public boolean isPaymentSuccess(HttpServletRequest request) {
        String responseCode = request.getParameter("vnp_ResponseCode");
        String transactionStatus = request.getParameter("vnp_TransactionStatus");
        return "00".equals(responseCode) && "00".equals(transactionStatus);
    }

    public String resolveReturnMessage(HttpServletRequest request) {
        String responseCode = request.getParameter("vnp_ResponseCode");
        if ("00".equals(responseCode)) {
            return "Thanh toán VNPay thành công.";
        }
        if ("24".equals(responseCode)) {
            return "Bạn đã hủy giao dịch thanh toán.";
        }
        if ("51".equals(responseCode)) {
            return "Tài khoản không đủ số dư để thực hiện giao dịch.";
        }
        if ("65".equals(responseCode)) {
            return "Tài khoản của bạn đã vượt quá hạn mức giao dịch trong ngày.";
        }
        if ("75".equals(responseCode)) {
            return "Ngân hàng thanh toán đang bảo trì.";
        }
        return "Thanh toán VNPay chưa thành công hoặc bị từ chối.";
    }

    public String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isBlank()) {
            return ip.split(",")[0].trim();
        }
        ip = request.getHeader("X-Real-IP");
        if (ip != null && !ip.isBlank()) {
            return ip.trim();
        }
        return Optional.ofNullable(request.getRemoteAddr()).orElse("127.0.0.1");
    }

    private String getSingle(Map<String, String[]> params, String key) {
        String[] values = params.get(key);
        if (values == null || values.length == 0) {
            return null;
        }
        return values[0];
    }

    private String buildQuery(Map<String, String> params, boolean encodeValueOnly) {
        StringBuilder builder = new StringBuilder();
        Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            String key = urlEncode(entry.getKey());
            String value = urlEncode(entry.getValue());

            if (encodeValueOnly) {
                builder.append(entry.getKey()).append("=").append(value);
            } else {
                builder.append(key).append("=").append(value);
            }

            if (iterator.hasNext()) {
                builder.append("&");
            }
        }

        return builder.toString();
    }

    private String urlEncode(String value) {
        return URLEncoder.encode(value, StandardCharsets.US_ASCII);
    }

    private String hmacSha512(String key, String data) {
        try {
            Mac hmac = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            hmac.init(secretKeySpec);
            byte[] bytes = hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));

            StringBuilder result = new StringBuilder(bytes.length * 2);
            for (byte b : bytes) {
                result.append(String.format("%02x", b & 0xff));
            }
            return result.toString();
        } catch (Exception e) {
            throw new RuntimeException("Không tạo được chữ ký VNPay", e);
        }
    }
}
