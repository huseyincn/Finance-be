package com.huseyincan.financeportfolio.controller.rest.auth;

import com.huseyincan.financeportfolio.controller.request.UserLoginRequest;
import com.huseyincan.financeportfolio.controller.response.BaseResponse;
import com.huseyincan.financeportfolio.controller.response.ErrorResponse;
import com.huseyincan.financeportfolio.controller.response.LoginResponse;
import com.huseyincan.financeportfolio.exception.EmailExistsException;
import com.huseyincan.financeportfolio.service.auth.UserService;
import com.huseyincan.financeportfolio.service.jwt.TokenManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
    public ResponseEntity<BaseResponse> login(@RequestBody UserLoginRequest loginReq) {
        try {
            Authentication authentication =
                    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginReq.getEmail(), loginReq.getPassword()));
            String email = authentication.getName();
            String token = tokenManager.generateToken(email);
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
    public ResponseEntity<BaseResponse> register(@RequestBody UserLoginRequest loginReq) {
        try {
            userService.createUser(loginReq.getEmail(), loginReq.getPassword());
            String token = tokenManager.generateToken(loginReq.getEmail());
            LoginResponse loginRes = new LoginResponse(loginReq.getEmail(), token);
            return ResponseEntity.ok(loginRes);
        } catch (EmailExistsException e) {
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, "This email already exists");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
}
