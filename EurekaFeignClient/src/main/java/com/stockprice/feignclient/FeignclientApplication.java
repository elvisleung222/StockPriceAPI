package com.stockprice.feignclient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableFeignClients
@RestController
public class FeignclientApplication {
    @Autowired
    StockPriceAPI stockPriceAPI;

    public static void main(String[] args) {
        SpringApplication.run(FeignclientApplication.class, args);
    }

    @GetMapping(path = "/health")
    public String health() {
        return stockPriceAPI.health();
    }
}
