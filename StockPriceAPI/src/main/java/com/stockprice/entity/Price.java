package com.stockprice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
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
@Table(indexes = {@Index(columnList = "stock_id,date")})
public class Price {
    @ApiModelProperty(value = "Composite primary key of a price object")
    @JsonIgnore
    @EmbeddedId
    final PriceId priceId = new PriceId();

    @ApiModelProperty(value = "The opening price of the day")
    private double open;

    @ApiModelProperty(value = "The highest price of the day")
    private double high;

    @ApiModelProperty(value = "The lowest price of the day")
    private double low;

    @ApiModelProperty(value = "The closing price of the day")
    private double close;

    @ApiModelProperty(value = "The trading volume of the day")
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
