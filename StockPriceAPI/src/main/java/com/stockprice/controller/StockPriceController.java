package com.stockprice.controller;

import com.stockprice.dto.PriceSaveResultDTO;
import com.stockprice.dto.StockPriceDTO;
import com.stockprice.entity.Price;
import com.stockprice.entity.Stock;
import com.stockprice.service.PriceService;
import com.stockprice.utils.Utils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;


@Api(tags = "Stock Price APIs")
@RestController
public class StockPriceController {
    private final static int NUM_OF_THREADS = 4;

    @Value("${eureka.instance.instanceId}")
    private String instanceId;

    @Autowired
    private PriceService priceService;

    private ExecutorService executor = Executors.newFixedThreadPool(NUM_OF_THREADS);

    /**
     * Return health status of the application
     *
     * @return
     */
    @ApiOperation(value = "Check the health status of the api server", response = String.class)
    @GetMapping(value = "/health")
    public String health() {
        return "Instance \"" + instanceId + "\" is running......";
    }

    /**
     * Retrieve prices for a list of stocks in a given date range
     *
     * @param symbols
     * @param from
     * @param to
     * @return
     */
    @ApiOperation(value = "Retrieve prices for a list of stocks in a given date range",
            response = StockPriceDTO.class, responseContainer = "List")
    @GetMapping(path = "/prices", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<StockPriceDTO>> getPrices(
            @RequestParam(required = true) String symbols,
            @RequestParam(required = true) @DateTimeFormat(pattern = "yyyy-MM-dd") final Date from,
            @RequestParam(required = true) @DateTimeFormat(pattern = "yyyy-MM-dd") final Date to) {

        final List<String> symbolList = Utils.splitStrings(symbols);
        final List<StockPriceDTO> stockPriceDTOList = new ArrayList<>();
        final CompletionService<StockPriceDTO> completionService = new ExecutorCompletionService<>(executor);

        // Split tasks and handle in different threads
        for (String symbol : symbolList) {
            completionService.submit(() -> {
                List<Price> prices = priceService.getPrices(symbol, from, to);
                StockPriceDTO dto = StockPriceDTO.builder().symbol(symbol).prices(prices).build();
                return dto;
            });
        }

        // Collect result from threads until the longest one finished
        for (String symbol : symbolList) {
            try {
                StockPriceDTO resultDto = completionService.take().get();
                stockPriceDTOList.add(resultDto);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace(); // to be handled
            }
        }

        return ResponseEntity.ok(stockPriceDTOList);
    }

    /**
     * Save / update prices for a list of stocks
     *
     * @param inputs
     * @return
     */
    @ApiOperation(value = "Save/update prices for a list of stocks",
            response = PriceSaveResultDTO.class, responseContainer = "List")
    @PostMapping(path = "/prices", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PriceSaveResultDTO>> savePrices(@RequestBody @Valid final List<StockPriceDTO> inputs) {

        final List<PriceSaveResultDTO> responseDTOS = new ArrayList<>();
        final CompletionService<PriceSaveResultDTO> completionService = new ExecutorCompletionService<>(executor);

        // Split tasks and handle in different threads
        for (StockPriceDTO input : inputs) {
            completionService.submit(() -> {
                long count = priceService.savePrices(input.getSymbol().toUpperCase(), input.getPrices());
                PriceSaveResultDTO result = new PriceSaveResultDTO();
                result.setSymbol(input.getSymbol().toUpperCase());
                result.setSuccessCount(count);
                return result;
            });
        }

        // Collect result from threads until the longest one finished
        for (StockPriceDTO input : inputs) {
            try {
                PriceSaveResultDTO dto = completionService.take().get();
                responseDTOS.add(dto);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace(); // to be handled
            }
        }

        return ResponseEntity.ok(responseDTOS);
    }

    /**
     * Delete multiple stocks
     *
     * @param symbols
     * @return
     */
    @ApiOperation(value = "Delete multiple stocks", response = Stock.class, responseContainer = "List")
    @DeleteMapping(path = "/stocks", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Stock>> deleteStock(@RequestParam(required = true) final String symbols) {
        final List<String> symbolList = Utils.splitStrings(symbols);
        final List<Stock> result = new ArrayList<>();
        final CompletionService<Stock> completionService = new ExecutorCompletionService<>(executor);

        // Split tasks and handle in different threads
        for (String symbol : symbolList) {
            try {
                completionService.submit(() -> priceService.deletePrices(symbol));

            } catch (Exception e) {
                e.printStackTrace(); // to be handled
            }
        }

        // Collect result from threads until the longest one finished
        for (String symbol : symbolList) {
            try {
                Stock stock = completionService.take().get();
                result.add(stock);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace(); // to be handled
            }
        }

        return ResponseEntity.ok(result);
    }
}
