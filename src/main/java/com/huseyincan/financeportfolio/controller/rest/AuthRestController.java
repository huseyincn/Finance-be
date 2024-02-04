package com.huseyincan.financeportfolio.controller.rest;

import com.huseyincan.financeportfolio.dao.User;
import com.huseyincan.financeportfolio.exception.EmailExistsException;
import com.huseyincan.financeportfolio.controller.request.UserLoginRequest;
import com.huseyincan.financeportfolio.controller.response.ErrorResponse;
import com.huseyincan.financeportfolio.controller.response.LoginResponse;
import com.huseyincan.financeportfolio.service.TokenManager;
import com.huseyincan.financeportfolio.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
public class AuthRestController {

    private UserService userService;
    private AuthenticationManager authenticationManager;
    private TokenManager tokenManager;

    @Autowired
    public AuthRestController(UserService userService, AuthenticationManager authenticationManager, TokenManager jwtUtil) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.tokenManager = jwtUtil;
    }

    @PostMapping(value = "/login")
    public ResponseEntity login(@ModelAttribute UserLoginRequest loginReq) {
        try {
            Authentication authentication =
                    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginReq.getEmail(), loginReq.getPassword()));
            String email = authentication.getName();
            User user = new User(email, "");
            String token = tokenManager.generateToken(user.getEmail());
            LoginResponse loginRes = new LoginResponse(email, token);
            return ResponseEntity.ok(loginRes);

        } catch (BadCredentialsException e) {
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, "Invalid username or password");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @PostMapping("register")
    public String register(@RequestParam String email, @RequestParam String password) {
        try {
            userService.createUser(email, password);
            return "Hello Admin";
        } catch (EmailExistsException e) {
            return "failed";
        }
    }
}
