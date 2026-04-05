package com.example.bkpaymenttest.controller.page;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainPageController {

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("pageTitle", "BK Payment Test");
        return "index";
    }
}
