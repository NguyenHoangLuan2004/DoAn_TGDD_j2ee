package com.hutech.demo.controller;

import com.hutech.demo.model.SinhVien;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class SinhVienController {

    @GetMapping("/sinhvien")
    public String showForm(Model model) {
        model.addAttribute("sinhVien", new SinhVien());
        return "sinhvien/form-sinhvien";
    }

    @PostMapping("/sinhvien")
    public String submitForm(@Valid SinhVien sinhVien,
                             BindingResult result,
                             Model model) {
        if (result.hasErrors()) {
            return "sinhvien/form-sinhvien";
        }
        model.addAttribute("sinhVien", sinhVien);
        model.addAttribute("message", "Sinh viên đã được thêm thành công!");
        return "sinhvien/result-sinhvien";
    }
}
