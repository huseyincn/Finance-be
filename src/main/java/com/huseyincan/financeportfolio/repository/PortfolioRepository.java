package com.huseyincan.financeportfolio.repository;

import com.huseyincan.financeportfolio.dao.Portfolio;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface PortfolioRepository extends MongoRepository<Portfolio, String> {
    long count();

    @Query(value = "{username:'?0'}")
    List<Portfolio> findByUsername(String username);

    List<Portfolio> findAllByOrderByRevenueDesc();
}
