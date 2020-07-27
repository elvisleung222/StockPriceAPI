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
    private PriceRepository priceRepository;

    @Autowired
    StockService stockService;

    /**
     * Retrieve prices of a stock
     *
     * @param symbol
     * @param from
     * @param to
     * @return
     */
    public List<Price> getPrices(String symbol, Date from, Date to) {
        Stock stock = stockService.getStock(symbol);
        List<Price> prices = priceRepository.findByPriceIdStockIdAndPriceIdDateBetween(stock.getId(), from, to);
        return prices;
    }

    /**
     * Insert / update prices for a stock
     *
     * @param symbol
     * @param prices
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public long savePrices(String symbol, List<Price> prices) {
        Stock stock = stockService.getOrCreateStock(symbol);

        for (Price price : prices) {
            price.getPriceId().setStock(stock);
        }

        try {
            /** Atomic operation **/
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
    public Stock deletePrices(String symbol) {
        Stock stock = stockService.getStock(symbol);
        priceRepository.deleteByPriceIdStockId(stock.getId());
        return stockService.deleteStock(stock);
    }
}
