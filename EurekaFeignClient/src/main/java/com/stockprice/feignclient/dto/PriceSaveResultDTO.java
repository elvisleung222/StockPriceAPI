package com.stockprice.feignclient.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Mapping class of Stock Price API
 */
@Getter
@Setter
public class PriceSaveResultDTO {
    private String symbol;
    private long successCount;
}
