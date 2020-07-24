package com.stockprice.service;

import com.stockprice.entity.Price;
import com.stockprice.entity.Stock;
import com.stockprice.repository.PriceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class PriceService {
    @Autowired
    PriceRepository priceRepository;

    @Autowired
    StockService stockService;

    public List<Price> getHistoricalPrices(String symbol, Date from, Date to) {
        Stock stock = stockService.getStock(symbol);
        List<Price> prices = priceRepository.findByStockIdAndDateBetween(stock.getId(), from, to);
        return prices;
    }
}
