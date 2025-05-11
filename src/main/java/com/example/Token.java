package com.example;

public record Token(String value, com.example.Token.QuoteType quoteType) {
    public enum QuoteType {
        SINGLE,
        DOUBLE,
        NONE
    }

}
