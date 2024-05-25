package com.huseyincan.financeportfolio.controller.request;

import com.huseyincan.financeportfolio.dto.SymbolPrice;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioSaveRequest {
    private List<SymbolPrice> elements;
}
