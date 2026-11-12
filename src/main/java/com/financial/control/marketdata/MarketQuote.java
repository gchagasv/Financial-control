package com.financial.control.marketdata;

import java.math.BigDecimal;

public record MarketQuote(
        String ticker,
        String name,
        BigDecimal price
) {
}
