package com.workwise.assignment.poker;

import com.workwise.assignment.poker.model.WinType;

public class WinnerException extends Throwable{
    private final int winner;
    private final WinType winType;

    public WinnerException(int winner, WinType winType) {
        this.winner = winner;
        this.winType = winType;
    }

    public int getWinner() {
        return winner;
    }

    public WinType getType() {
        return winType;
    }
}
