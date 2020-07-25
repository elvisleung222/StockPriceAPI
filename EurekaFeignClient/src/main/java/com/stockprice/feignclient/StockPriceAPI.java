package com.stockprice.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("stock-price-service")
public interface StockPriceAPI {
    @RequestMapping("/health")
    String health();
}
