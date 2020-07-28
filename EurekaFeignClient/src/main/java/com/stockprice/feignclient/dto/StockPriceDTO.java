package com.stockprice.feignclient.dto;

import com.stockprice.feignclient.entity.Price;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Mapping class of Stock Price API
 */
@Getter
@Setter
public class StockPriceDTO {
    private String symbol;
    private List<Price> prices;
}
