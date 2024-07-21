package rcs.stock.models;

public record StockPrice(
        String symbol,
        String currency,
        double price,
        double percentageChange) { }