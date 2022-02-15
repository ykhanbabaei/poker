package com.workwise.assignment.poker.model;

public class Card {
    private final CardSuit suit;
    private final CardValue value;

    public Card(CardValue value, CardSuit suit) {
        this.suit = suit;
        this.value = value;
    }

    public CardSuit getSuit() {
        return suit;
    }

    public CardValue getValue() {
        return value;
    }
}
