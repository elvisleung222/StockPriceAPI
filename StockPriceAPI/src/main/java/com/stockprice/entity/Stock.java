package com.stockprice.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
public class Stock {
    @Id
    @JsonIgnore
    @ApiModelProperty(value = "The system generated id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ApiModelProperty(value = "The stock code on exchange")
    @Column(nullable = false, unique = true)
    private String symbol;
}
