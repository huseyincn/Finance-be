package com.huseyincan.financeportfolio.controller.rest.portfolio;

import com.huseyincan.financeportfolio.controller.request.PortfolioSaveRequest;
import com.huseyincan.financeportfolio.dto.PortfolioDto;
import com.huseyincan.financeportfolio.service.jwt.TokenManager;
import com.huseyincan.financeportfolio.service.portfolio.PortfolioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("portfolio")
public class PortfolioController {

    private PortfolioService portfolioService;
    private TokenManager tokenManager;
    @Autowired
    public PortfolioController(PortfolioService portfolioService, TokenManager tokenManager) {
        this.portfolioService = portfolioService;
        this.tokenManager = tokenManager;
    }

    @GetMapping("fetch")
    public List<PortfolioDto> fetchPortfolios() {
        return portfolioService.fetchPortfolios();
    }

    @PostMapping("save")
    public HttpStatus savePortfolios(@RequestHeader(value = "Authorization") String authHeader, @RequestBody PortfolioSaveRequest saveRequest) {
        String token = authHeader.substring(7);
        String email = tokenManager.getUserFromToken(token);
        portfolioService.savePortfolio(saveRequest.getElements(), email);
        return HttpStatus.OK;
    }
}
