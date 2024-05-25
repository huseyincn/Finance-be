package com.huseyincan.financeportfolio.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioDto {
    private List<SymbolPrice> elements;
    private String userName;
    private double revenue;
}
