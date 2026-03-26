package com.hutech.demo.config;

import com.hutech.demo.model.AppUser;
import com.hutech.demo.model.Category;
import com.hutech.demo.model.Product;
import com.hutech.demo.model.RewardVoucher;
import com.hutech.demo.repository.AppUserRepository;
import com.hutech.demo.repository.CategoryRepository;
import com.hutech.demo.repository.ProductRepository;
import com.hutech.demo.repository.RewardVoucherRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedDatabase(CategoryRepository categoryRepository,
                                   ProductRepository productRepository,
                                   AppUserRepository appUserRepository,
                                   RewardVoucherRepository rewardVoucherRepository,
                                   PasswordEncoder passwordEncoder) {
        return args -> {
            List<Category> standardCategories = List.of(
                    new Category("Điện thoại", "bi-phone", 1),
                    new Category("Laptop", "bi-laptop", 2),
                    new Category("Phụ kiện", "bi-headphones", 3),
                    new Category("Smartwatch", "bi-smartwatch", 4),
                    new Category("Tablet", "bi-tablet-landscape", 5),
                    new Category("Đồng hồ", "bi-watch", 6),
                    new Category("Màn hình, Máy in", "bi-display", 7),
                    new Category("Sim, Thẻ cào", "bi-sim", 8),
                    new Category("Dịch vụ tiện ích", "bi-grid-3x3-gap", 9)
            );

            for (Category standard : standardCategories) {
                Category existing = categoryRepository.findAll().stream()
                        .filter(c -> c.getName().equalsIgnoreCase(standard.getName()))
                        .findFirst()
                        .orElse(null);

                if (existing == null) {
                    categoryRepository.save(standard);
                } else {
                    existing.setIconClass(standard.getIconClass());
                    existing.setDisplayOrder(standard.getDisplayOrder());
                    categoryRepository.save(existing);
                }
            }

            if (productRepository.count() == 0) {
                Category phone = categoryRepository.findAll().stream()
                        .filter(c -> c.getName().equalsIgnoreCase("Điện thoại"))
                        .findFirst()
                        .orElseThrow();

                Category laptop = categoryRepository.findAll().stream()
                        .filter(c -> c.getName().equalsIgnoreCase("Laptop"))
                        .findFirst()
                        .orElseThrow();

                Category accessory = categoryRepository.findAll().stream()
                        .filter(c -> c.getName().equalsIgnoreCase("Phụ kiện"))
                        .findFirst()
                        .orElseThrow();

                Category watch = categoryRepository.findAll().stream()
                        .filter(c -> c.getName().equalsIgnoreCase("Đồng hồ"))
                        .findFirst()
                        .orElseThrow();

                Category monitor = categoryRepository.findAll().stream()
                        .filter(c -> c.getName().equalsIgnoreCase("Màn hình, Máy in"))
                        .findFirst()
                        .orElseThrow();

                productRepository.saveAll(List.of(
                        new Product("Samsung Galaxy S24 FE 5G 8GB/256GB", 12790000, 18160000d, "Galaxy AI, hiệu năng mạnh, camera đẹp.", "Mẫu nổi bật cho khu vực khuyến mãi online.", "samsung-galaxy-s24-fe-8gb-256gb-thumb-600x600.jpg", "Samsung", 8, true, phone),
                        new Product("Xiaomi 14T Pro", 15990000, 17990000d, "Hiệu năng mạnh, camera đẹp, sạc nhanh.", "Thiết kế cao cấp, phù hợp block sản phẩm nổi bật.", "xiaomi-14t-pro.png", "Xiaomi", 8, true, phone),
                        new Product("OPPO Reno13 5G", 12200000, 15700000d, "Thiết kế đẹp, chụp ảnh tốt, pin ổn định.", "Mẫu điện thoại nổi bật cho trang chủ demo.", "oppo-reno13-5g.png", "OPPO", 9, true, phone),
                        new Product("vivo V50 Lite 8GB/256GB", 7830000, 8830000d, "Pin khỏe, màu sắc nổi bật, màn hình đẹp.", "Máy tầm trung đẹp mắt cho khu vực flash sale.", "vivo-v50-lite.png", "vivo", 10, true, phone),
                        new Product("realme 14 5G 12GB/256GB", 8800000, 10300000d, "Hiệu năng tốt, thiết kế trẻ trung.", "Sản phẩm điện thoại demo cho block khuyến mãi.", "realme-14-5g.png", "realme", 10, true, phone),
                        new Product("realme 15 5G 12GB/256GB", 9990000, 11490000d, "Điện thoại mới, đẹp, hiệu năng ổn định.", "Mẫu điện thoại bổ sung cho danh sách sản phẩm.", "realme-15-5g.png", "realme", 9, true, phone),
                        new Product("Samsung Galaxy A16 5G 8GB/256GB", 5700000, 6870000d, "Máy phổ thông đẹp, pin ổn, hỗ trợ 5G.", "Sản phẩm điện thoại tầm trung phù hợp trang chủ.", "samsung-a16-5g.png", "Samsung", 14, true, phone),
                        new Product("Xiaomi Redmi Note 14 Pro 5G 12GB/512GB", 7900000, 10800000d, "Màn đẹp, hiệu năng tốt, pin bền.", "Mẫu điện thoại Xiaomi nổi bật cho giao diện TGDD.", "xiaomi-redmi-note-14-pro.png", "Xiaomi", 5, true, phone),
                        new Product("Galaxy S26 Ultra", 22990000, 24990000d, "Flagship cao cấp, màn hình lớn, camera mạnh.", "Mẫu flagship demo để làm sản phẩm nổi bật.", "galaxy-s26-ultra.png", "Samsung", 6, true, phone),
                        new Product("MacBook Pro 14 inch Nano M5 16GB/512GB", 41990000, 45690000d, "Laptop cao cấp, chip mạnh, màn hình đẹp.", "Laptop nổi bật trong khu vực khuyến mãi online.", "macbook-pro-14-nano-m5.png", "Apple", 9, true, laptop),
                        new Product("HP 15 fd1063TU Ultra 5 125H", 18590000, 20590000d, "Laptop học tập, văn phòng, cấu hình ổn.", "Mẫu laptop phổ thông đẹp mắt cho giao diện demo.", "hp-15-fd1063tu.png", "HP", 5, true, laptop),
                        new Product("Xiaomi A24i 23.8 inch Full HD", 1990000, 2590000d, "Màn hình viền mỏng, đẹp, phù hợp văn phòng.", "Sản phẩm màn hình cho danh mục hiển thị mở rộng.", "xiaomi-a24i.png", "Xiaomi", 4, true, monitor),
                        new Product("Loa Bluetooth Marshall Kilburn II", 5600000, 8830000d, "Loa đẹp, âm thanh tốt, pin ổn.", "Phụ kiện âm thanh nổi bật đúng phong cách TGDD.", "marshall-kilburn-ii.png", "Marshall", 5, true, accessory),
                        new Product("Camera IP 360 Độ IMOU Ranger Dual Pro", 950000, 1290000d, "Camera quan sát 360 độ, nhỏ gọn, tiện lợi.", "Thiết bị phụ kiện thông minh cho trang chủ demo.", "imou-ranger-dual-pro.png", "IMOU", 14, true, accessory),
                        new Product("ELIO Starlight 33 mm Nữ EL151-02", 583000, 1290000d, "Đồng hồ nữ thời trang, màu sắc đẹp.", "Mẫu đồng hồ giúp giao diện danh mục đa dạng hơn.", "elio-starlight-el151-02.png", "ELIO", 4, true, watch)
                ));
            }

            ensureUser(appUserRepository, passwordEncoder, "Quản trị viên", "admin@tgdd.vn", "0900000001", "admin123", AppUser.ROLE_ADMIN, 500);
            ensureUser(appUserRepository, passwordEncoder, "Quản lý sản phẩm", "manager@tgdd.vn", "0900000002", "manager123", AppUser.ROLE_MANAGER, 250);
            ensureUser(appUserRepository, passwordEncoder, "Khách hàng demo", "khach@tgdd.vn", "0900000003", "123456", AppUser.ROLE_USER, 180);

            ensureVoucher(rewardVoucherRepository, "VC50K", "Voucher giảm 50.000đ", 50, 50000d, "Dùng 50 điểm để đổi voucher giảm 50.000đ", 300000d, false);
            ensureVoucher(rewardVoucherRepository, "VC100K", "Voucher giảm 100.000đ", 100, 100000d, "Dùng 100 điểm để đổi voucher giảm 100.000đ", 700000d, false);
            ensureVoucher(rewardVoucherRepository, "VC200K", "Voucher giảm 200.000đ", 180, 200000d, "Dùng 180 điểm để đổi voucher giảm 200.000đ", 1200000d, false);
        };
    }

    private void ensureUser(AppUserRepository appUserRepository,
                            PasswordEncoder passwordEncoder,
                            String fullName,
                            String email,
                            String phone,
                            String rawPassword,
                            String role,
                            int points) {

        AppUser user = appUserRepository.findByEmailIgnoreCase(email).orElseGet(AppUser::new);

        user.setFullName(fullName);
        user.setEmail(email);
        user.setPhone(phone);
        user.setRole(role);
        user.setPoints(points);
        user.setEnabled(true);

        if (user.getPassword() == null || user.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(rawPassword));
        } else if (!user.getPassword().startsWith("$2a$")
                && !user.getPassword().startsWith("$2b$")
                && !user.getPassword().startsWith("$2y$")) {
            user.setPassword(passwordEncoder.encode(rawPassword));
        }

        appUserRepository.save(user);
    }

    private void ensureVoucher(RewardVoucherRepository rewardVoucherRepository,
                               String code,
                               String name,
                               int pointsRequired,
                               double discountAmount,
                               String description,
                               Double minOrderAmount,
                               boolean reusable) {

        RewardVoucher voucher = rewardVoucherRepository.findAll().stream()
                .filter(v -> v.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElseGet(RewardVoucher::new);

        voucher.setCode(code);
        voucher.setName(name);
        voucher.setPointsRequired(pointsRequired);
        voucher.setDiscountAmount(discountAmount);
        voucher.setDescription(description);
        voucher.setMinOrderAmount(minOrderAmount);
        voucher.setReusable(reusable);
        voucher.setActive(true);

        rewardVoucherRepository.save(voucher);
    }
}
