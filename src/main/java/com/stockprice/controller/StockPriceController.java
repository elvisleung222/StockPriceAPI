package com.stockprice.controller;

import com.stockprice.dto.PriceResponseDTO;
import com.stockprice.entity.Price;
import com.stockprice.service.PriceService;
import com.stockprice.service.StockService;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@RestController
public class StockPriceController {
    private final ExecutorService executor = Executors.newFixedThreadPool(2);
    @Autowired
    private PriceService priceService;
    @Autowired
    private StockService stockService;

    @GetMapping(path = "/historical-prices")
    public ResponseEntity<List<PriceResponseDTO>> getHistoricalPrices(
            @RequestParam String symbols,
            @RequestParam String from,
            @RequestParam String to) {
        Date started = new Date();
        final List<String> symbolList = Arrays.stream(symbols.split(",")).map(String::trim).collect(Collectors.toList());
        final List<PriceResponseDTO> priceResponseDTOList = new ArrayList<>();
        Date fromDate = null;
        Date toDate = null;
        try {
            fromDate = DateUtils.parseDate(from, new String[]{"yyyy-MM-dd"});
            toDate = DateUtils.parseDate(to, new String[]{"yyyy-MM-dd"});
        } catch (ParseException e) {
        }

        CompletionService<PriceResponseDTO> completionService =
                new ExecutorCompletionService<>(executor);
        for (String symbol : symbolList) {
            Date finalFromDate = fromDate;
            Date finalToDate = toDate;
            completionService.submit(() -> {
                List<Price> prices = priceService.getHistoricalPrices(symbol, finalFromDate, finalToDate);
                PriceResponseDTO dto = PriceResponseDTO.builder().symbol(symbol).historicalPrices(prices).build();
                return dto;
            });
        }

        for (String symbol : symbolList) {
            try {
                PriceResponseDTO resultDto = completionService.take().get();
                priceResponseDTOList.add(resultDto);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Time collapsed: " + ((new Date()).getTime() - started.getTime()));
        return ResponseEntity.ok(priceResponseDTOList);
    }
}
