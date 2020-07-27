package com.stockprice.controller;

import com.stockprice.dto.PriceSaveResultDTO;
import com.stockprice.dto.StockPriceDTO;
import com.stockprice.entity.Price;
import com.stockprice.entity.Stock;
import com.stockprice.service.PriceService;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/multi-threaded")
public class StockPriceMultiThreadedController {
    private final ExecutorService executor = Executors.newFixedThreadPool(2);
    @Autowired
    private PriceService priceService;

    @GetMapping(value = "/health")
    public String health() {
        return "Server is running......";
    }


    @GetMapping(path = "/prices")
    public ResponseEntity<List<StockPriceDTO>> getPrices(
            @RequestParam String symbols,
            @RequestParam String from,
            @RequestParam String to) {
        Date started = new Date();
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

        CompletionService<StockPriceDTO> completionService =
                new ExecutorCompletionService<>(executor);
        for (String symbol : symbolList) {
            Date finalFromDate = fromDate;
            Date finalToDate = toDate;
            completionService.submit(() -> {
                List<Price> prices = priceService.getHistoricalPrices(symbol, finalFromDate, finalToDate);
                StockPriceDTO dto = StockPriceDTO.builder().symbol(symbol).historicalPrices(prices).build();
                return dto;
            });
        }

        for (String symbol : symbolList) {
            try {
                StockPriceDTO resultDto = completionService.take().get();
                stockPriceDTOList.add(resultDto);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Time collapsed: " + ((new Date()).getTime() - started.getTime()));
        return ResponseEntity.ok(stockPriceDTOList);
    }

    @PostMapping(path = "/prices")
    public ResponseEntity<List<PriceSaveResultDTO>> savePrices(@RequestBody @Valid List<StockPriceDTO> inputs) {
        List<PriceSaveResultDTO> responseDTOS = new ArrayList<>();
        CompletionService<PriceSaveResultDTO> completionService = new ExecutorCompletionService<>(executor);
        for (StockPriceDTO input : inputs) {
            completionService.submit(() -> {
                long count = priceService.saveHistoricalPrices(input.getSymbol().toUpperCase(), input.getHistoricalPrices());
                PriceSaveResultDTO result = new PriceSaveResultDTO();
                result.setSymbol(input.getSymbol().toUpperCase());
                result.setSuccessCount(count);
                return result;
            });
        }

        for (StockPriceDTO input : inputs) {
            try {
                PriceSaveResultDTO dto = completionService.take().get();
                responseDTOS.add(dto);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        return ResponseEntity.ok(responseDTOS);
    }

    @DeleteMapping(path = "/stocks")
    public ResponseEntity<List<Stock>> deleteStock(@RequestParam String symbols) {
        List<String> symbolList = Arrays.stream(symbols.split(",")).map(String::trim).map(String::toUpperCase).collect(Collectors.toList());
        List<Stock> result = new ArrayList<>();

        CompletionService<Stock> completionService = new ExecutorCompletionService<>(executor);

        for (String symbol : symbolList) {
            try {
                completionService.submit(() -> priceService.deleteHistoricalPrices(symbol));

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        for (String symbol : symbolList) {
            try {
                Stock stock = completionService.take().get();
                result.add(stock);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        return ResponseEntity.ok(result);
    }

}
