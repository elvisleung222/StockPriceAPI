package com.stockprice.service;

import com.stockprice.entity.Price;
import com.stockprice.entity.Stock;
import com.stockprice.repository.PriceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
        List<Price> prices = priceRepository.findByPriceIdStockIdAndPriceIdDateBetween(stock.getId(), from, to);
        return prices;
    }

    // TODO: java doc
    // TODO: transactionl
    public long saveHistoricalPrices(String symbol, List<Price> prices) {
        Stock stock = stockService.getOrCreateStock(symbol);

        for (Price price : prices) {

            price.getPriceId().setStock(stock);
        }

        try {
            // TODO: explain why go for atomic
            priceRepository.saveAll(prices);
            return prices.size();

        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Delete a stock and its associated historical prices
     *
     * @param symbol
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public Stock deleteHistoricalPrices(String symbol) {
        Stock stock = stockService.getStock(symbol);
        priceRepository.deleteByPriceIdStockId(stock.getId());
        return stockService.deleteStock(stock);
    }
}
