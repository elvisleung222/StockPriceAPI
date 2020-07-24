package com.stockprice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Indexed;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@Indexed
@Table(indexes = {@Index(columnList = "stock_id,date", unique = true)})
public class Price {
    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private Date date;
    private double open;
    private double high;
    private double low;
    private double close;
    private long volume;

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id")
    private Stock stock;
}
