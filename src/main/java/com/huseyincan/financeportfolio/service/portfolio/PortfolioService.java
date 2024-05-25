package com.huseyincan.financeportfolio.service.portfolio;

import com.huseyincan.financeportfolio.dao.Portfolio;
import com.huseyincan.financeportfolio.dao.User;
import com.huseyincan.financeportfolio.dto.PortfolioDto;
import com.huseyincan.financeportfolio.dto.SymbolPrice;
import com.huseyincan.financeportfolio.mapper.PortfolioMapper;
import com.huseyincan.financeportfolio.repository.PortfolioRepository;
import com.huseyincan.financeportfolio.repository.UserRepository;
import com.huseyincan.financeportfolio.service.jwt.TokenManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PortfolioService {

    private PortfolioRepository repository;
    private UserRepository userRepository;

    @Autowired
    public PortfolioService(PortfolioRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    public void savePortfolio(List<SymbolPrice> symbols, String email) {
        User user = userRepository.findItemByEmail(email);
        repository.insert(new Portfolio(symbols, user.getEmail(), 0d));
    }

    public List<PortfolioDto> fetchPortfolios() {
        return PortfolioMapper.maptoDtoList(repository.findAllByOrderByRevenueDesc());
    }

    /**
     * Username must be correct here only internal call don't expose to controllers if it is control it
     * @param username
     * @return
     */
    public List<PortfolioDto> fetchPortfoliosOfUser(String username) {
        return PortfolioMapper.maptoDtoList(repository.findByUsername(username));
    }

}
