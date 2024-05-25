package com.huseyincan.financeportfolio.mapper;

import com.huseyincan.financeportfolio.dao.Portfolio;
import com.huseyincan.financeportfolio.dto.PortfolioDto;

import java.util.ArrayList;
import java.util.List;

public class PortfolioMapper {

    private PortfolioMapper() {

    }

    public static PortfolioDto mapToDto(Portfolio portfolio) {
        PortfolioDto response = new PortfolioDto();
        response.setElements(portfolio.getElements());
        response.setUserName(portfolio.getUsername());
        response.setRevenue(portfolio.getRevenue());
        return response;
    }

    public static List<PortfolioDto> maptoDtoList(List<Portfolio> liste) {
        List<PortfolioDto> tmpList = new ArrayList<>();
        for (Portfolio portfolio : liste) {
            tmpList.add(mapToDto(portfolio));
        }
        return tmpList;
    }

}
