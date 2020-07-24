package com.stockprice.service;

import com.stockprice.entity.Stock;
import com.stockprice.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StockService {
    @Autowired
    private StockRepository stockRepository;

    public Stock getStock(long id) {
        Stock stock = stockRepository.findById(id).get();
        if (stock == null)
            throw new RuntimeException("Can't find stock by id " + id);
        return stock;
    }

    public Stock getStock(String symbol) {
        Stock stock = stockRepository.findBySymbol(symbol);
        if (stock == null)
            throw new RuntimeException("Can't find stock by symbol " + symbol);
        return stock;
    }
}
