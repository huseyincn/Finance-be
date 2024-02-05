package com.huseyincan.financeportfolio.controller.rest;

import com.huseyincan.financeportfolio.exception.EmailExistsException;
import com.huseyincan.financeportfolio.service.auth.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("public")
public class HelloRestController {

    @Autowired
    UserService userService;

    @GetMapping("fetch")
    public String helloUser(@RequestParam String email) {
        return userService.getUserForEmail(email);
    }

    @GetMapping("create")
    public String helloAdmin(@RequestParam String email, @RequestParam String password) {
        try {
            userService.createUser(email,password);
            return "Hello Admin";
        } catch (EmailExistsException e) {
            return "failed";
        }
    }
}
