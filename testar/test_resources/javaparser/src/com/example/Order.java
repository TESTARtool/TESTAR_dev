package com.example;

import java.math.BigDecimal;

public class Order {
    public Order(List<OrderLine> orderLines, BigDecimal price) {
        this.orderLines = orderLines;
        this.price = price;
    }

    private List<OrderLine> orderLines;
    private BigDecimal price;

}