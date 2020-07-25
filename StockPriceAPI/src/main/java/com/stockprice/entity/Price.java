package com.stockprice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Indexed;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import java.util.Date;

@Getter
@Setter
@Entity
@Indexed
@Table(indexes = {@Index(columnList = "stock_id,date")}) // TODO: evaluate whether need it or not, given PK exists
public class Price {
    @JsonIgnore
    @EmbeddedId
    final PriceId priceId = new PriceId();
    private double open;
    private double high;
    private double low;
    private double close;
    private long volume;

    public Date getDate() {
        return priceId.getDate();
    }

    public void setDate(Date date) {
        priceId.setDate(date);
    }

    public void setStock(Stock stock) {
        priceId.setStock(stock);
    }
}
