package com.huseyincan.financeportfolio.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SymbolPrice {
    private String symbol;
    private Double price;
    private Integer quantity;
}
