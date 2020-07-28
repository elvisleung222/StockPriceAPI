package com.stockprice.service;

import com.stockprice.entity.Stock;
import com.stockprice.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StockService {
    @Autowired
    private StockRepository stockRepository;

    /**
     * Retrieve a stock by its symbol
     *
     * @param symbol
     * @return
     */
    protected Stock getStock(final String symbol) {
        final Stock stock = stockRepository.findBySymbol(symbol);
        if (stock == null)
            throw new RuntimeException("Can't find stock by symbol " + symbol);
        return stock;
    }

    /**
     * Retrieve a stock by its symbol. If does not exist, then create a new stock
     *
     * @param symbol
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    protected Stock getOrCreateStock(final String symbol) {
        try {
            return getStock(symbol);
        } catch (RuntimeException e) {
            Stock stock = new Stock();
            stock.setSymbol(symbol);
            return stockRepository.save(stock);
        }
    }

    /**
     * Delete a stock
     *
     * @param stock
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    protected Stock deleteStock(final Stock stock) {
        stockRepository.delete(stock);
        return stock;
    }
}
