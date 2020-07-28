package com.stockprice.feignclient.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * Mapping class of Stock Price API
 */
@Getter
@Setter
public class Stock {
    private long id;
    private String symbol;
}
