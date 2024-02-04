package com.huseyincan.financeportfolio.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomepageController {
    @GetMapping(value = "/")
    public String welcome() {
        return "index";
    }
}
