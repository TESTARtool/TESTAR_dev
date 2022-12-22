package com.example;

import java.math.BigDecimal;

public interface OrderFactory {
    Order createOrder(List<OrderLine> orderLines, BigDecimal amount);
}