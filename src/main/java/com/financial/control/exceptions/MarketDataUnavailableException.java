package com.financial.control.exceptions;

public class MarketDataUnavailableException extends RuntimeException {
    public MarketDataUnavailableException(String message) {
        super(message);
    }
}
