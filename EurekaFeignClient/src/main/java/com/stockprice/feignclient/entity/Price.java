package com.stockprice.feignclient.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class Price {
    private PriceId priceId = new PriceId();
    private double open;
    private double high;
    private double low;
    private double close;
    private long volume;

    public Date getDate() {
        return priceId.getDate();
    }

    public void setDate(Date date) {
        priceId.setDate(date);
    }

    public void setStock(Stock stock) {
        priceId.setStock(stock);
    }
}
