package com.stockprice.dto;

import com.stockprice.entity.Price;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class StockPriceDTO {
    private String symbol;
    private List<Price> historicalPrices;
}
