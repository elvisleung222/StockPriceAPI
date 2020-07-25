package com.stockprice.controller;

import com.stockprice.dto.PriceSaveResultDTO;
import com.stockprice.dto.StockPriceDTO;
import com.stockprice.entity.Price;
import com.stockprice.entity.Stock;
import com.stockprice.service.PriceService;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@RestController
public class StockPriceController {
    private final ExecutorService executor = Executors.newFixedThreadPool(4);
    @Autowired
    private PriceService priceService;

    @Value("${eureka.instance.instanceId}")
    private String instanceId;

    @GetMapping(value = "/health")
    public String health() {
        return "Instance \"" + instanceId + "\" is running......";
    }


    @GetMapping(path = "/historical-prices")
    public ResponseEntity<List<StockPriceDTO>> getHistoricalPrices(
            @RequestParam String symbols,
            @RequestParam String from,
            @RequestParam String to) {
        final List<String> symbolList = Arrays.stream(symbols.split(","))
                .map(String::trim)
                .map(String::toUpperCase)
                .collect(Collectors.toList());
        final List<StockPriceDTO> stockPriceDTOList = new ArrayList<>();
        Date fromDate = null;
        Date toDate = null;
        try {
            fromDate = DateUtils.parseDate(from, new String[]{"yyyy-MM-dd"});
            toDate = DateUtils.parseDate(to, new String[]{"yyyy-MM-dd"});
        } catch (ParseException e) {
        }

        for (String symbol : symbolList) {
            Date finalFromDate = fromDate;
            Date finalToDate = toDate;
            List<Price> prices = priceService.getHistoricalPrices(symbol, finalFromDate, finalToDate);
            StockPriceDTO dto = StockPriceDTO.builder().symbol(symbol).historicalPrices(prices).build();
            stockPriceDTOList.add(dto);
        }
        return ResponseEntity.ok(stockPriceDTOList);
    }

    @PostMapping(path = "/historical-prices")
    public ResponseEntity<List<PriceSaveResultDTO>> saveHistoricalPrices(@RequestBody @Valid List<StockPriceDTO> inputs) {
        List<PriceSaveResultDTO> responseDTOS = new ArrayList<>();
        for (StockPriceDTO input : inputs) {

            long count = priceService.saveHistoricalPrices(input.getSymbol().toUpperCase(), input.getHistoricalPrices());
            PriceSaveResultDTO result = new PriceSaveResultDTO();
            result.setSymbol(input.getSymbol().toUpperCase());
            result.setSuccessCount(count);
            responseDTOS.add(result);
        }
        return ResponseEntity.ok(responseDTOS);
    }

    @DeleteMapping(path = "/stocks")
    public ResponseEntity<List<Stock>> deleteStock(@RequestParam String symbols) {
        List<String> symbolList = Arrays.stream(symbols.split(",")).map(String::trim).map(String::toUpperCase).collect(Collectors.toList());
        List<Stock> result = new ArrayList<>();

        for (String symbol : symbolList) {
            try {
                result.add(priceService.deleteHistoricalPrices(symbol));

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        return ResponseEntity.ok(result);
    }

}
