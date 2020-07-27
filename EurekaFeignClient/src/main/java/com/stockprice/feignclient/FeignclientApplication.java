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

    @GetMapping(path = "/multi-threaded/health")
    public String health_m() {
        return stockPriceAPI.health();
    }

    @GetMapping(path = "/multi-threaded/prices")
    public ResponseEntity<List<StockPriceDTO>> getPrices_m(
            @RequestParam String symbols,
            @RequestParam String from,
            @RequestParam String to) {
        return stockPriceAPI.getPrices_m(symbols, from, to);
    }

    @PostMapping(path = "/multi-threaded/prices")
    public ResponseEntity<List<PriceSaveResultDTO>> savePrices_m(@RequestBody @Valid List<StockPriceDTO> inputs) {
        return stockPriceAPI.savePrices_m(inputs);
    }

    @DeleteMapping(path = "/multi-threaded/stocks")
    public ResponseEntity<List<Stock>> deleteStock_m(@RequestParam String symbols) {
        return stockPriceAPI.deleteStock_m(symbols);
    }
}
