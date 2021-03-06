package com.stockprice.repository;

import com.stockprice.entity.Price;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface PriceRepository extends JpaRepository<Price, Long> {
    List<Price> findByPriceIdStockIdAndPriceIdDateBetween(long stockId, Date from, Date to);

    void deleteByPriceIdStockId(long stockId);
}
