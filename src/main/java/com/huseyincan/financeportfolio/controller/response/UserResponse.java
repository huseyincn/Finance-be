package com.huseyincan.financeportfolio.controller.response;

import com.huseyincan.financeportfolio.dao.User;
import com.huseyincan.financeportfolio.dto.PortfolioDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private User user;
    private List<PortfolioDto> portfolios;
}
