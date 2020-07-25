package com.stockprice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class StockpriceApplication {

    public static void main(String[] args) {
        SpringApplication.run(StockpriceApplication.class, args);
    }

}
