package com.example;

import java.math.BigDecimal;

public class OrderManager {

    public OrderManager() {
    }

    public Order submitOrder(List<OrderLine> orderLines, BigDecimal amount) {
        var factory = new OrderFactoryImpl();
        return factory.createOrder(orderLines, amount);
    }

    private static class OrderFactoryImpl implements OrderFactory {

        public Order createOrder(List<OrderLine> orderLines, BigDecimal amount) {
            return new Order(orderLines, amount);
        }
    }

    private static class Bill {
        private Bill(String customer, BigDecimal amount) {}

        public static Bill prepareBill(String customer, BigDecimal amount) {
            return new Bill(customer, amount);
        }

        public static Bill prepareBill(BigDecimal amount) {
            return new Bill(null, amount);
        }

        public static Bill prepareBill(String customer, com.example.monetary.BigDecimal amount) {
            return new Bill(customer, amount.toBigDecimal());
        }
    }
}