package com.huseyincan.financeportfolio.controller.rest.user;

import com.huseyincan.financeportfolio.controller.response.BaseResponse;
import com.huseyincan.financeportfolio.controller.response.ErrorResponse;
import com.huseyincan.financeportfolio.controller.response.SuccessfulResponse;
import com.huseyincan.financeportfolio.controller.response.UserResponse;
import com.huseyincan.financeportfolio.dao.User;
import com.huseyincan.financeportfolio.service.jwt.TokenManager;
import com.huseyincan.financeportfolio.service.portfolio.PortfolioService;
import com.huseyincan.financeportfolio.service.user.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("user")
public class UserController {

    private UserInfoService userInfoService;
    private TokenManager tokenManager;
    private PortfolioService portfolioService;

    @Autowired
    public UserController(UserInfoService userInfoService, TokenManager tokenManager, PortfolioService portfolioService) {
        this.userInfoService = userInfoService;
        this.tokenManager = tokenManager;
        this.portfolioService = portfolioService;
    }

    @GetMapping("fetch")
    public ResponseEntity<UserResponse> fetchUserInfo(@RequestHeader(value = "Authorization") String authHeader) {
        UserResponse userResponse = new UserResponse();
        String token = authHeader.substring(7);
        String email = tokenManager.getUserFromToken(token);
        User a = userInfoService.fetchUserData(email);
        userResponse.setUser(a);
        userResponse.setPortfolios(portfolioService.fetchPortfoliosOfUser(email));
        return ResponseEntity.ok(userResponse);
    }

    @PostMapping("savePhoto")
    public ResponseEntity<BaseResponse> savePhoto(@RequestHeader(value = "Authorization") String authHeader, @RequestBody MultipartFile file) {
        String token = authHeader.substring(7);
        String email = tokenManager.getUserFromToken(token);
        try {
            userInfoService.saveUserImageToMongo(email, file);
            return ResponseEntity.ok(new SuccessfulResponse(HttpStatus.OK, "File saved successfully"));
        } catch (IOException e) {
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, "Photo cannot be saved");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
}
