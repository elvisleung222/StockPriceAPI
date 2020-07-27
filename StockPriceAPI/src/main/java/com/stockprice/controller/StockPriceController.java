package com.stockprice.controller;

import com.stockprice.dto.PriceSaveResultDTO;
import com.stockprice.dto.StockPriceDTO;
import com.stockprice.entity.Price;
import com.stockprice.entity.Stock;
import com.stockprice.service.PriceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;


@Api(tags = "Stock Price APIs")
@RestController
public class StockPriceController {
    // TODO: put the number of thread to properties file
    private final ExecutorService executor = Executors.newFixedThreadPool(5);
    @Autowired
    private PriceService priceService;

    @ApiOperation(value = "Check the health status of the api server", response = String.class)
    @GetMapping(value = "/health")
    public String health() {
        return "Server is running......";
    }

    @ApiOperation(value = "Retrieve prices for a list of stocks in a given date range",
            response = StockPriceDTO.class, responseContainer = "List")
    @GetMapping(path = "/prices", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<StockPriceDTO>> getPrices(
            @RequestParam(required = true) String symbols,
            @RequestParam(required = true) @DateTimeFormat(pattern = "yyyy-MM-dd") Date from,
            @RequestParam(required = true) @DateTimeFormat(pattern = "yyyy-MM-dd") Date to) {
        final List<String> symbolList = Arrays.stream(symbols.split(","))
                .map(String::trim)
                .map(String::toUpperCase)
                .collect(Collectors.toList());
        final List<StockPriceDTO> stockPriceDTOList = new ArrayList<>();

        CompletionService<StockPriceDTO> completionService =
                new ExecutorCompletionService<>(executor);
        for (String symbol : symbolList) {
            completionService.submit(() -> {
                List<Price> prices = priceService.getPrices(symbol, from, to);
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
        return ResponseEntity.ok(stockPriceDTOList);
    }

    @ApiOperation(value = "Save/update prices for a list of stocks",
            response = PriceSaveResultDTO.class, responseContainer = "List")
    @PostMapping(path = "/prices", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PriceSaveResultDTO>> savePrices(@RequestBody @Valid List<StockPriceDTO> inputs) {
        List<PriceSaveResultDTO> responseDTOS = new ArrayList<>();
        CompletionService<PriceSaveResultDTO> completionService = new ExecutorCompletionService<>(executor);
        for (StockPriceDTO input : inputs) {
            completionService.submit(() -> {
                long count = priceService.savePrices(input.getSymbol().toUpperCase(), input.getHistoricalPrices());
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

    @ApiOperation(value = "Delete multiple stocks", response = Stock.class, responseContainer = "List")
    @DeleteMapping(path = "/stocks", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Stock>> deleteStock(@RequestParam String symbols) {
        List<String> symbolList = Arrays.stream(symbols.split(",")).map(String::trim).map(String::toUpperCase).collect(Collectors.toList());
        List<Stock> result = new ArrayList<>();

        CompletionService<Stock> completionService = new ExecutorCompletionService<>(executor);

        for (String symbol : symbolList) {
            try {
                completionService.submit(() -> priceService.deletePrices(symbol));

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
