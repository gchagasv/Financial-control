package com.financial.control.marketdata;

public interface MarketDataProvider {
    MarketQuote getQuote(String ticker);
}
