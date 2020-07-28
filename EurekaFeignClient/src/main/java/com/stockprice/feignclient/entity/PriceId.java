package com.stockprice.feignclient.entity;


import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * Mapping class of Stock Price API
 */
@Getter
@Setter
public class PriceId implements Serializable {
    private Stock stock;
    private Date date;
}
