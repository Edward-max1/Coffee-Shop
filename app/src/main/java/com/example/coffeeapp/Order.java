package com.example.coffeeapp;

import java.util.Map;

public class Order {
    public String orderId;
    public String username;
    public String customerName;
    public String date;
    public int coffeeQuantity;
    public Map<String, Integer> toppings;
    public int totalPrice;
    public String paymentMethod;
    public long timestamp;

    public Order() {
        }

    public Order(String orderId, String username, String customerName, String date, int coffeeQuantity, Map<String, Integer> toppings, int totalPrice, String paymentMethod, long timestamp) {
        this.orderId = orderId;
        this.username = username;
        this.customerName = customerName;
        this.date = date;
        this.coffeeQuantity = coffeeQuantity;
        this.toppings = toppings;
        this.totalPrice = totalPrice;
        this.paymentMethod = paymentMethod;
        this.timestamp = timestamp;
    }
}
