package com.stockprice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PriceSaveResultDTO {
    private String symbol;
    private long successCount;
}
