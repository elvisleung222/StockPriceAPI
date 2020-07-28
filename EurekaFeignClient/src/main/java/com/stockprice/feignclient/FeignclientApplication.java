package com.stockprice.feignclient;

import com.stockprice.feignclient.dto.PriceSaveResultDTO;
import com.stockprice.feignclient.dto.StockPriceDTO;
import com.stockprice.feignclient.entity.Stock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Load balancing endpoints to stock prices API
 */
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

    @GetMapping(path = "/prices")
    public ResponseEntity<List<StockPriceDTO>> getPrices(
            @RequestParam String symbols,
            @RequestParam String from,
            @RequestParam String to) {
        return stockPriceAPI.getPrices(symbols, from, to);
    }

    @PostMapping(path = "/prices")
    public ResponseEntity<List<PriceSaveResultDTO>> savePrices(@RequestBody @Valid List<StockPriceDTO> inputs) {
        return stockPriceAPI.savePrices(inputs);
    }

    @DeleteMapping(path = "/stocks")
    public ResponseEntity<List<Stock>> deleteStock(@RequestParam String symbols) {
        return stockPriceAPI.deleteStock(symbols);
    }
}
