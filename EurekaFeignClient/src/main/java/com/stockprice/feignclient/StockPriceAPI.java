package com.stockprice.feignclient;

import com.stockprice.feignclient.dto.PriceSaveResultDTO;
import com.stockprice.feignclient.dto.StockPriceDTO;
import com.stockprice.feignclient.entity.Stock;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.List;

/**
 * Mapping endpoints to stock price APIs
 */
@FeignClient("stock-price-service")
public interface StockPriceAPI {
    @RequestMapping(method = RequestMethod.GET, value = "/health")
    String health();

    @RequestMapping(method = RequestMethod.GET, value = "/prices")
    ResponseEntity<List<StockPriceDTO>> getPrices(@RequestParam String symbols, @RequestParam String from, @RequestParam String to);

    @RequestMapping(method = RequestMethod.POST, value = "/prices")
    ResponseEntity<List<PriceSaveResultDTO>> savePrices(@RequestBody @Valid List<StockPriceDTO> inputs);

    @RequestMapping(method = RequestMethod.DELETE, value = "/stocks")
    ResponseEntity<List<Stock>> deleteStock(@RequestParam String symbols);
}
