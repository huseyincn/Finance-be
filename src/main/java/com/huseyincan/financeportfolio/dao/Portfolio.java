package com.huseyincan.financeportfolio.dao;

import com.huseyincan.financeportfolio.dto.SymbolPrice;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document("Portfolio")
public class Portfolio {
    @Id
    private String id;
    private List<SymbolPrice> elements;

    public Portfolio(List<SymbolPrice> elements) {
        this.id = UUID.randomUUID().toString();
        this.elements = elements;
    }
}
