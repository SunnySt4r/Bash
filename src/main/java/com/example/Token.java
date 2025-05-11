package com.example;

public class Token {
    private final String value;
    private final QuoteType quoteType;

    public enum QuoteType {
        SINGLE,
        DOUBLE,
        NONE
    }

    public Token(String value, QuoteType quoteType) {
        this.value = value;
        this.quoteType = quoteType;
    }

    public String getValue() {
        return value;
    }

    public QuoteType getQuoteType() {
        return quoteType;
    }
}