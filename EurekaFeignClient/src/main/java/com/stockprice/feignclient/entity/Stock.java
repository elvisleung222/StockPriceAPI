package com.stockprice.feignclient.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Stock {
    private long id;
    private String symbol;
}
